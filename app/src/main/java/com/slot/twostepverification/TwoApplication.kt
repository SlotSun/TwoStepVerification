package com.slot.twostepverification

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import coil.Coil
import coil.ImageLoader
import com.slot.twostepverification.const.LOCALE
import com.slot.twostepverification.const.languageEN
import com.slot.twostepverification.const.locales
import com.slot.twostepverification.utils.data.DataStoreUtils
import com.slot.twostepverification.utils.https.OkHelper
import com.slot.twostepverification.utils.https.setBaseUrl
import com.slot.twostepverification.utils.https.setHttpClient

class TwoApplication:Application() {
    companion object {
        val localeState: MutableState<Map<String, String>> by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            mutableStateOf(locales.getValue("English"))
        }
    }

    override fun onCreate() {
        super.onCreate()
        setBaseUrl("https://pub.dev/packages/")
        setHttpClient(OkHelper.httpClient(applicationContext))
        // 初始化 datastore-preferences
        DataStoreUtils.init(applicationContext)
        localeState.value = locales.getValue(DataStoreUtils.getSyncData(LOCALE, "简体中文"))
        initCoil(context = this)
    }

    /**
     * 初始化图片选择引擎
     */
    private fun initCoil(context: Context) {
        val imageLoader = ImageLoader.Builder(context = context)
            .crossfade(enable = false)
            .allowHardware(enable = true)
            .bitmapConfig(bitmapConfig = Bitmap.Config.RGB_565)
            .components {

            }
            .build()
        Coil.setImageLoader(imageLoader)
    }
}