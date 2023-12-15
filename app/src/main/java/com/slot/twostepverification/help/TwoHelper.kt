package com.slot.twostepverification.help

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.slot.twostepverification.data.database.TwoDatabase
import com.slot.twostepverification.data.entity.Authorization
import com.slot.twostepverification.data.entity.Cache
import com.slot.twostepverification.data.entity.VerificationItem
import com.slot.twostepverification.ui.home.TwoUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking


/**
 * 数据持久化辅助类
 */
object TwoHelper {
    private const val VERIFICATION_ITEM = "VerificationItem"
    private const val CACHE = "Cache"
    private const val AUTHORIZATION = "Authorization"
    private var _itemState = MutableStateFlow(TwoUiState())
    val itemState: StateFlow<TwoUiState> = _itemState.asStateFlow()
    private fun updateItemState(items:List<VerificationItem>) {
        _itemState.update {
            it.copy(
                listItem = items
            )
        }
    }

    /**
     * 插入+更新+删除
     */
    suspend fun updateItems(items: List<VerificationItem>) {
        // 每次对数据库操作 主界面刷新
        // 合并原来数据 去重
        val insItems = mutableListOf<VerificationItem>()
        val res = insItems.let {
            it.addAll(_itemState.value.listItem)
            it.addAll(items)
            it.distinct()
        }
        updateItemState(res)

        TwoDatabase.set(VERIFICATION_ITEM, Gson().toJson(res))
    }

    suspend fun delItems(item:VerificationItem){
        val insItems = mutableListOf<VerificationItem>()
        insItems.let {
            it.addAll(_itemState.value.listItem)
            it.remove(item)
        }
        updateItemState(insItems)
        TwoDatabase.set(VERIFICATION_ITEM, Gson().toJson(insItems))
    }


    fun updateCache(cache: Cache) {
        TwoDatabase.set(CACHE, Gson().toJson(cache))
    }

    fun updateAuth(auth: Authorization) {
        TwoDatabase.set(AUTHORIZATION, Gson().toJson(auth))
    }


    /**
     * 删除
     */

    suspend fun delAuth(){
        TwoDatabase.del(AUTHORIZATION)
    }
    // 查询 Cache
    suspend fun getCache(): Cache {
        return try {
            val cacheJson = TwoDatabase.get(CACHE)
            Gson().fromJson(cacheJson, object : TypeToken<Cache>() {}.type) ?: Cache()
        } catch (e: Exception) {
            Log.e(this.javaClass.name, e.message.toString())
            Cache()
        }
    }

    /**
     * 查询
     */
    suspend fun getItems(): List<VerificationItem> {
        val res:List<VerificationItem> = try {
            val json = TwoDatabase.get(VERIFICATION_ITEM)
            Gson().fromJson(json, object : TypeToken<List<VerificationItem>>() {}.type)
                ?: ArrayList()
        } catch (e: Exception) {
            Log.e(this.javaClass.name, e.message.toString())
            ArrayList()
        }
        updateItemState(res)
        return res
    }
    fun getSyncItems():List<VerificationItem> = runBlocking {
        getItems()
    }

    suspend fun getAuth(): Authorization? {
        return try {
            val json = TwoDatabase.get(AUTHORIZATION)
            Gson().fromJson(json, object : TypeToken<Authorization>() {}.type)
        } catch (e: Exception) {
            Log.e(this.javaClass.name, e.message.toString())
            null
        }
    }
}