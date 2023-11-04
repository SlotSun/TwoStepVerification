package com.slot.twostepverification.utils.otp

import android.net.Uri
import android.util.Log
import com.google.protobuf.InvalidProtocolBufferException
import com.slot.twostepverification.const.locale
import com.slot.twostepverification.data.entity.VerificationItem
import com.slot.twostepverification.utils.encoding.Base32
import com.slot.twostepverification.utils.encoding.Base64
import com.slot.twostepverification.utils.encoding.EncodingException
import com.slot.twostepverification.utils.encoding.Hex
import googleauth.GoogleAuth.MigrationPayload
import java.nio.charset.StandardCharsets

class GoogleAuth {


    companion object {
        private const val SCHEME = "otpauth"
        private const val SCHEME_EXPORT = "otpauth-migration"

        /**
         *  统一解析二步验证链接
         *  @param uri 二步验证链接
         *  @result 解析结果
         */
        @Throws(GoogleAuthInfoException::class)
        fun parseUri(uri: Uri): List<VerificationItem> {
            val scheme = uri.scheme
            val items: List<VerificationItem> = if (scheme != null) {
                when (scheme) {
                    SCHEME_EXPORT -> {
                        parseExportUri(uri = uri)
                    }

                    SCHEME -> {
                        parseInputUri(uri = uri)
                    }

                    else -> {
                        listOf()
                    }
                }
            } else {
                throw GoogleAuthInfoException(uri, locale("Notice_valid_data"))
            }
            return items
        }

        // 解析谷歌字符串：
        @Throws(GoogleAuthInfoException::class)
        fun parseExportUri(uri: Uri): List<VerificationItem> {
            val items = mutableListOf<VerificationItem>()
            val host = uri.host
            if (host == null || host != "offline") {
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
                    val sha = if (params.algorithm == MigrationPayload.Algorithm.ALGO_SHA1) {
                        "SHA1"
                    } else {
                        "SHA256"
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

        @Throws(EncodingException::class)
        fun parseSecret(s: String): ByteArray {
            var s = s
            s = s.trim { it <= ' ' }.replace("-", "").replace(" ", "")
            return Base32.decode(s)
        }

        /**
         * 解析非导出和手动输入的二步验证
         */
        @Throws(GoogleAuthInfoException::class)
        fun parseInputUri(uri: Uri): List<VerificationItem> {
            val scheme: String? = uri.scheme
            if (scheme != SCHEME && scheme != MotpInfo.SCHEME) {
                throw GoogleAuthInfoException(uri, "Unsupported protocol:$scheme")
            }


            // secret 是必须的
            val encodedSecret = uri.getQueryParameter("secret")
                ?: throw GoogleAuthInfoException(uri, "Parameter 'secret' is not present")

            val secret = try {
                //这句是个废话
                if (scheme == MotpInfo.SCHEME) {
                    Hex.decode(encodedSecret)
                } else {
                    parseSecret(encodedSecret)
                }
            } catch (e: EncodingException) {
                throw GoogleAuthInfoException(uri, "Bad secret", e)
            }

            if (secret.isEmpty()) {
                throw GoogleAuthInfoException(uri, "Secret is empty")
            }

            lateinit var info: OtpInfo
            // 填充 item
            var issuer = ""
            val type: String?
            try {
                type = if (scheme == MotpInfo.SCHEME) {
                    MotpInfo.ID
                } else {
                    uri.host
                }
                if (type != null) {
                    val period: String?
                    when (type) {
                        "totp" -> {
                            val totpInfo: TotpInfo = TotpInfo(secret)
                            val period = uri.getQueryParameter("period")
                            period?.run { totpInfo.period = period.toInt() }
                            info = totpInfo
                        }

                        "steam" -> {
                            val steamInfo = SteamInfo(secret)
                            period = uri.getQueryParameter("period")
                            if (period != null) {
                                steamInfo.setPeriod(period.toInt())
                            }
                            info = steamInfo
                        }

                        "hotp" -> {
                            val hotpInfo = HotpInfo(secret)
                            val counter = uri.getQueryParameter("counter")
                                ?: throw GoogleAuthInfoException(
                                    uri,
                                    "Parameter 'counter' is not present"
                                )
                            hotpInfo.setCounter(counter.toLong())
                            info = hotpInfo
                        }

                        YandexInfo.HOST_ID -> {
                            var pin = uri.getQueryParameter("pin")
                            if (pin != null) {
                                pin = String(
                                    parseSecret(pin),
                                    StandardCharsets.UTF_8
                                )
                            }

                            info = YandexInfo(secret, pin)
                            issuer = info.getType()
                        }

                        MotpInfo.ID -> {
                            info = MotpInfo(secret)
                        }
                    }
                } else {
                    throw GoogleAuthInfoException(uri, "Host not present in URI")
                }
            } catch (e: OtpInfoException) {
                throw GoogleAuthInfoException(uri, e)
            } catch (e: NumberFormatException) {
                throw GoogleAuthInfoException(uri, e)
            } catch (e: EncodingException) {
                throw GoogleAuthInfoException(uri, e)
            }

            val path = uri.path
            val label = if (path.isNullOrEmpty()) {
                ""
            } else {
                path.substring(1)
            }

            val accountName: String
            if (label.contains(":")) {
                val strs = label.split(":")
                if (strs.size == 2) {
                    issuer = strs[0]
                    accountName = strs[1]
                } else {
                    accountName = label
                }
            } else {
                val issuerParam = uri.getQueryParameter("issuer")
                if (issuer.isEmpty()) {
                    issuer = issuerParam ?: ""
                }
                accountName = label
            }
            val items = mutableListOf<VerificationItem>()
            try {
                val algorithm = uri.getQueryParameter("algorithm")
                if (algorithm != null) {
                    info.setAlgorithm(algorithm)
                }
                val digits = uri.getQueryParameter("digits")
                if (digits != null) {
                    info.digits = digits.toInt()
                }
            } catch (e: OtpInfoException) {
                throw GoogleAuthInfoException(uri, e)
            }
            items.add(
                element = VerificationItem(
                    type = type,
                    name = accountName,
                    vendor = issuer,
                    key = info.secret,
                    time = 30,
                    length = 6,
                    sha = info.getAlgorithm(false),
                    counter = if (info is HotpInfo) {
                        info.counter.toInt()
                    } else {
                        0
                    }
                )
            )
            return items
        }
    }

}