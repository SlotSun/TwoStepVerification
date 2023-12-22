package com.slot.twostepverification.ui.nav

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slot.twostepverification.const.LocalConfig
import com.slot.twostepverification.const.locale
import com.slot.twostepverification.help.WebDavHelper
import com.slot.twostepverification.help.store.Backup
import com.slot.twostepverification.utils.AppLog
import com.slot.twostepverification.utils.permission.Permissions
import com.slot.twostepverification.utils.showToasts
import com.slot.twostepverification.utils.coroutine.Coroutine
import com.slot.twostepverification.utils.permission.PermissionsCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import splitties.init.appCtx

data class NavUiState(
    val filePath: Uri? = null,
    val isBackUp: Boolean = false,
    val isSelectFilePath: Boolean = false,
    val isStartBackUp: Boolean = false,
    val isSelectBackUpPath: Boolean = false,
    val isSelectRestoreFileFromWebDav: Boolean = false,
    val webDavFileList: List<String> = listOf(),
    val isShowLoading: Boolean = false
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
     *  选择webdav备份的文件
     */
    fun selectRestoreFileFromWebDav() {
        viewModelScope.launch {
            if (LocalConfig.isWebDavLogin) {
                val names = withContext(Dispatchers.IO) {
                    WebDavHelper.getBackupNames()
                }
                if (WebDavHelper.isJianGuoYun && names.size > 700) {
                    AppLog.put("由于坚果云限制，部分备份可能未显示")
                }
                if (names.isNotEmpty()) {
                    coroutineContext.ensureActive()
                    _uiState.update {
                        it.copy(
                            isSelectRestoreFileFromWebDav = true,
                            webDavFileList = names
                        )
                    }
                } else {
                    AppLog.put("没有备份文件")
                }
            } else {
                AppLog.put("请先登录WEBDAV")
            }
        }

    }

    /**
     *  关闭 webdav_dialog
     */
    fun closeSelectRestoreFileFromWebDavDialog() {
        _uiState.update {
            it.copy(
                isShowLoading = false,
                isSelectRestoreFileFromWebDav = false)
        }
    }

    fun openSelectRestoreFileFromWebDavDialog() {
        _uiState.update {
            it.copy(isSelectRestoreFileFromWebDav = true)
        }
    }

    fun restoreWebDav(name: String) {
        _uiState.update {
            it.copy(isShowLoading = true)
        }
        val task = Coroutine.async {
            WebDavHelper.restoreWebDav(name)
        }.onError {
            AppLog.put("WebDav恢复出错\n${it.localizedMessage}", it)
            appCtx.showToasts("WebDav恢复出错\n${it.localizedMessage}")
        }.onFinally(Dispatchers.Main) {
            closeSelectRestoreFileFromWebDavDialog()
        }
        // 结束 释放
        if (task.isCompleted) {
            task.cancel()
        }
    }

    fun selectRestoreFileFromLocal() {

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
    private fun uploadData() {
        Coroutine.async {
            uiState.value.filePath?.let { uri ->
                Backup.backup(appCtx, uri.toString())
            }
        }.onSuccess {
            appCtx.showToasts(locale("backup_success"))
        }.onError {
            appCtx.showToasts(locale("backup_fail"))
        }
    }

    /**
     *  本地备份
     */
    fun backUp() {
        // 要优先获取到 uri读取权限
        PermissionsCompat.Builder()
            .addPermissions(*Permissions.Group.STORAGE)
            .rationale(locale("tip_perm_request_storage"))
            .onGranted {
                uploadData()
            }
            .request()
    }

    /**
     *  同步云数据
     */
    fun syncWebDav() {
        Coroutine.async {
            WebDavHelper.syncWebDav()
        }.onSuccess {
            appCtx.showToasts(locale("restore_success"))
        }.onError {
            appCtx.showToasts(locale("restore_fail"))
        }
    }

}