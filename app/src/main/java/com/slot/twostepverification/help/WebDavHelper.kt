package com.slot.twostepverification.help

import android.text.format.DateUtils
import com.slot.twostepverification.const.LocalConfig
import com.slot.twostepverification.const.locale
import com.slot.twostepverification.data.entity.Authorization
import com.slot.twostepverification.exception.NoStackTraceException
import com.slot.twostepverification.help.store.Backup
import com.slot.twostepverification.help.store.Restore
import com.slot.twostepverification.utils.AlphanumComparator
import com.slot.twostepverification.utils.compress.ZipUtils
import com.slot.twostepverification.utils.file.FileUtils
import com.slot.twostepverification.utils.https.NetworkUtils
import com.slot.twostepverification.utils.webdav.WebDav
import com.slot.twostepverification.utils.webdav.WebDavException
import com.slot.twostepverification.utils.webdav.WebDavFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File

/**
 * webDav初始化会访问网络,不要放到主线程
 */
object WebDavHelper {
    private const val defaultWebDavUrl = "https://dav.jianguoyun.com/dav/"
    private val rootWebDavUrl = "https://dav.jianguoyun.com/dav/tsv/"
    var authorization: Authorization? = null
        private set

    val isJianGuoYun get() = rootWebDavUrl.startsWith(defaultWebDavUrl, true)
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
                var mAuthorization = Authorization(defaultWebDavUrl, account, password)
                checkAuthorization(mAuthorization)
                // webdav 创建用户文件夹
                mAuthorization = Authorization(rootWebDavUrl, account, password)
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
            val putAuth = Authorization(putUrl, it.username, it.password)
            WebDav(putAuth).upload(Backup.zipFilePath)
        }
    }

    /**
     *  最后一次备份
     */
    suspend fun lastBackUp(): Result<WebDavFile?> {
        return kotlin.runCatching {
            authorization?.let {
                var lastBackupFile: WebDavFile? = null
                WebDav(it).listFiles().reversed().forEach { webDavFile ->
                    if (webDavFile.displayName.startsWith("backup")) {
                        if (lastBackupFile == null
                            || webDavFile.lastModify > lastBackupFile!!.lastModify
                        ) {
                            lastBackupFile = webDavFile
                        }
                    }
                }
                println(lastBackupFile?.displayName)
                lastBackupFile
            }
        }
    }

    /**
     *  获取云端数据列表
     */
    @Throws(Exception::class)
    suspend fun getBackupNames(): List<String> {
        val names = mutableListOf<String>()
        authorization?.let {
            var files = WebDav(it).listFiles()
            files = files.sortedWith { o1, o2 ->
                AlphanumComparator.compare(o1.displayName, o2.displayName)
            }.reversed()
            files.forEach { webDav ->
                val name = webDav.displayName
                if (name.startsWith("backup")) {
                    names.add(name)
                }
            }
        } ?: throw NoStackTraceException("webDav没有配置")
        return names
    }


    /**
     *  todo: 同步webDav到本地
     */
    suspend fun syncWebDav() {
        // 最后一次更新的文件
        val lastBackupFile = withContext(Dispatchers.IO) { lastBackUp().getOrNull() } ?: return
        // 坚果云在中国，本地时间和中国有时差，需要修正最后备份时间
        if (lastBackupFile.lastModify - LocalConfig.lastBackup > DateUtils.MINUTE_IN_MILLIS) {
            LocalConfig.lastBackup = lastBackupFile.lastModify
            restoreWebDav(lastBackupFile.displayName)
        }
    }
    @Throws(WebDavException::class)
    suspend fun restoreWebDav(name: String) {
        authorization?.let {
            val authTemp = it.copy(url = rootWebDavUrl + name)
            val webDav = WebDav(authHandler = authTemp)
            // 拉取云端数据
            webDav.downloadTo(Backup.zipFilePath, true)
            FileUtils.delete(Backup.backupPath)
            // 解压
            ZipUtils.unZipToPath(File(Backup.zipFilePath), Backup.backupPath)
            // 导入
            Restore.restore(Backup.backupPath)
        }
    }

}