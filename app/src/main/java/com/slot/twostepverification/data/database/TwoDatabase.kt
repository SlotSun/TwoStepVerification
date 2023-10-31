package com.slot.twostepverification.data.database

import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.slot.twostepverification.data.dao.TwoDao
import com.slot.twostepverification.data.entity.Two
import com.slot.twostepverification.data.provider.BaseContentProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * 对RoomDatabase进行封装
 */
@Database(entities = [Two::class], version = 1, exportSchema = false)
abstract class TwoDatabase:RoomDatabase() {
    companion object {
        @Volatile
        private var database: TwoDatabase? = null

        private fun getDB() = database ?: synchronized(TwoDatabase::class.java) {
            database ?: Room.databaseBuilder(
                BaseContentProvider.get().applicationContext,
                TwoDatabase::class.java,
                TwoDatabase::class.java.simpleName
            ).build().also { db -> database = db }
        }

        @JvmStatic
        fun set(key: String, value: String) {
            getDB().setValue(key, value)
        }


        @JvmStatic
        suspend fun get(key: String): String {
            return getDB().getValue(key)
        }

        @JvmStatic
        fun get(key: String, result: (String) -> Unit) {
            getDB().getValue(key, result)
        }

        fun closeDatabase() {
            getDB().close()
        }
    }



    abstract fun getDao():TwoDao


    // 在外部处理后（包括删除） 插入
    fun setValue(key: String, value: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                var two = getDao().findByKey(key)
                if (two == null) {
                    two = Two(key = key, value = value)
                    getDao().insert(two)
                } else {
                    two.value = value
                    getDao().update(two)
                }
            } catch (e: Exception) {
                Log.e(this.javaClass.name, e.message.toString())
            }
        }
    }

    suspend fun getValue(key: String): String {
        return try {
            getDao().findByKey(key)?.value ?: ""
        } catch (e: Exception) {
            ""
        }
    }
    fun getValue(key: String, result: (String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val value = try {
                getDao().findByKey(key)?.value ?: ""
            } catch (e: Exception) {
                ""
            }
            withContext(Dispatchers.Main) {
                result.invoke(value)
            }
        }
    }

    /**
     * 如果使用数据库频繁，则不建议每次操作后关闭
     */
    override fun close() {
        super.close()
//        database = null
    }
}