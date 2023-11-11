package com.slot.twostepverification.utils.webdav

import android.util.Log
import at.bitfire.dav4jvm.BasicDigestAuthHandler
import at.bitfire.dav4jvm.DavCollection
import com.slot.twostepverification.utils.https.OkHelper
import com.slot.twostepverification.utils.https.OkHelper.okHttpClient
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URL
import java.net.URLEncoder

open class WebDav(authHandler: BasicDigestAuthHandler) {
    private val url: URL = URL(authHandler.domain)
    private val httpUrl: String? by lazy {
        val raw = url.toString()
            .replace("davs://", "https://")
            .replace("dav://", "http://")
        return@lazy kotlin.runCatching {
            URLEncoder.encode(raw, "UTF-8")
                .replace("+", "%20")
                .replace("%3A", ":")
                .replace("%2F", "/")
        }.getOrNull()
    }
    val host: String? get() = url.host

    // 重新添加 authHandler
    private val webDavClient by lazy {
        okHttpClient.newBuilder().run {
            followRedirects(false)
            authenticator(authHandler)
            addNetworkInterceptor(authHandler)
            build()
        }
    }
    val location = "https://example.com/webdav/hello.txt".toHttpUrl()
    val davCollection = DavCollection(webDavClient, location)
    fun downloadWebDav() {
        davCollection.put("World".toRequestBody(contentType = "text/plain".toMediaType())) { response ->
            // todo:something
        }
    }
}