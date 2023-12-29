package com.slot.twostepverification.utils.crypto.otp

import kotlin.math.pow

class OTP(code: Int, digits: Int) {
    private val STEAM_ALPHABET = "23456789BCDFGHJKMNPQRTVWXY"

    private var _code = code
    private var _digits = digits

    fun getCode():Int{
        return  _code
    }

    fun getDigits():Int{
        return _digits
    }

    override fun toString(): String {
        val code = _code % 10.0.pow(_digits.toDouble()).toInt()
        // prepend zeroes if needed
        val res = StringBuilder(code.toString())
        while (res.length < _digits) {
            res.insert(0, "0")
        }

        return res.toString()
    }

    fun toSteamString():String{
        var code = _code
        val res = StringBuilder()

        for (i in 0 until _digits) {
            val c: Char = STEAM_ALPHABET.get(code % STEAM_ALPHABET.length)
            res.append(c)
            code /= STEAM_ALPHABET.length
        }
        return res.toString()
    }
}