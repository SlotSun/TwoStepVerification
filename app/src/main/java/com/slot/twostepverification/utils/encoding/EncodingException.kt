package com.slot.twostepverification.utils.encoding

import java.io.IOException

class EncodingException : IOException {
    constructor(cause: Throwable?) : super(cause)
    constructor(message: String?) : super(message)
}