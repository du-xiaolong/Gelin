package com.shop.base.ext

import android.graphics.Color
import android.view.Gravity
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.blankj.utilcode.util.ToastUtils
import java.util.regex.Pattern

/**
 * 是否为数字
 */
fun String.isNumber(): Boolean {
    return Pattern.compile("^[-+]?[\\d]*$").matcher(this).matches()
}

/**
 * 获取json value
 */
fun String?.getJsonValue(key: String): String {
    if (this.isNullOrEmpty()) return ""
    return kotlin.runCatching {
        JSON.parseObject(this).getString(key)
    }.getOrElse { "" }
}

/**
 * 字符串转json对象（不为空）
 */
fun String?.toJsonObject(): JSONObject {
    return kotlin.runCatching { JSON.parseObject(this) }
        .getOrElse { JSONObject() }
}

fun <T> String.parseObject(clazz: Class<T>): T? {
    return kotlin.runCatching {
        JSON.parseObject(this, clazz)
    }.getOrNull()
}

fun <T> String?.parseArray(key: String? = null, clazz: Class<T>): List<T> {
    if (this.isNullOrEmpty()) return emptyList()
    return kotlin.runCatching {
        var text = this
        if (key != null) {
            text = this.toJsonObject().getString(key)
        }
        JSON.parseArray(text, clazz)
    }.getOrElse { emptyList() }

}

fun String.toast() {
    ToastUtils.make()
        .setBgColor(Color.parseColor("#999999"))
        .setTextColor(Color.WHITE)
        .setDurationIsLong(true)
        .show(this)
}

fun String.toastCenter() {
    ToastUtils.make()
        .setBgColor(Color.parseColor("#999999"))
        .setTextColor(Color.WHITE)
        .setGravity(Gravity.CENTER, 0, 0)
        .setDurationIsLong(true)
        .show(this)
}

/**
 * 字符串非空
 */
val String?.notNull: String
    get() = this ?: ""
