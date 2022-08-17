package com.shop.base.ext

import android.util.Log
import com.shop.base.BuildConfig

const val BASE_TAG = "shop_log"

fun lllog(message: Any?, tag: String = BASE_TAG) {
    if (BuildConfig.DEBUG)
        Log.d(tag, message?.toString() ?: "message is null")
}

fun llloge(message: Any?, tag: String = BASE_TAG) {
    if (BuildConfig.DEBUG)
        Log.e(tag, message?.toString() ?: "message is null")
}

fun Any?.lllog() {
    lllog(message = this)
}

fun Any?.llloge() {
    llloge(message = this)
}