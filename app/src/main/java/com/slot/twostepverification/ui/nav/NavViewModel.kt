package com.slot.twostepverification.ui.nav

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

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

    /**
     *  选择备份路径
     */
    fun selectFilePath(uri: Uri?) {
        _uiState.update {
            it.copy(
                filePath = uri,
                isSelectBackUpPath = true,
                isSelectFilePath = true,
            )
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
    private fun uploadData(): Boolean {
        return true
    }

    /**
     *  本地备份
     */

    fun backUp() {

    }
}