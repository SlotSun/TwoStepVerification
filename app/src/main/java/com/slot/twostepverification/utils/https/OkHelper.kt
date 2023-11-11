package com.slot.twostepverification.utils.https

import android.content.Context
import com.slot.twostepverification.const.UA_NAME
import com.slot.twostepverification.const.USER_AGENT
import okhttp3.Cache
import okhttp3.ConnectionSpec
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.io.InputStream
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext

object OkHelper {

    private var httpClient: OkHttpClient? = null

    private var clientCertificate: InputStream? = null
    private var clientCertificatePwd: String? = null
    private var serverCertificates: Array<InputStream>? = null


    @JvmStatic
    @Synchronized
    fun httpClient(context: Context): OkHttpClient = httpClient ?: getOkHttpBuilder(context).also {
        httpClient = it
    }

    val okHttpClient:OkHttpClient by lazy {
        val specs = arrayListOf(
            ConnectionSpec.CLEARTEXT,
            ConnectionSpec.MODERN_TLS,
            ConnectionSpec.COMPATIBLE_TLS
        )
        val builder = OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .callTimeout(60, TimeUnit.SECONDS)
            //.cookieJar(cookieJar = cookieJar)
            .sslSocketFactory(SSLHelper.unsafeSSLSocketFactory, SSLHelper.unsafeTrustManager)
            .retryOnConnectionFailure(true)
            .hostnameVerifier(SSLHelper.unsafeHostnameVerifier)
            .connectionSpecs(specs)
            .followRedirects(true)
            .followSslRedirects(true)
            .addInterceptor(OkHttpExceptionInterceptor)
            .addInterceptor(Interceptor { chain ->
                val request = chain.request()
                val builder = request.newBuilder()
                if (request.header(UA_NAME) == null) {
                    builder.addHeader(UA_NAME, USER_AGENT)
                } else if (request.header(UA_NAME) == "null") {
                    builder.removeHeader(UA_NAME)
                }
                builder.addHeader("Keep-Alive", "300")
                builder.addHeader("Connection", "Keep-Alive")
                builder.addHeader("Cache-Control", "no-cache")
                chain.proceed(builder.build())
            })
            .addNetworkInterceptor { chain ->
                var request = chain.request()
                val enableCookieJar = request.header("CookieJar") != null

                if (enableCookieJar) {
                    val requestBuilder = request.newBuilder()
                    requestBuilder.removeHeader("CookieJar")
//                    request = CookieManager.loadRequest(requestBuilder.build())
                }

                val networkResponse = chain.proceed(request)

                if (enableCookieJar) {
//                    CookieManager.saveResponse(networkResponse)
                }
                networkResponse
            }

        builder.build().apply {
            val okHttpName =
                OkHttpClient::class.java.name.removePrefix("okhttp3.").removeSuffix("Client")
            val executor = dispatcher.executorService as ThreadPoolExecutor
            val threadName = "$okHttpName Dispatcher"
            executor.threadFactory = ThreadFactory { runnable ->
                Thread(runnable, threadName).apply {
                    isDaemon = false
                    uncaughtExceptionHandler = OkhttpUncaughtExceptionHandler
                }
            }
        }
    }



    private fun getOkHttpBuilder(context: Context): OkHttpClient {
        val keyManagers = HttpsHelper.prepareKeyManager(clientCertificate, clientCertificatePwd)
        val trustManager = HttpsHelper.prepareX509TrustManager(serverCertificates)
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(keyManagers, arrayOf(trustManager), null)
        return OkHttpClient().newBuilder()
            .readTimeout(10000L, TimeUnit.MILLISECONDS)
            .writeTimeout(10000L, TimeUnit.MILLISECONDS)
            .connectTimeout(10000L, TimeUnit.MILLISECONDS)
            .cookieJar(CookieJar())
            .cache(Cache(File(context.cacheDir, "okhttp"), 250L * 1024 * 1024))
            .sslSocketFactory(sslContext.socketFactory, trustManager)
            .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    }
}