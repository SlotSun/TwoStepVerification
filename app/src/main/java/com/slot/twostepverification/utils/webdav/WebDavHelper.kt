package com.slot.twostepverification.utils.webdav

import com.slot.twostepverification.const.PreferKey
import com.slot.twostepverification.const.locale
import com.slot.twostepverification.data.entity.Authorization
import com.slot.twostepverification.utils.getPrefString
import com.slot.twostepverification.utils.removePref
import kotlinx.coroutines.runBlocking
import splitties.init.appCtx

/**
 * webDav初始化会访问网络,不要放到主线程
 */
object WebDavHelper {
    private const val defaultWebDavUrl = "https://dav.jianguoyun.com/dav/tsv"
    var authorization: Authorization? = null
        private set

    // 初始化
    init {
        runBlocking {
            // 读取pref里的存的webDav数据
            upConfig()
        }
    }

    private suspend fun upConfig() {
        kotlin.runCatching {
            authorization = null
            val account = appCtx.getPrefString(PreferKey.ACCOUNT)
            val password = appCtx.getPrefString(PreferKey.PASS_WORD)
            if (!account.isNullOrBlank() && !password.isNullOrBlank()) {
                val mAuthorization = Authorization(defaultWebDavUrl,account, password)
                checkAuthorization(mAuthorization)
                // webdav 创建用户
                WebDav(mAuthorization).makeAsDir()
                authorization = mAuthorization
            }
        }
    }

    @Throws(WebDavException::class)
    private suspend fun checkAuthorization(authorization: Authorization) {
        if (!WebDav(authorization).check()) {
            appCtx.removePref(PreferKey.PASS_WORD)
            throw WebDavException(locale("FailToAuthorizeWebDav"))
        }
    }



}