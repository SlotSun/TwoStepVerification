@file:Suppress("unused", "UnusedReceiverParameter")

package com.slot.twostepverification.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.PendingIntent.getActivity
import android.app.PendingIntent.getBroadcast
import android.app.PendingIntent.getService
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.os.BatteryManager
import android.os.Build
import android.os.Process
import android.provider.Settings
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.fragment.app.FragmentActivity
import androidx.preference.PreferenceManager
import com.slot.twostepverification.utils.log.printOnDebug
import splitties.systemservices.clipboardManager
import splitties.systemservices.connectivityManager
import splitties.systemservices.uiModeManager
import java.io.File
import kotlin.system.exitProcess

inline fun <reified A : Activity> Context.startActivity(configIntent: Intent.() -> Unit = {}) {
    val intent = Intent(this, A::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.apply(configIntent)
    startActivity(intent)
}

inline fun <reified T : Service> Context.startService(configIntent: Intent.() -> Unit = {}) {
    startService(Intent(this, T::class.java).apply(configIntent))
}

inline fun <reified T : Service> Context.stopService() {
    stopService(Intent(this, T::class.java))
}

@SuppressLint("UnspecifiedImmutableFlag")
inline fun <reified T : Service> Context.servicePendingIntent(
    action: String,
    configIntent: Intent.() -> Unit = {}
): PendingIntent? {
    val intent = Intent(this, T::class.java)
    intent.action = action
    configIntent.invoke(intent)
    val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        FLAG_UPDATE_CURRENT or FLAG_MUTABLE
    } else {
        FLAG_UPDATE_CURRENT
    }
    return getService(this, 0, intent, flags)
}

@SuppressLint("UnspecifiedImmutableFlag")
fun Context.activityPendingIntent(
    intent: Intent,
    action: String
): PendingIntent? {
    intent.action = action
    val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        FLAG_UPDATE_CURRENT or FLAG_MUTABLE
    } else {
        FLAG_UPDATE_CURRENT
    }
    return getActivity(this, 0, intent, flags)
}

@SuppressLint("UnspecifiedImmutableFlag")
inline fun <reified T : Activity> Context.activityPendingIntent(
    action: String,
    configIntent: Intent.() -> Unit = {}
): PendingIntent? {
    val intent = Intent(this, T::class.java)
    intent.action = action
    configIntent.invoke(intent)
    val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        FLAG_UPDATE_CURRENT or FLAG_MUTABLE
    } else {
        FLAG_UPDATE_CURRENT
    }
    return getActivity(this, 0, intent, flags)
}

@SuppressLint("UnspecifiedImmutableFlag")
inline fun <reified T : BroadcastReceiver> Context.broadcastPendingIntent(
    action: String,
    configIntent: Intent.() -> Unit = {}
): PendingIntent? {
    val intent = Intent(this, T::class.java)
    intent.action = action
    configIntent.invoke(intent)
    val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        FLAG_UPDATE_CURRENT or FLAG_MUTABLE
    } else {
        FLAG_UPDATE_CURRENT
    }
    return getBroadcast(this, 0, intent, flags)
}

val Context.defaultSharedPreferences: SharedPreferences
    get() = PreferenceManager.getDefaultSharedPreferences(this)

fun Context.getPrefBoolean(key: String, defValue: Boolean = false) =
    defaultSharedPreferences.getBoolean(key, defValue)

fun Context.putPrefBoolean(key: String, value: Boolean = false) =
    defaultSharedPreferences.edit { putBoolean(key, value) }

fun Context.getPrefInt(key: String, defValue: Int = 0) =
    defaultSharedPreferences.getInt(key, defValue)

fun Context.putPrefInt(key: String, value: Int) =
    defaultSharedPreferences.edit { putInt(key, value) }

fun Context.getPrefLong(key: String, defValue: Long = 0L) =
    defaultSharedPreferences.getLong(key, defValue)

fun Context.putPrefLong(key: String, value: Long) =
    defaultSharedPreferences.edit { putLong(key, value) }

fun Context.getPrefString(key: String, defValue: String? = null) =
    defaultSharedPreferences.getString(key, defValue)

fun Context.putPrefString(key: String, value: String?) =
    defaultSharedPreferences.edit { putString(key, value) }

fun Context.getPrefStringSet(
    key: String,
    defValue: MutableSet<String>? = null
): MutableSet<String>? = defaultSharedPreferences.getStringSet(key, defValue)

fun Context.putPrefStringSet(key: String, value: MutableSet<String>) =
    defaultSharedPreferences.edit { putStringSet(key, value) }

fun Context.removePref(key: String) =
    defaultSharedPreferences.edit { remove(key) }


fun Context.getCompatColor(@ColorRes id: Int): Int = ContextCompat.getColor(this, id)

fun Context.getCompatDrawable(@DrawableRes id: Int): Drawable? = ContextCompat.getDrawable(this, id)

fun Context.getCompatColorStateList(@ColorRes id: Int): ColorStateList? =
    ContextCompat.getColorStateList(this, id)

/**
 *  获得context的FragmentActivity
 */
fun Context.getFragmentActivity(): FragmentActivity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is FragmentActivity) {
            return currentContext
        }
        currentContext = currentContext.baseContext
    }
    return null
}

fun Context.restart() {
    val intent: Intent? = packageManager.getLaunchIntentForPackage(packageName)
    intent?.let {
        intent.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK
                    or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    or Intent.FLAG_ACTIVITY_CLEAR_TOP
        )
        startActivity(intent)
        //杀掉以前进程
        Process.killProcess(Process.myPid())
        exitProcess(0)
    }
}

/**
 * 系统息屏时间
 */
val Context.sysScreenOffTime: Int
    get() {
        return kotlin.runCatching {
            Settings.System.getInt(contentResolver, Settings.System.SCREEN_OFF_TIMEOUT)
        }.onFailure {
            it.printOnDebug()
        }.getOrDefault(0)
    }

val Context.statusBarHeight: Int
    @SuppressLint("DiscouragedApi", "InternalInsetResource")
    get() {
        if (Build.BOARD == "windows") {
            return 0
        }
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }

val Context.navigationBarHeight: Int
    @SuppressLint("DiscouragedApi", "InternalInsetResource")
    get() {
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }




fun Context.getClipText(): String? {
    clipboardManager.primaryClip?.let {
        if (it.itemCount > 0) {
            return it.getItemAt(0).text.toString().trim()
        }
    }
    return null
}


/**
 * 获取电量
 */
val Context.sysBattery: Int
    get() {
        val iFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = registerReceiver(null, iFilter)
        return batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
    }

val Context.externalFiles: File
    get() = this.getExternalFilesDir(null) ?: this.filesDir

val Context.externalCache: File
    get() = this.externalCacheDir ?: this.cacheDir




@Suppress("DEPRECATION")
val Context.isWifiConnect: Boolean
    @SuppressLint("MissingPermission")
    get() {
        val info = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        return info?.isConnected == true
    }

val Context.isPad: Boolean
    get() {
        return (resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }

val Context.isTv: Boolean
    get() = uiModeManager.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION

@Suppress("DEPRECATION")
val Context.channel: String
    get() {
        try {
            val pm = packageManager
            val appInfo = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            return appInfo.metaData.getString("channel") ?: ""
        } catch (e: Exception) {
            e.printOnDebug()
        }
        return ""
    }
