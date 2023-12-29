package com.slot.twostepverification.utils.crypto.otp

import com.slot.twostepverification.utils.encoding.Hex
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class MOTP constructor(code: String, digits: Int) {
    private val _code: String = code
    private val _digits = digits

    companion object {

        @JvmStatic
        @Throws(NoSuchAlgorithmException::class)
        fun generateOTP(
            secret: ByteArray,
            algo: String,
            digits: Int,
            period: Int,
            pin: String,
            time: Long
        ): MOTP {
            val timeBasedCounter: Long = time / period
            val secretAsString = Hex.encode(secret)
            val toDigest = timeBasedCounter.toString() + secretAsString + pin
            val code = getDigest(algo, toDigest.encodeToByteArray())
            return MOTP(code = code, digits = digits)
        }
        @JvmStatic
        @Throws(NoSuchAlgorithmException::class)
        fun generateOTP(
            secret: ByteArray,
            algo: String,
            digits: Int,
            period: Int,
            pin: String
        ): MOTP {
            return generateOTP(
                secret, algo, digits, period, pin, System.currentTimeMillis() / 1000
            )
        }


        @Throws(NoSuchAlgorithmException::class)
        fun getDigest(algo: String, toDigest: ByteArray): String {
            val md = MessageDigest.getInstance(algo)
            val digest = md.digest(toDigest)
            return Hex.encode(digest)
        }
    }

    override fun toString(): String {
        return _code.substring(0, _digits)
    }

}