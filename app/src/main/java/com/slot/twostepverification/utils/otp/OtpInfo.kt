package com.slot.twostepverification.utils.otp

import com.slot.twostepverification.utils.encoding.Base32
import com.slot.twostepverification.utils.encoding.EncodingException
import org.json.JSONException
import org.json.JSONObject
import java.io.Serializable
import java.util.Arrays
import java.util.Locale

abstract class OtpInfo : Serializable{


    private var _secret: ByteArray = byteArrayOf()
    private var _algorithm: String? = null
    private var _digits = 0


    companion object{
        const val DEFAULT_DIGITS = 6
        const val DEFAULT_ALGORITHM = "SHA1"
    }

    @Throws(OtpInfoException::class)
    constructor(secret:ByteArray):this(secret, DEFAULT_ALGORITHM, DEFAULT_DIGITS)

    @Throws(OtpInfoException::class)
    constructor(secret: ByteArray,algorithm:String,digits:Int){
        setSecret(secret)
        setAlgorithm(algorithm)
        setDigits(digits)
    }

    @Throws(OtpInfoException::class)
    abstract fun getOtp(): String

    @Throws(OtpInfoException::class)
    open fun checkSecret(){
        if (getSecret().isEmpty()){
            throw OtpInfoException("Secret is empty")
        }
    }


    open fun isDigitsValid(digits: Int): Boolean {
        // allow a max of 10 digits, as truncation will only extract 31 bits
        return digits in 1..10
    }

    open fun setSecret(secret: ByteArray) {
        _secret = secret
    }
    open fun getSecret(): ByteArray {
        return _secret
    }

    @Throws(OtpInfoException::class)
    open fun setDigits(digits: Int) {
        if (!isDigitsValid(digits)) {
            throw OtpInfoException(String.format("unsupported amount of digits: %d", digits))
        }
        _digits = digits
    }
    open fun getDigits(): Int {
        return _digits
    }

    open fun isAlgorithmValid(algorithm: String): Boolean {
        return algorithm == "SHA1" || algorithm == "SHA256" || algorithm == "SHA512" || algorithm == "MD5"
    }


    abstract fun getTypeId(): String

    open fun getType():String{
        return getTypeId().uppercase(Locale.ROOT)
    }

    @Throws(OtpInfoException::class)
    open fun setAlgorithm(algorithm: String) {
        var algorithm = algorithm
        if (algorithm.startsWith("Hmac")) {
            algorithm = algorithm.substring(4)
        }
        algorithm = algorithm.uppercase()
        if (!isAlgorithmValid(algorithm)) {
            throw OtpInfoException(String.format("unsupported algorithm: %s", algorithm))
        }
        _algorithm = algorithm
    }

    open fun getAlgorithm(java: Boolean): String? {
        return if (java) {
            "Hmac$_algorithm"
        } else _algorithm
    }

    open fun toJson(): JSONObject? {
        val obj = JSONObject()
        try {
            obj.put("secret", Base32.encode(getSecret()))
            obj.put("algo", getAlgorithm(false))
            obj.put("digits", getDigits())
        } catch (e: JSONException) {
            throw RuntimeException(e)
        }
        return obj
    }

    @Throws(OtpInfoException::class)
    open fun fromJson(type: String, obj: JSONObject): OtpInfo? {
        val info: OtpInfo
        try {
            val secret = Base32.decode(obj.getString("secret"))
            var algo = obj.getString("algo")
            val digits = obj.getInt("digits")

            // Special case to work around a bug where a user could accidentally
            // set the hash algorithm of a non-mOTP entry to MD5
            if (type != MotpInfo.ID && algo == "MD5") {
                algo = DEFAULT_ALGORITHM
            }
            info = when (type) {
                TotpInfo.ID -> TotpInfo(secret, algo, digits, obj.getInt("period"))
                SteamInfo.ID -> SteamInfo(secret, algo, digits, obj.getInt("period"))
                HotpInfo.ID -> HotpInfo(secret, algo, digits, obj.getLong("counter"))
                YandexInfo.ID -> YandexInfo(secret, obj.getString("pin"))
                MotpInfo.ID -> MotpInfo(secret, obj.getString("pin"))
                else -> throw OtpInfoException("unsupported otp type: $type")
            }
        } catch (e: EncodingException) {
            throw OtpInfoException(e)
        } catch (e: JSONException) {
            throw OtpInfoException(e)
        }
        return info
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is OtpInfo) {
            return false
        }
        val info : OtpInfo = other
        return getTypeId() == info.getTypeId() && Arrays.equals(
            getSecret(),
            info.getSecret()
        ) && getAlgorithm(false) == info.getAlgorithm(false) && getDigits() == info.getDigits()
    }
}