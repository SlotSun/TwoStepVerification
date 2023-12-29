package com.slot.twostepverification.utils.encoding

import com.google.common.io.BaseEncoding
import java.nio.charset.StandardCharsets

object Base32 {

    @Throws(EncodingException::class)
    fun decode(s: String): ByteArray {
        return try {
            BaseEncoding.base32().decode(s.uppercase())
        } catch (e: IllegalArgumentException) {
            throw EncodingException(e)
        }
    }

    fun encode(data: ByteArray): String {
        return BaseEncoding.base32().omitPadding().encode(data)
    }

    fun encode(s: String): String {
        val bytes = s.toByteArray(StandardCharsets.UTF_8)
        return encode(bytes)
    }
}