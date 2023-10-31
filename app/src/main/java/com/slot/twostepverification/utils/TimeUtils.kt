package com.slot.twostepverification.utils

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.PluralsRes
import java.util.Date


object TimeUtils {
    fun getElapsedSince(context: Context, date: Date): String {
        var since = (Date().time - date.time) / 1000
        if (since < 60) {
            return formatElapsedSince(context, since, "seconds")
        }
        since /= 60
        if (since < 60) {
            return formatElapsedSince(context, since, "minutes")
        }
        since /= 60
        if (since < 24) {
            return formatElapsedSince(context, since, "hours")
        }
        since /= 24
        if (since < 365) {
            return formatElapsedSince(context, since, "days")
        }
        since /= 365
        return formatElapsedSince(context, since, "years")
    }

    @SuppressLint("DiscouragedApi")
    private fun formatElapsedSince(context: Context, since: Long, unit: String): String {
        val res = context.resources
        @PluralsRes val id = res.getIdentifier(
            String.format("time_elapsed_%s", unit),
            "plurals",
            context.packageName
        )
        return res.getQuantityString(id, since.toInt(), since.toInt())
    }
}
