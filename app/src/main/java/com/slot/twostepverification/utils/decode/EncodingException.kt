package com.slot.twostepverification.utils.decode

import java.io.IOException


class EncodingException : IOException {
    constructor(cause: Throwable?) : super(cause)
    constructor(message: String?) : super(message)
}
