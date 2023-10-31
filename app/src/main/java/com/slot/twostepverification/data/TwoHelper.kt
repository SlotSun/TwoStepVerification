package com.slot.twostepverification.data

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.slot.twostepverification.data.database.TwoDatabase
import com.slot.twostepverification.data.entity.VerificationItem


/**
 * 数据持久化辅助类
 */
object TwoHelper {
    private const val VERIFICATION_ITEM = "VerificationItem"

    /**
     * 插入+更新+删除
     */
    fun updateItems(items: List<VerificationItem>) {
        TwoDatabase.set(VERIFICATION_ITEM, Gson().toJson(items))
    }

    /**
     * 查询
     */
    suspend fun getItems(): List<VerificationItem> {
        return try {
            val json = TwoDatabase.get(VERIFICATION_ITEM)
            Gson().fromJson(json, object : TypeToken<List<VerificationItem>>() {}.type)
                ?: ArrayList()
        } catch (e: Exception) {
            Log.e(this.javaClass.name, e.message.toString())
            ArrayList()
        }
    }
}