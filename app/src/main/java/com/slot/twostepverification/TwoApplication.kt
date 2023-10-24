package com.slot.twostepverification

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
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
    }
}