package com.slot.twostepverification.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "two_table")
data class Two(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "first")
    val key: String,

    @ColumnInfo(name = "second")
    var value: String?

)
