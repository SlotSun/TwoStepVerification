package com.slot.twostepverification.ui.nav.webdav

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slot.twostepverification.const.AUTH_IS_OK
import com.slot.twostepverification.data.TwoHelper
import com.slot.twostepverification.data.TwoHelper.getAuth
import com.slot.twostepverification.data.entity.Authorization
import com.slot.twostepverification.data.entity.Two
import com.slot.twostepverification.ui.scan.ScanUIState
import com.slot.twostepverification.utils.data.DataStoreUtils
import com.slot.twostepverification.utils.webdav.WebDav
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

data class WebDavUIState(
    var isLoading: Boolean = false,
    var isLogin: Boolean = false,
    var message: String = "",
)

class WebDavViewModel : ViewModel() {

    private val _webDavUiState = MutableStateFlow(WebDavUIState())
    val uiState: StateFlow<WebDavUIState> = _webDavUiState.asStateFlow()
    private var domain: String = ""
    private var user: String = ""
    private var password = ""
    var authIsOK = false


    init {
        authIsOK = DataStoreUtils.readBooleanData(AUTH_IS_OK)
//        runBlocking {
//            if (authIsOK) {
//                getAuth()?.let {
//                    domain = it.url
//                    user = it.username
//                    password = it.password
//                }
//            }
//        }
    }

    private fun getAuthHandler(): Authorization {
        return Authorization(
            url = domain,
            username = user,
            password = password
        )
    }

    /**
     *  坚果云：https://dav.jianguoyun.com/dav/
     */
    // 调用webdav 登录接口
    fun loginWebDav() {
        if (user.isEmpty()){
            _webDavUiState.update {
                it.copy(message = "用户名不能为空")
            }
            return
        }
        if (password.isEmpty()){
            _webDavUiState.update {
                it.copy(message = "密码不能为空")
            }
            return
        }
        if (domain.isEmpty()){
            _webDavUiState.update {
                it.copy(message = "网络地址不能为空")
            }
            return
        }
        _webDavUiState.update {
            it.copy(
                isLoading = true
            )
        }
        // res = 登录结果
        // if res == true auth 更改为true
        // 将domain user 等存进SP
        viewModelScope.launch {
            val auth = getAuthHandler()
            kotlin.runCatching {
                authIsOK = WebDav(auth).check()

            }.onFailure {
                authIsOK = false
                _webDavUiState.update {
                    it.copy(
                        isLogin = false,
                        isLoading = false,
                        message = "登录 $authIsOK，请重新输入配置"
                    )
                }
                Log.e("WEBDAV_LOGIN", "${it.message}")
            }
            if (authIsOK) {
                // 记录是否登录成功
                DataStoreUtils.putData(AUTH_IS_OK, authIsOK)
                //登录成功 添加到数据库
                TwoHelper.updateAuth(auth)
            }else{
                // 改动后失败删除
                TwoHelper.delAuth()
            }
            _webDavUiState.update {
                it.copy(
                    isLogin = authIsOK,
                    isLoading = false,
                    message = "登录成功"
                )
            }
        }
        // 跳转回上一页
    }

    /**
     *  选择备份路径
     */
    fun selectFilePath(){

    }

    /**
     *  开始备份
     */

    fun backUp(){

    }

    fun updateDomain(domain: String) {
        this.domain = domain
    }

    fun updateUser(user: String) {
        this.user = user
    }

    fun updatePassword(password: String) {
        this.password = password
    }
    fun resetMessage() {
        _webDavUiState.update {
            it.copy(message = "")
        }
    }


    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}