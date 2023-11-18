package com.slot.twostepverification.data.entity

import android.os.Parcelable
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class Cache(
    @PrimaryKey
    val key: String = "",
    var value: String? = null,
    var deadline: Long = 0L
) : Parcelable