package com.slot.twostepverification.utils.webdav

import com.slot.twostepverification.const.LocalConfig
import com.slot.twostepverification.const.locale
import com.slot.twostepverification.data.entity.Authorization
import com.slot.twostepverification.utils.file.store.Backup
import com.slot.twostepverification.utils.https.NetworkUtils
import kotlinx.coroutines.runBlocking

/**
 * webDav初始化会访问网络,不要放到主线程
 */
object WebDavHelper {
    private const val defaultWebDavUrl = "https://dav.jianguoyun.com/dav/"
    private val rootWebDavUrl = "https://dav.jianguoyun.com/dav/tsv/"
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
            val account = LocalConfig.user
            val password = LocalConfig.password
            if (!account.isNullOrBlank() && !password.isNullOrBlank()) {
                var mAuthorization = Authorization(defaultWebDavUrl,account, password)
                checkAuthorization(mAuthorization)
                // webdav 创建用户文件夹
                mAuthorization = Authorization(rootWebDavUrl,account, password)
                WebDav(mAuthorization).makeAsDir()
                authorization = mAuthorization
            }
        }
    }

    @Throws(WebDavException::class)
    private suspend fun checkAuthorization(authorization: Authorization) {
        if (!WebDav(authorization).check()) {
            LocalConfig.password = ""
            throw WebDavException(locale("FailToAuthorizeWebDav"))
        }
    }

    /**
     * webDav备份
     * @param fileName 备份文件名
     */
    @Throws(Exception::class)
    suspend fun backUpWebDav(fileName: String) {
        if (!NetworkUtils.isAvailable()) return
        authorization?.let {
            val putUrl = "${it.url}$fileName"
            val putAuth = Authorization(putUrl,it.username,it.password)
            WebDav(putAuth).upload(Backup.zipFilePath)
        }
    }

    /**
     *  todo: 同步webDav到本地
     */
    fun syncWebDav() {

    }


}