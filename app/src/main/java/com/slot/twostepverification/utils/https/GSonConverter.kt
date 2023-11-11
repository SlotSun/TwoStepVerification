package com.slot.twostepverification.utils.https

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import java.lang.reflect.Type
import kotlin.math.ceil

class GSonConverter : CoroutineHttp.Converter {

    companion object {
        // 实例化
        fun create(): GSonConverter {
            return GSonConverter()
        }
    }

    private val gSon = Gson()


    /**
     * 封装Gson类的实现
     */
    override fun <T> converter(responseBody: ResponseBody, type: Class<T>): T {
        val jsonReader = gSon.newJsonReader(responseBody.charStream())
        // 注册 序列化类型
        val adapter = gSon.getAdapter(type)
        return responseBody.use {
            adapter.read(jsonReader)
        }
    }

    override fun <T> fromJson(json: String, classOfT: Class<T>): T {
        return gSon.fromJson(json, classOfT)
    }

}
