package com.slot.twostepverification.help.store

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import com.slot.twostepverification.const.LocalConfig
import com.slot.twostepverification.help.TwoHelper
import com.slot.twostepverification.data.entity.VerificationItem
import com.slot.twostepverification.utils.AppLog
import com.slot.twostepverification.utils.compress.ZipUtils
import com.slot.twostepverification.utils.file.FileUtils
import com.slot.twostepverification.utils.file.openInputStream
import com.slot.twostepverification.utils.https.GSON
import com.slot.twostepverification.utils.https.fromJsonArray
import com.slot.twostepverification.utils.isContentScheme
import com.slot.twostepverification.utils.isJsonArray
import com.slot.twostepverification.utils.showToasts
import splitties.init.appCtx
import java.io.File
import java.io.FileInputStream

/**
 * 恢复
 */
object Restore {

    suspend fun restore(context: Context, uri: Uri) {
        kotlin.runCatching {
            FileUtils.delete(Backup.backupPath)
            if (uri.isContentScheme()) {
                DocumentFile.fromSingleUri(context, uri)!!.openInputStream()!!.use {
                    ZipUtils.unZipToPath(it, Backup.backupPath)
                }
            } else {
                ZipUtils.unZipToPath(File(uri.path!!), Backup.backupPath)
            }
        }.onFailure {
            AppLog.put("复制解压文件出错\n${it.localizedMessage}", it)
            return
        }
        kotlin.runCatching {
            restore(Backup.backupPath)
            LocalConfig.lastBackup = System.currentTimeMillis()
        }.onFailure {
            appCtx.showToasts("恢复备份出错\n${it.localizedMessage}")
            AppLog.put("恢复备份出错\n${it.localizedMessage}", it)
        }
    }

    suspend fun restore(path: String) {
        val aes = BackupAES()
        File(path, "tsp.json").takeIf {
            it.exists()
        }?.runCatching {
            var json = readText()
            Log.e("json", json)
            if (!json.isJsonArray()) {
                json = aes.decryptStr(json)
            }
            GSON.fromJsonArray<VerificationItem>(json).getOrNull()?.let {
                TwoHelper.updateItems(it)
            }
        }?.onFailure {
            AppLog.put("恢复二步验证出错\n${it.localizedMessage}", it)
        }?.onSuccess {
            appCtx.showToasts("备份成功")
        }
        // todo: 恢复 DataStoreUtils中的数据

    }

    private inline fun <reified T> fileToListT(path: String, fileName: String): List<T>? {
        try {
            val file = File(path, fileName)
            if (file.exists()) {
                FileInputStream(file).use {
                    return GSON.fromJsonArray<T>(it).getOrThrow()
                }
            }
        } catch (e: Exception) {
            AppLog.put("$fileName\n读取解析出错\n${e.localizedMessage}", e)
            appCtx.showToasts("$fileName\n读取文件出错\n${e.localizedMessage}")
        }
        return null
    }

}