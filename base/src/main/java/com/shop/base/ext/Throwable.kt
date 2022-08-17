package com.shop.base.ext

import com.alibaba.fastjson.JSONException
import com.google.gson.JsonSyntaxException
import com.shop.base.net.ApiException
import retrofit2.HttpException
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException
import javax.net.ssl.SSLHandshakeException

/**
 * @author dxl
 * @date 2022-03-21
 */
fun Throwable?.format() = (when {
    this is ApiException -> {
        this.msg
    }
    this is InterruptedIOException && this.message?.contains("timeout") == true -> {
        "当前网络不稳定，请稍候重试～"
    }
    this is HttpException && this.code() == 500 -> {
        "服务器开小差了，稍后再试吧~"
    }
    // 网络请求失败
    this is ConnectException
            || this is SocketTimeoutException
            || this is UnknownHostException
            || this is HttpException
            || this is SSLHandshakeException
            || this is SSLException ->
        "当前网络不稳定，请刷新重试～"
    // 数据解析错误
    this is JSONException || this is JsonSyntaxException || this?.cause is JSONException -> "数据解析异常，请刷新重试～"
    // 其他错误
    else -> this?.message
} ?: "请求失败").also {
    llloge(this?.message)
}


fun Throwable.isNetError() =
    // 网络请求失败
    this is ApiException
            || this is ConnectException
            || this is SocketTimeoutException
            || this is UnknownHostException
            || this is HttpException
            || this is SSLHandshakeException
            || this is SSLException
            || (this is InterruptedIOException && this.message?.contains("timeout") == true)

