package com.slot.twostepverification.data.entity

import okhttp3.Credentials
import java.io.Serial
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
data class Authorization(
    val url : String = "https://dav.jianguoyun.com/dav/",
    val username: String,
    val password: String,
    val charset: Charset = StandardCharsets.ISO_8859_1
) {

    var name = "Authorization"
        private set

    var data: String = Credentials.basic(username, password, charset)
        private set

    override fun toString(): String {
        return "$username:$password"
    }
    constructor(webDavConfig: Server.WebDavConfig) : this(webDavConfig.url,webDavConfig.username, webDavConfig.password)
}