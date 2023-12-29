package com.slot.twostepverification.utils.otp

class OtpInfoException : Exception {
    constructor(cause: Throwable?) : super(cause)
    constructor(message: String?) : super(message)
}
