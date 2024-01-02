package com.slot.twostepverification.ui.nav.webdav

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slot.twostepverification.const.LocalConfig
import com.slot.twostepverification.const.locale
import com.slot.twostepverification.help.TwoHelper
import com.slot.twostepverification.data.entity.Authorization
import com.slot.twostepverification.utils.webdav.WebDav
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
        authIsOK = LocalConfig.isWebDavLogin
        domain = LocalConfig.webDavUrl
        user = LocalConfig.user
        password = LocalConfig.password
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
        if (user.isEmpty()) {
            _webDavUiState.update {
                it.copy(message = locale("account can't be null"))
            }
            return
        }
        if (password.isEmpty()) {
            _webDavUiState.update {
                it.copy(message = locale("password can't be null"))
            }
            return
        }
        if (domain.isEmpty()) {
            _webDavUiState.update {
                it.copy(message = locale( "website can't be empty"))
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
            if (WebDav(auth).check()) {
                //登录成功 添加到数据库
                authIsOK = true
                LocalConfig.password = password
                LocalConfig.user = user
                LocalConfig.webDavUrl = domain
                // 记录是否登录成功
                LocalConfig.isWebDavLogin = authIsOK
                TwoHelper.updateAuth(auth)
                _webDavUiState.update {
                    it.copy(
                        isLogin = authIsOK,
                        isLoading = false,
                        message = locale("login successful")
                    )
                }
            } else {
                // 改动后失败删除
                TwoHelper.delAuth()
                _webDavUiState.update {
                    it.copy(
                        isLogin = authIsOK,
                        isLoading = false,
                        message = locale("login failed")
                    )
                }
            }


        }
        // 跳转回上一页
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