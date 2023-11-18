package com.slot.twostepverification.utils.https

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

// okhttp拦截异常处理
object OkHttpExceptionInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        try {
            return chain.proceed(chain.request())
        } catch (e: IOException) {
            throw e
        } catch (e: Throwable) {
            throw IOException(e)
        }
    }

}
