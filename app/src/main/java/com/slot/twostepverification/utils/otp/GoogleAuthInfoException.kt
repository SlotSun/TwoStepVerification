package com.slot.twostepverification.utils.otp

import android.net.Uri


class GoogleAuthInfoException : Exception {
    private val _uri: Uri?

    constructor(uri: Uri?, cause: Throwable?) : super(cause) {
        _uri = uri
    }

    constructor(uri: Uri?, message: String?) : super(message) {
        _uri = uri
    }

    constructor(uri: Uri?, message: String?, cause: Throwable?) : super(message, cause) {
        _uri = uri
    }

    val isPhoneFactor: Boolean
        /**
         * Reports whether the scheme of the URI is phonefactor://.
         */
        get() = _uri != null && _uri.scheme != null && _uri.scheme == "phonefactor"
    override val message: String
        get() {
            val cause = cause
            return if (cause == null || this === cause || super.message != null && super.message == cause.message
            ) {
                super.message!!
            } else String.format("%s (%s)", super.message, cause.message)
        }
}