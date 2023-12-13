package com.slot.twostepverification.utils.https

import androidx.collection.LruCache
import com.slot.twostepverification.help.TwoHelper
import com.slot.twostepverification.data.entity.Cache
import com.slot.twostepverification.utils.ACache
import com.slot.twostepverification.utils.memorySize

object CacheManager {

    /**
     * 最多只缓存50M的数据,防止OOM
     */
    private val memoryLruCache = object : LruCache<String, Any>(1024 * 1024 * 50) {

        override fun sizeOf(key: String, value: Any): Int {
            return value.toString().memorySize()
        }

    }

    /**
     *  saveTime 单位秒
     */
    fun put(key: String, value: Any, saveTime: Int = 0) {
        val deadline = if (saveTime == 0) 0 else System.currentTimeMillis()+saveTime*1000
        when(value){
            is ByteArray->ACache.get().put(key,value, saveTime)
            else->{
                val cache = Cache(key,value.toString(),deadline)
                putMemory(key,value)
                // 保存cache到数据库
                TwoHelper.updateCache(cache)
            }
        }
    }


    fun putMemory(key: String, value: Any) {
        memoryLruCache.put(key, value)
    }

    fun getFromMemory(key: String): Any? {
        return memoryLruCache.get(key)
    }

    fun deleteMemory(key: String) {
        memoryLruCache.remove(key)
    }

    suspend fun get(key: String): String? {
        getFromMemory(key)?.let {
            if (it is String) return it
        }
        val cache = TwoHelper.getCache()
        if (cache != null && (cache.deadline == 0L || cache.deadline > System.currentTimeMillis())) {
            putMemory(key, cache.value ?: "")
            return cache.value
        }
        return null
    }

    suspend fun getInt(key: String): Int? {
        return get(key)?.toIntOrNull()
    }

    suspend fun getLong(key: String): Long? {
        return get(key)?.toLongOrNull()
    }

    suspend fun getDouble(key: String): Double? {
        return get(key)?.toDoubleOrNull()
    }

    suspend fun getFloat(key: String): Float? {
        return get(key)?.toFloatOrNull()
    }

    fun getByteArray(key: String): ByteArray? {
        return ACache.get().getAsBinary(key)
    }
    fun putFile(key: String, value: String, saveTime: Int = 0) {
        ACache.get().put(key, value, saveTime)
    }

    fun getFile(key: String): String? {
        return ACache.get().getAsString(key)
    }
    fun delete(key: String) {
        TwoHelper.updateCache(Cache())
        deleteMemory(key)
        ACache.get().remove(key)
    }
}