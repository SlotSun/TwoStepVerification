package com.slot.twostepverification.data.entity

import android.os.Parcelable
import androidx.room.PrimaryKey
import com.google.gson.Gson
import kotlinx.parcelize.Parcelize

@Parcelize
data class VerificationItem(
    val type:String?,
    val name:String?,
    val vendor:String?,
    val key:ByteArray?,
    val time:Int?,
    val length:Int? = 0,
    val sha:String? = "SHA1",
    val counter:Int? = 0,
    val used:Int? = 0
):Parcelable {
}