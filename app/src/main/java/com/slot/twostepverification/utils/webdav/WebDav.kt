package com.slot.twostepverification.utils.webdav

import android.net.Uri
import android.util.Log
import at.bitfire.dav4jvm.DavCollection
import com.slot.twostepverification.data.entity.Authorization
import com.slot.twostepverification.exception.NoStackTraceException
import com.slot.twostepverification.utils.AppLog
import com.slot.twostepverification.utils.findNS
import com.slot.twostepverification.utils.findNSPrefix
import com.slot.twostepverification.utils.https.NetworkUtils
import com.slot.twostepverification.utils.https.OkHelper.okHttpClient
import com.slot.twostepverification.utils.https.newCallResponse
import com.slot.twostepverification.utils.https.text
import com.slot.twostepverification.utils.log.printOnDebug
import com.slot.twostepverification.utils.toRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.intellij.lang.annotations.Language
import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URL
import java.net.URLDecoder
import java.net.URLEncoder
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Suppress("unused", "MemberVisibilityCanBePrivate")
open class WebDav(val authHandler: Authorization) {

    private var path = authHandler.url

    companion object {
        private val dateTimeFormatter = DateTimeFormatter.RFC_1123_DATE_TIME

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


    /**
     * @param propsList 指定列出文件的哪些属性
     */
    @Throws(WebDavException::class)
    private suspend fun propFindResponse(
        propsList: List<String> = emptyList(),
        depth: Int = 1
    ): String? {
        val requestProps = StringBuilder()
        for (p in propsList) {
            requestProps.append("<a:").append(p).append("/>\n")
        }
        val requestPropsStr: String = if (requestProps.toString().isEmpty()) {
            DIR.replace("%s", "")
        } else {
            String.format(DIR, requestProps.toString() + "\n")
        }
        val url = httpUrl ?: return null
        return webDavClient.newCallResponse {
            url(url)
            addHeader("Depth", depth.toString())
            // 添加RequestBody对象，可以只返回的属性。如果设为null，则会返回全部属性
            // 注意：尽量手动指定需要返回的属性。若返回全部属性，可能后由于Prop.java里没有该属性名，而崩溃。
            val requestBody = requestPropsStr.toRequestBody("text/plain".toMediaType())
            method("PROPFIND", requestBody)
        }.apply {
            checkResult(this)
        }.body.text()
    }


    @Throws(WebDavException::class)
    suspend fun listFiles(): List<WebDavFile> {
        propFindResponse()?.let { body ->
            return parseBody(body).filter {wbFile->
                wbFile.urlStr != path
            }
        }
        return emptyList()
    }

    /**
     * 检测返回结果是否正确
     */
    @Throws(Exception::class)
    private fun checkResult(response: Response) {
        if (!response.isSuccessful) {
            val body = response.body?.string()
            if (response.code == 401) {
                val headers = response.headers("WWW-Authenticate")
                val supportBasicAuth = headers.any {
                    it.startsWith("Basic", ignoreCase = true)
                }
                if (headers.isNotEmpty() && !supportBasicAuth) {
                    Log.e("WebDav", "服务器不支持BasicAuth认证")
                }
            }

            if (response.message.isNotBlank() || body.isNullOrBlank()) {
                throw WebDavException("${url}\n${response.code}:${response.message}")
            }
            val document = Jsoup.parse(body)
            val exception = document.getElementsByTag("s:exception").firstOrNull()?.text()
            val message = document.getElementsByTag("s:message").firstOrNull()?.text()
            if (exception == "ObjectNotFound") {
                throw ObjectNotFoundException(

                    message ?: "$path doesn't exist. code:${response.code}"
                )
            }
            throw WebDavException(message ?: "未知错误 code:${response.code}")
        }
    }

    //备份地址
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
                    .header(authHandler.name, authHandler.data)
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
                addHeader("Depth", "0")
                val requestBody = EXISTS.toRequestBody("application/xml".toMediaType())
                method("PROPFIND", requestBody)
            }.use { it.code != 401 }
        }.getOrDefault(true)
    }

    /**
     *  上传文件到WEBDAV
     */
    suspend fun upload(path: String, data: ByteArray): Boolean {
        return kotlin.runCatching {
            webDavClient.newCallResponse {
                url(url)
                addHeader("Depth", "0")
                val requestBody = data.toRequestBody("application/octet-stream".toMediaType())
                method("PUT", requestBody)
            }.use { it.code == 201 }
        }.getOrDefault(false)
    }

    /**
     * 获取当前url文件信息
     */
    @Throws(WebDavException::class)
    suspend fun getWebDavFile(): WebDavFile? {
        return propFindResponse(depth = 0)?.let {
            parseBody(it).firstOrNull()
        }
    }


    /**
     *  检索WEBDAV文件列表
     */
    suspend fun getListFormWebDav(): List<DavCollection> {
        return emptyList()
    }

    /**
     * 文件是否存在
     */
    suspend fun exists(): Boolean {
        val url = httpUrl ?: return false
        return kotlin.runCatching {
            return webDavClient.newCallResponse {
                url(url)
                addHeader("Depth", "0")
                val requestBody = EXISTS.toRequestBody("application/xml".toMediaType())
                method("PROPFIND", requestBody)
            }.use { it.isSuccessful }
        }.getOrDefault(false)
    }

    /**
     * 根据自己的URL，在远程处创建对应的文件夹
     * @return 是否创建成功
     */
    suspend fun makeAsDir(): Boolean {
        val url = httpUrl ?: return false
        //防止报错
        return kotlin.runCatching {
            if (!exists()) {
                webDavClient.newCallResponse {
                    url(url)
                    method("MKCOL", null)
                }.use {
                    checkResult(it)
                }
            }
        }.onFailure {
            Log.e("WebDav", "WebDav创建目录失败\n${it.localizedMessage}")
        }.isSuccess
    }


    @Throws(WebDavException::class)
    suspend fun upload(
        localPath: String,
        contentType: String = "application/octet-stream"
    ) {
        upload(File(localPath), contentType)
    }

    /**
     * 上传文件
     */
    @Throws(WebDavException::class)
    suspend fun upload(
        file: File,
        contentType: String = "application/octet-stream"
    ) {
        kotlin.runCatching {
            withContext(Dispatchers.IO) {
                if (!file.exists()) throw WebDavException("文件不存在")
                // 务必注意RequestBody不要嵌套，不然上传时内容可能会被追加多余的文件信息
                val fileBody = file.asRequestBody(contentType.toMediaType())
                val url = httpUrl ?: throw WebDavException("url不能为空")
                webDavClient.newCallResponse {
                    url(url)
                    put(fileBody)
                }.use {
                    checkResult(it)
                }
            }
        }.onFailure {
            AppLog.put("WebDav上传失败\n${it.localizedMessage}")
            throw WebDavException("WebDav上传失败\n${it.localizedMessage}")
        }
    }

    @Throws(WebDavException::class)
    suspend fun upload(uri: Uri, contentType: String) {
        // 务必注意RequestBody不要嵌套，不然上传时内容可能会被追加多余的文件信息
        kotlin.runCatching {
            withContext(Dispatchers.IO) {
                val fileBody = uri.toRequestBody(contentType.toMediaType())
                val url = httpUrl ?: throw NoStackTraceException("url不能为空")
                webDavClient.newCallResponse {
                    url(url)
                    put(fileBody)
                }.use {
                    checkResult(it)
                }
            }
        }.onFailure {
            AppLog.put("WebDav上传失败\n${it.localizedMessage}")
            throw WebDavException("WebDav上传失败\n${it.localizedMessage}")
        }
    }

    /**
     * 下载到本地
     * @param savedPath       本地的完整路径，包括最后的文件名
     * @param replaceExisting 是否替换本地的同名文件
     */
    @Throws(WebDavException::class)
    suspend fun downloadTo(savedPath: String, replaceExisting: Boolean) {
        val file = File(savedPath)
        if (file.exists() && !replaceExisting) {
            return
        }
        downloadInputStream().use { byteStream ->
            FileOutputStream(file).use {
                byteStream.copyTo(it)
            }
        }
    }

    @Throws(WebDavException::class)
    suspend fun downloadInputStream(): InputStream {
        val url = httpUrl ?: throw WebDavException("WebDav下载出错\nurl为空")
        val byteStream = webDavClient.newCallResponse {
            url(url)
        }.apply {
            checkResult(this)
        }.body.byteStream()
        return byteStream ?: throw WebDavException("WebDav下载出错\nNull Exception")
    }

    /**
     * 移除文件/文件夹
     */
    suspend fun delete(): Boolean {
        val url = httpUrl ?: return false
        //防止报错
        return kotlin.runCatching {
            webDavClient.newCallResponse {
                url(url)
                method("DELETE", null)
            }.use {
                checkResult(it)
            }
        }.onFailure {
            AppLog.put("WebDav删除失败\n${it.localizedMessage}")
        }.isSuccess
    }


    /**
     * 解析webDav返回的xml
     */
    private fun parseBody(s: String): List<WebDavFile> {
        val list = ArrayList<WebDavFile>()
        val document = kotlin.runCatching {
            Jsoup.parse(s, Parser.xmlParser())
        }.getOrElse {
            Jsoup.parse(s)
        }
        val ns = document.findNSPrefix("DAV:")
        val elements = document.findNS("response", ns)
        val urlStr = httpUrl ?: return list
        val baseUrl = NetworkUtils.getBaseUrl(urlStr)
        for (element in elements) {
            //依然是优化支持 caddy 自建的 WebDav ，其目录后缀都为“/”, 所以删除“/”的判定，不然无法获取该目录项
            val href = element.findNS("href", ns)[0].text().replace("+", "%2B")
            val hrefDecode = URLDecoder.decode(href, "UTF-8")
                .removeSuffix("/")
            val fileName = hrefDecode.substringAfterLast("/")
            val webDavFile: WebDav
            try {
                val urlName = hrefDecode.ifEmpty {
                    url.file.replace("/", "")
                }
                val contentType = element
                    .findNS("getcontenttype", ns)
                    .firstOrNull()?.text().orEmpty()
                val resourceType = element
                    .findNS("resourcetype", ns)
                    .firstOrNull()?.html()?.trim().orEmpty()
                val size = kotlin.runCatching {
                    element.findNS("getcontentlength", ns)
                        .firstOrNull()?.text()?.toLong() ?: 0
                }.getOrDefault(0)
                val lastModify: Long = kotlin.runCatching {
                    element.findNS("getlastmodified", ns)
                        .firstOrNull()?.text()?.let {
                            LocalDateTime.parse(it, dateTimeFormatter)
                                .toInstant(ZoneOffset.of("+8")).toEpochMilli()
                        }
                }.getOrNull() ?: 0
                val fullURL = NetworkUtils.getAbsoluteURL(baseUrl, hrefDecode)
                webDavFile = WebDavFile(
                    urlStr =  fullURL,
                    authorization = authHandler,
                    displayName = fileName,
                    urlName = urlName,
                    size = size,
                    contentType = contentType,
                    resourceType = resourceType,
                    lastModify = lastModify
                )
                list.add(webDavFile)
            } catch (e: MalformedURLException) {
                e.printOnDebug()
            }
        }
        return list
    }
}