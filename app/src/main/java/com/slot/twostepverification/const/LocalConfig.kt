package com.slot.twostepverification.const

import android.net.Uri
import androidx.core.net.toUri
import com.slot.twostepverification.utils.data.DataStoreUtils


object LocalConfig {

    var user: String
        get() = DataStoreUtils.getSyncData("user", default = "")
        set(value) = DataStoreUtils.putSyncData("user", value = value)
    var password: String
        get() = DataStoreUtils.getSyncData("password", default = "")
        set(value) = DataStoreUtils.putSyncData("password", value = value)
    var webDavUrl: String
        get() = DataStoreUtils.getSyncData("webDavUrl", default = "https://dav.jianguoyun.com/dav/")
        set(value) = DataStoreUtils.putSyncData("webDavUrl", value = value)

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
    var isWebDavLogin: Boolean
        get() = DataStoreUtils.readBooleanData("authIsOk", default = false)
        set(value) {
            DataStoreUtils.saveSyncBooleanData("authIsOk", value)
        }
    var filePath: Uri?
        get() {
            val value = DataStoreUtils.getSyncData("filePath", default = "")
            return Uri.parse(value)
        }
        set(value) {
            val setValue = value?.toString()
            DataStoreUtils.putSyncData("filePath", value = setValue)
        }
    var versionCode
        get() = DataStoreUtils.readLongData("versionCode", default = 0)
        set(value) {
            DataStoreUtils.saveSyncLongData("versionCode", value)
        }
    val onlyLatestBackup get() = DataStoreUtils.readBooleanData("onlyLatestBackup", default = false)
}