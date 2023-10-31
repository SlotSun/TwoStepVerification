package com.slot.twostepverification.utils.decode

import com.google.common.io.BaseEncoding
import java.nio.charset.StandardCharsets


object Base64 {
    @Throws(EncodingException::class)
    fun decode(s: String?): ByteArray {
        return try {
            BaseEncoding.base64().decode(s)
        } catch (e: IllegalArgumentException) {
            throw EncodingException(e)
        }
    }

    @Throws(EncodingException::class)
    fun decode(s: ByteArray?): ByteArray {
        return decode(String(s!!, StandardCharsets.UTF_8))
    }

    fun encode(data: ByteArray?): String {
        return BaseEncoding.base64().encode(data)
    }
}
