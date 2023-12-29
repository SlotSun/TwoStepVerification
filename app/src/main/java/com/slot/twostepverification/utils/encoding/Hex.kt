package com.slot.twostepverification.utils.encoding

import com.google.common.io.BaseEncoding

object Hex {
    @Throws(EncodingException::class)
    fun decode(s: String): ByteArray {
        return try {
            BaseEncoding.base16().decode(s.uppercase())
        } catch (e: IllegalArgumentException) {
            throw EncodingException(e)
        }
    }

    fun encode(data: ByteArray): String {
        return BaseEncoding.base16().lowerCase().encode(data)
    }
}