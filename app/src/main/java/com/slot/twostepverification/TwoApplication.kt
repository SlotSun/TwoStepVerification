package com.slot.twostepverification

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.text.intl.Locale
import coil.Coil
import coil.ImageLoader
import com.slot.twostepverification.const.CHANGED_THEME
import com.slot.twostepverification.const.DYNAMIC_COLOR
import com.slot.twostepverification.const.LOCALE
import com.slot.twostepverification.const.LocalConfig.dynamicColorState
import com.slot.twostepverification.const.LocalConfig.localeState
import com.slot.twostepverification.const.LocalConfig.securityOpenState
import com.slot.twostepverification.const.LocalConfig.themeTypeState
import com.slot.twostepverification.const.SECURITY_OPEN
import com.slot.twostepverification.const.locales
import com.slot.twostepverification.utils.data.DataStoreUtils
import com.slot.twostepverification.utils.https.OkHelper
import com.slot.twostepverification.utils.https.setBaseUrl
import com.slot.twostepverification.utils.https.setHttpClient

class TwoApplication : Application() {
    companion object {
        /**
         *  初始化ui
         */
        fun initUi() {
            //界面语言初始化
            val locale = DataStoreUtils.readStringData(key = LOCALE)
            val localeNative  = Locale.current.language
            localeState.value = locales.getValue(
                if (locale == "") {
                    when(localeNative){
                        "zh" -> "简体中文"
                        "en" -> "English"
                        else -> "English"
                    }
                } else {
                    locale
                }
            )
            //界面主题初始化
            themeTypeState.value = DataStoreUtils.readIntData(key = CHANGED_THEME)
            // 是否动态主题
            dynamicColorState.value = DataStoreUtils.readBooleanData(key = DYNAMIC_COLOR)
            // 是否开启安全认证
            securityOpenState.value = DataStoreUtils.readBooleanData(key = SECURITY_OPEN)
        }
    }

    override fun onCreate() {
        super.onCreate()
        // 初始化okHttp
        // 不设置不行
        setBaseUrl("https://www.baidu.com")
        setHttpClient(OkHelper.httpClient(applicationContext))
        // 初始化 datastore-preferences
        DataStoreUtils.init(applicationContext)
        initUi()
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