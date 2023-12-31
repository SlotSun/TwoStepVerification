package com.slot.twostepverification.const

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.annotation.Keep
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import com.slot.twostepverification.ui.theme.getDefaultThemeId
import com.slot.twostepverification.utils.data.DataStoreUtils
import splitties.init.appCtx


object LocalConfig {
    // 当前文字
    val currentLanguage: MutableState<String> =
        mutableStateOf(DataStoreUtils.getSyncData(LOCALE, "English"))

    // 全局文字
    val localeState: MutableState<Map<String, String>> by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        mutableStateOf(
            locales.getValue(
                currentLanguage.value
            )
        )
    }

    // 主题状态
    val themeTypeState: MutableState<Int> by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        mutableIntStateOf(getDefaultThemeId())
    }

    // 动态颜色
    val dynamicColorState: MutableState<Boolean> by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        mutableStateOf(DataStoreUtils.getSyncData(DYNAMIC_COLOR, true))
    }
    var recordLog
        get() = DataStoreUtils.readBooleanData("recordLog", default = false)
        set(value) {
            DataStoreUtils.saveSyncBooleanData("recordLog", value)
        }

    // 指纹识别
    val securityOpenState: MutableState<Boolean> by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        mutableStateOf(DataStoreUtils.getSyncData(SECURITY_OPEN, false))
    }

    // webdav config
    var user: String
        get() = DataStoreUtils.getSyncData("user", default = "")
        set(value) = DataStoreUtils.putSyncData("user", value = value)
    var password: String
        get() = DataStoreUtils.getSyncData("password", default = "")
        set(value) = DataStoreUtils.putSyncData("password", value = value)
    var webDavUrl: String
        get() = DataStoreUtils.getSyncData("webDavUrl", default = "https://dav.jianguoyun.com/dav/")
        set(value) = DataStoreUtils.putSyncData("webDavUrl", value = value)
    var isWebDavLogin: Boolean
        get() = DataStoreUtils.readBooleanData("authIsOk", default = false)
        set(value) {
            DataStoreUtils.saveSyncBooleanData("authIsOk", value)
        }

    /**
     *  最后一次备份时间
     */
    var lastBackup: Long
        get() = DataStoreUtils.readLongData("lastBackup", default = 0)
        set(value) {
            DataStoreUtils.saveSyncLongData("lastBackup", value)
        }

    /**
     *  是否第一次启动APP
     */
    val isFirstOpenApp: Boolean
        get() {
            val value = DataStoreUtils.readBooleanData("isFirstOpenApp", default = true)
            if (value) {
                DataStoreUtils.saveSyncBooleanData("isFirstOpenApp", false)
            }
            return value
        }


    // 选择备份的文件路径
    var filePath: Uri?
        get() {
            val value = DataStoreUtils.getSyncData("filePath", default = "")
            return Uri.parse(value)
        }
        set(value) {
            val setValue = value?.toString()
            DataStoreUtils.putSyncData("filePath", value = setValue)
        }

    // app版本号
    val appInfo: AppInfo by lazy {
        val appInfo = AppInfo()
        @Suppress("DEPRECATION")
        appCtx.packageManager.getPackageInfo(appCtx.packageName, PackageManager.GET_ACTIVITIES)
            ?.let {
                appInfo.versionName = it.versionName
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    appInfo.versionCode = it.longVersionCode
                } else {
                    @Suppress("DEPRECATION")
                    appInfo.versionCode = it.versionCode.toLong()
                }
            }
        appInfo
    }
    @Keep
    data class AppInfo(
        var versionCode: Long = 0L,
        var versionName: String = ""
    )


    val onlyLatestBackup get() = DataStoreUtils.readBooleanData("onlyLatestBackup", default = false)
}