package com.slot.twostepverification.const

import com.slot.twostepverification.utils.data.DataStoreUtils


object LocalConfig {

    /**
     *  最后一次更新时间
     */
    var lastBackup: Long
        get() = DataStoreUtils.readLongData("lastBackup", default = 0)
        set(value) {
            DataStoreUtils.saveSyncLongData("lastBackup", value)
        }

    /**
     *  是否第一次启动APP
     */
    val isFirstOpenApp:Boolean
        get(){
           val value = DataStoreUtils.readBooleanData("isFirstOpenApp", default = true)
            if (value){
                DataStoreUtils.saveSyncBooleanData("isFirstOpenApp", false)
            }
            return value
        }


    var versionCode
        get() = DataStoreUtils.readLongData("versionCode", default = 0)
        set(value) {
            DataStoreUtils.saveSyncLongData("versionCode", value)
        }

}