package com.slot.twostepverification.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.widget.Toast

fun showToast(context: Context?, text: String?) {
    if (context == null || TextUtils.isEmpty(text)) return
    if (Thread.currentThread() === Looper.getMainLooper().thread) {
        val toast = Toast.makeText(context, text, Toast.LENGTH_SHORT)
        toast.show()
    } else {
        Handler(context.mainLooper).post {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
        }
    }
}