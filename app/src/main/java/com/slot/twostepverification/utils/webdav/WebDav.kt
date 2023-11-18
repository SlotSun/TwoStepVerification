package com.slot.twostepverification.utils.webdav

import at.bitfire.dav4jvm.BasicDigestAuthHandler
import at.bitfire.dav4jvm.DavCollection
import com.slot.twostepverification.data.entity.Authorization
import com.slot.twostepverification.utils.https.HttpRequest
import com.slot.twostepverification.utils.https.OkHelper
import com.slot.twostepverification.utils.https.OkHelper.okHttpClient
import com.slot.twostepverification.utils.https.addHeaders
import com.slot.twostepverification.utils.https.newCallResponse
import com.slot.twostepverification.utils.https.setHttpClient
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.intellij.lang.annotations.Language
import java.net.URL
import java.net.URLEncoder

class WebDav(authHandler: Authorization) {

    companion object{
        // 指定返回哪些属性
        @Language("xml")
        private const val DIR =
            """<?xml version="1.0"?>
            <a:propfind xmlns:a="DAV:">
                <a:prop>
                    <a:displayname/>
                    <a:resourcetype/>
                    <a:getcontentlength/>
                    <a:creationdate/>
                    <a:getlastmodified/>
                    %s
                </a:prop>
            </a:propfind>"""

        @Language("xml")
        private const val EXISTS =
            """<?xml version="1.0"?>
            <propfind xmlns="DAV:">
               <prop>
                  <resourcetype />
               </prop>
            </propfind>"""
    }

    private val url: URL = URL(authHandler.url)
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
        val authInterceptor = Interceptor { chain ->
            var request = chain.request()
            if (request.url.host.equals(host, true)) {
                request = request
                    .newBuilder()
                    .header("Authorization", authHandler.data)
                    .build()
            }
            chain.proceed(request)
        }
        okHttpClient.newBuilder().run {
            interceptors().add(0, authInterceptor)
            addNetworkInterceptor(authInterceptor)
            build()
        }
    }
    /**
     *  检测用户名密码是否正确
     */
    suspend fun check(): Boolean {
        return kotlin.runCatching {
            webDavClient.newCallResponse {
                url(url)
                addHeader("Depth","0")
                val requestBody = EXISTS.toRequestBody("application/xml".toMediaType())
                method("PROPFIND", requestBody)
            }.use { it.code != 401 }
        }.getOrDefault(true)
    }
}