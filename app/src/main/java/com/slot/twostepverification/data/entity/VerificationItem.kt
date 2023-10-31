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
    val length:Int?,
    val sha:String?,
    val counter:Int?,
    val used:Int? = 0
):Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VerificationItem

        if (type != other.type) return false
        if (name != other.name) return false
        if (vendor != other.vendor) return false
        if (key != null) {
            if (other.key == null) return false
            if (!key.contentEquals(other.key)) return false
        } else if (other.key != null) return false
        if (time != other.time) return false
        if (length != other.length) return false
        if (sha != other.sha) return false
        if (counter != other.counter) return false
        if (used != other.used) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type?.hashCode() ?: 0
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (vendor?.hashCode() ?: 0)
        result = 31 * result + (key?.contentHashCode() ?: 0)
        result = 31 * result + (time ?: 0)
        result = 31 * result + (length ?: 0)
        result = 31 * result + (sha?.hashCode() ?: 0)
        result = 31 * result + (counter ?: 0)
        result = 31 * result + (used ?: 0)
        return result
    }
}