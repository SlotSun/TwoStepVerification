package com.slot.twostepverification

import android.app.Application
import androidx.compose.runtime.MutableState
import com.slot.twostepverification.utils.data.DataStoreUtils
import com.slot.twostepverification.utils.https.OkHelper
import com.slot.twostepverification.utils.https.setBaseUrl
import com.slot.twostepverification.utils.https.setHttpClient

class TwoApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        setBaseUrl("https://pub.dev/packages/")
        setHttpClient(OkHelper.httpClient(applicationContext))

        // 初始化 datastore-preferences
        DataStoreUtils.init(applicationContext)
    }
}