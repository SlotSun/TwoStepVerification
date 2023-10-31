package com.slot.twostepverification.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.slot.twostepverification.data.entity.Two

@Dao
interface TwoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(two: Two): Long

    @Delete
    suspend fun delete(two: Two): Int

    @Update
    suspend fun update(two: Two): Int

    @Query("SELECT * FROM two_table WHERE first = :key ORDER BY id DESC LIMIT 1")
    suspend fun findByKey(key: String): Two?
}