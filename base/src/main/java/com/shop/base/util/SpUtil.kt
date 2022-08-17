@file:Suppress("UNCHECKED_CAST")

package com.shop.base.util

import android.os.Parcelable
import com.shop.base.ext.llloge
import com.tencent.mmkv.MMKV


const val SP_NAME = "user"

object SpUtil {

    @JvmOverloads
    @JvmStatic
    fun <T> get(key: String, defaultVal: T, filename: String = SP_NAME): T {
        val mmkv = MMKV.mmkvWithID(filename)
        if (mmkv == null) {
            llloge("mmkv = null!!!")
            return defaultVal
        }
        return when (defaultVal) {
            is Boolean -> mmkv.decodeBool(key, defaultVal) as T
            is String -> mmkv.decodeString(key, defaultVal) as T
            is Int -> mmkv.decodeInt(key, defaultVal) as T
            is Long -> mmkv.decodeLong(key, defaultVal) as T
            is Float -> mmkv.decodeFloat(key, defaultVal) as T
            is Double -> mmkv.decodeDouble(key, defaultVal) as T
            is Set<*> -> mmkv.decodeStringSet(key, defaultVal as Set<String>) as T
            is Parcelable -> mmkv.decodeParcelable(key, defaultVal.javaClass, defaultVal) as T
            else -> throw IllegalArgumentException("Unrecognized default value $defaultVal")
        }
    }

    @JvmOverloads
    @JvmStatic
    fun <T> put(key: String, value: T, filename: String = SP_NAME) {
        val mmkv = MMKV.mmkvWithID(filename)
        if (mmkv == null) {
            llloge("mmkv = null!!!")
            return
        }
        when (value) {
            is Boolean -> mmkv.encode(key, value)
            is String -> mmkv.encode(key, value)
            is Int -> mmkv.encode(key, value)
            is Long -> mmkv.encode(key, value)
            is Float -> mmkv.encode(key, value)
            is Double -> mmkv.encode(key, value)
            is Parcelable -> mmkv.encode(key, value)
            is Set<*> -> mmkv.encode(key, value as Set<String>)
            else -> throw UnsupportedOperationException("Unrecognized value $value")
        }

    }

    @JvmStatic
    @JvmOverloads
    inline fun <reified T : Parcelable> getParcel(key: String, filename: String = SP_NAME): T? {
        val mmkv = MMKV.mmkvWithID(filename) ?: return null
        if (mmkv.containsKey(key).not()) return null
        return mmkv.decodeParcelable(key, T::class.java)
    }

    @JvmOverloads
    @JvmStatic
    fun remove(key: String, filename: String = SP_NAME) {
        val mmkv = MMKV.mmkvWithID(filename)
        if (mmkv == null) {
            llloge("mmkv = null!!!")
            return
        }
        mmkv.removeValueForKey(key)
    }

    @JvmOverloads
    @JvmStatic
    fun clearAll(filename: String = SP_NAME) {
        val mmkv = MMKV.mmkvWithID(filename)
        if (mmkv == null) {
            llloge("mmkv = null!!!")
            return
        }
        mmkv.clearAll()
    }
}
