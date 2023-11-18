package com.slot.twostepverification.utils.https

import android.util.Log

object OkhttpUncaughtExceptionHandler : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(t: Thread, e: Throwable) {
        Log.e("Okhttp Dispatcher中的线程执行出错\n${e.localizedMessage}", e.toString())
    }

}
