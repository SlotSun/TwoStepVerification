package com.slot.twostepverification.help.store

import android.content.Context
import android.net.Uri
import androidx.annotation.Keep
import androidx.documentfile.provider.DocumentFile
import com.slot.twostepverification.const.LocalConfig
import com.slot.twostepverification.help.TwoHelper
import com.slot.twostepverification.exception.NoStackTraceException
import com.slot.twostepverification.utils.compress.ZipUtils
import com.slot.twostepverification.utils.externalFiles
import com.slot.twostepverification.utils.file.FileUtils
import com.slot.twostepverification.utils.file.createFolderIfNotExist
import com.slot.twostepverification.utils.file.getFile
import com.slot.twostepverification.utils.file.openOutputStream
import com.slot.twostepverification.utils.https.GSON
import com.slot.twostepverification.utils.https.writeToOutputStream
import com.slot.twostepverification.utils.isContentScheme
import com.slot.twostepverification.help.WebDavHelper
import com.slot.twostepverification.utils.data.DataStoreUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import splitties.init.appCtx
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.coroutines.coroutineContext

/**
 *  备份
 */
object Backup {
    val backupPath: String by lazy {
        appCtx.filesDir.getFile("backup").createFolderIfNotExist().absolutePath
    }
    val zipFilePath = "${appCtx.externalFiles.absolutePath}${File.separator}tmp_backup.zip"

    private val backupFileNames by lazy {
        arrayOf(
            "config.json",
            "tsp.json"
        )
    }
    private val aes = BackupAES()


    // 获取当前压缩文件名称
    private fun getNowZipFileName(): String {
        val backupDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .format(Date(System.currentTimeMillis()))
        return "backup${backupDate}.zip"
    }

    /**
     *  备份
     */
    @Throws(Exception::class)
    suspend fun backup(context: Context, path: String?) {
        // 坚果云在中国，本地时间和中国有时差，需要修正最后备份时间
        LocalConfig.lastBackup = Calendar.getInstance(TimeZone.getTimeZone("GMT+8")).timeInMillis
        // 清空备份文件夹
        FileUtils.delete(backupPath)
        // 输出所有的items
        TwoHelper.getItems().let {
            GSON.toJson(it).let { json ->
                aes.runCatching {
                    encryptBase64(json)
                }.getOrDefault(json).let {
                    FileUtils.createFileIfNotExist(backupPath + File.separator + "tsp.json")
                        .writeText(it)
                }
            }
        }
        // 将 preferences 中的数据填入config.json
        DataStoreUtils.getAll().let {
            GSON.toJson(it).let { json ->
                FileUtils.createFileIfNotExist(backupPath + File.separator + "config.json")
                    .writeText(json)
            }
        }

        coroutineContext.ensureActive()
        val zipFileName = getNowZipFileName()
        val paths = arrayListOf(*backupFileNames)
        for (i in 0 until paths.size) {
            paths[i] = backupPath + File.separator + paths[i]
        }
        FileUtils.delete(zipFilePath)
        FileUtils.delete(zipFilePath.replace("tmp_", ""))
        // 如果只开启最后一次保存：Todo
        val backupFileName = if (LocalConfig.onlyLatestBackup) {
            "backup.zip"
        } else {
            zipFileName
        }
        FileUtils.delete(zipFilePath)
        FileUtils.delete(zipFilePath.replace("tmp_", ""))
        if (ZipUtils.zipFiles(paths, zipFilePath)) {
            when {
                path.isNullOrBlank() -> {
                    copyBackup(context.getExternalFilesDir(null)!!, backupFileName)
                }

                path.isContentScheme() -> {
                    copyBackup(context, Uri.parse(path), backupFileName)
                }

                else -> {
                    copyBackup(File(path), backupFileName)
                }
            }
            WebDavHelper.backUpWebDav(zipFileName)
        }
        clearCache()
    }

    /**
     *  @param list 列表
     *  @param fileName 文件名
     *  @param path 文件路径
     *  将list<any>按文件路径path写入json文件
     */
    private suspend fun writeListToJson(list: List<Any>, fileName: String, path: String) {
        coroutineContext.ensureActive()
        withContext(Dispatchers.IO) {
            if (list.isNotEmpty()) {
                val file = FileUtils.createFileIfNotExist(path + File.separator + fileName)
                FileOutputStream(file).use { fos ->
                    BufferedOutputStream(fos, 64 * 1024).use {
                        GSON.writeToOutputStream(it, list)
                    }
                }
            }
        }
    }


    @Throws(Exception::class)
    @Suppress("SameParameterValue")
    private fun copyBackup(context: Context, uri: Uri, fileName: String) {
        val treeDoc = DocumentFile.fromTreeUri(context, uri)!!
        treeDoc.findFile(fileName)?.delete()
        val fileDoc = treeDoc.createFile("", fileName)
            ?: throw NoStackTraceException("创建文件失败")
        val outputS = fileDoc.openOutputStream()
            ?: throw NoStackTraceException("打开OutputStream失败")
        outputS.use {
            FileInputStream(zipFilePath).use { inputS ->
                inputS.copyTo(outputS)
            }
        }
    }

    @Throws(Exception::class)
    @Suppress("SameParameterValue")
    private fun copyBackup(rootFile: File, fileName: String) {
        FileInputStream(File(zipFilePath)).use { inputS ->
            val file = FileUtils.createFileIfNotExist(rootFile, fileName)
            FileOutputStream(file).use { outputS ->
                inputS.copyTo(outputS)
            }
        }
    }


    // 清空缓存
    fun clearCache() {
        FileUtils.delete(backupPath)
        FileUtils.delete(zipFilePath)
    }

}