package com.slot.twostepverification.utils.webdav

import com.slot.twostepverification.data.entity.Authorization

/**
 * webDavFile
 */
@Suppress("unused")
class WebDavFile(
    urlStr: String,
    authorization: Authorization,
    val displayName: String,
    val urlName: String,
    val size: Long,
    val contentType: String,
    val resourceType: String,
    val lastModify: Long
) : WebDav(authorization) {

    val isDir by lazy {
        contentType == "httpd/unix-directory" || resourceType.lowercase().contains("collection")
    }

}
