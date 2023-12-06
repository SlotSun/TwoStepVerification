package com.slot.twostepverification.utils

import android.util.Log
import com.slot.twostepverification.BuildConfig
import com.slot.twostepverification.const.LocalConfig
import splitties.init.appCtx

object AppLog {
    private val mLogs = arrayListOf<Triple<Long, String, Throwable?>>()

    val logs get() = mLogs.toList()

    @Synchronized
    fun put(message: String?, throwable: Throwable? = null, toast: Boolean = false) {
        message ?: return
        if (toast) {
            appCtx.showToasts(message)
        }
        if (mLogs.size > 100) {
            mLogs.removeLastOrNull()
        }
        mLogs.add(0, Triple(System.currentTimeMillis(), message, throwable))
        if (BuildConfig.DEBUG) {
            val stackTrace = Thread.currentThread().stackTrace
            Log.e(stackTrace[3].className, message, throwable)
        }
    }

    @Synchronized
    fun clear() {
        mLogs.clear()
    }

    fun putDebug(message: String?, throwable: Throwable? = null) {
        if (LocalConfig.recordLog) {
            put(message, throwable)
        }
    }
}