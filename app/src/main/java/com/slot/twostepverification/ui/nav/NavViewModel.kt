package com.slot.twostepverification.ui.nav

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slot.twostepverification.const.LocalConfig
import com.slot.twostepverification.utils.file.store.Backup
import com.slot.twostepverification.utils.showToasts
import io.legado.app.help.coroutine.Coroutine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import splitties.init.appCtx

data class NavUiState(
    val filePath: Uri? = null,
    val isBackUp: Boolean = false,
    val isSelectFilePath: Boolean = false,
    val isStartBackUp: Boolean = false,
    val isSelectBackUpPath: Boolean = false
)


class NavViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NavUiState())
    val uiState: StateFlow<NavUiState> = _uiState.asStateFlow()

    init {
        _uiState.update {
            it.copy(
                filePath = LocalConfig.filePath
            )
        }
    }
    /**
     *  选择备份路径
     */
    fun selectFilePath(uri: Uri?) {
        // 存进preferences
        uri?.let { it ->
            LocalConfig.filePath = it
            _uiState.update {
                it.copy(
                    filePath = uri,
                    isSelectBackUpPath = true,
                    isSelectFilePath = true,
                )
            }
        }
    }

    /**
     *  检查权限
     */
    private fun checkPermission():Boolean {
        return true
    }

    /**
     *  文件从数据库导出并加密
     */
    private fun exportData() {
        kotlin.runCatching {

        }
    }

    /**
     *  上传到云端
     */
    private fun uploadData(){
        Coroutine.async {
            uiState.value.filePath?.let {uri->
                Backup.backup(appCtx,uri.toString())
            }
        }.onSuccess {
            appCtx.showToasts("备份成功")
        }.onError {
            appCtx.showToasts("备份失败")
        }
    }

    /**
     *  本地备份
     */
    fun backUp() {
        uploadData()
    }
}