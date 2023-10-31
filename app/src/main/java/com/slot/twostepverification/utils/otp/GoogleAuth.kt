package com.slot.twostepverification.utils.otp

import android.net.Uri
import android.util.Log
import com.google.protobuf.InvalidProtocolBufferException
import com.slot.twostepverification.data.entity.VerificationItem
import com.slot.twostepverification.utils.decode.EncodingException
import com.slot.twostepverification.utils.encoding.Base64
import googleauth.GoogleAuth.MigrationPayload

class GoogleAuth {


    @Throws(GoogleAuthInfoException::class)
    fun parseUri(s: String?): List<VerificationItem> {
        val uri = Uri.parse(s)
        if (uri == null) {
            throw GoogleAuthInfoException(uri, String.format("Bad URI format: %s", s))
        }
        return parseUri(uri)
    }

    companion object {
        val SCHEME = "otpauth"
        val SCHEME_EXPORT = "otpauth-migration"

        // 解析谷歌字符串：
        @Throws(GoogleAuthInfoException::class)
        fun parseUri(uri: Uri): List<VerificationItem> {
            var items = mutableListOf<VerificationItem>()
            val scheme = uri.scheme
            if (scheme == null || !scheme.equals(SCHEME_EXPORT)) {
                throw GoogleAuthInfoException(uri, "Unsupported protocol")
            }

            val host = uri.host
            if (host == null || !host.equals("offline")) {
                throw GoogleAuthInfoException(uri, "Unsupported host")
            }

            val data = uri.getQueryParameter("data") ?: throw GoogleAuthInfoException(
                uri,
                "Parameter 'data' is not set"
            )

            val payload: MigrationPayload
            try {
                val bytes = Base64.decode(data)
                payload = MigrationPayload.parseFrom(bytes)
            } catch (e: EncodingException) {
                throw GoogleAuthInfoException(uri, e)
            } catch (e: InvalidProtocolBufferException) {
                throw GoogleAuthInfoException(uri, e)
            }


            for (params in payload.otpParametersList) {
                try {
                    val type = when (params.type.toString()) {
                        "OTP_HOTP" -> {
                            "HOTP"
                        }

                        else -> {
                            "TOTP"
                        }
                    }
                    val sha = when (params.algorithm) {
                        MigrationPayload.Algorithm.ALGO_SHA1 -> {
                            "SHA1"
                        }
                        MigrationPayload.Algorithm.ALGO_INVALID->{
                            "SHA256"
                        }
                        else -> {"SHA1"}
                    }

                    items.add(
                        element = VerificationItem(
                            type = type,
                            name = params.name.toString(),
                            vendor = params.issuer.toString(),
                            key = params.secret.toByteArray(),
                            time = 30,
                            length = 6,
                            sha = sha,
                            counter = params.counter.toInt()
                        )
                    )
                    Log.d("secret", "${params.secret}")
                } catch (e: Exception) {
                    throw e
                }
            }
            return items
        }
    }

}