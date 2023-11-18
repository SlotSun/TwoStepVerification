package com.slot.twostepverification.data.entity

import android.os.Parcelable
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class Cookie(
    @PrimaryKey
    var url: String = "",
    var cookie: String = ""
) : Parcelable