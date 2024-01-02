package com.slot.twostepverification.utils

import com.slot.twostepverification.const.LocalConfig
import com.slot.twostepverification.exception.NoStackTraceException
import com.slot.twostepverification.utils.coroutine.Coroutine
import com.slot.twostepverification.utils.https.get

object VersionUtil {

    data class UpdateInfo(
        val tagName: String,
        val updateLog: String,
        val downloadUrl: String,
        val fileName: String
    )


    fun check(
    ): Coroutine<UpdateInfo> {
        val lastReleaseUrl = "https://api.github.com/repos/SlotSun/TwoStepVerification/releases/latest"
        return Coroutine.async {
            val body = get(lastReleaseUrl)
            if (body.isBlank()) {
                throw Exception("获取新版本出错")
            }
            val rootDoc = jsonPath.parse(body)
            val tagName = rootDoc.read<String>("$.tag_name")?.replace("v", "")
                ?: throw NoStackTraceException("获取新版本出错1")
            if (tagName > LocalConfig.appInfo.versionName) {
                val updateBody = rootDoc.readString("$.body")
                    ?: throw NoStackTraceException("获取新版本出错2")
                val path = "\$.assets[?(@.name =~ /TSV_app_.*?apk\$/)]"
                val downloadUrl = rootDoc.read<List<String>>("${path}.browser_download_url")
                    .firstOrNull()
                    ?: throw NoStackTraceException("获取新版本出错3")
                val fileName = rootDoc.read<List<String>>("${path}.name")
                    .firstOrNull()
                    ?: throw NoStackTraceException("获取新版本出错4")
                return@async UpdateInfo(tagName, updateBody, downloadUrl, fileName)
            } else {
                throw NoStackTraceException("已是最新版本")
            }
        }.timeout(10000)
    }
}