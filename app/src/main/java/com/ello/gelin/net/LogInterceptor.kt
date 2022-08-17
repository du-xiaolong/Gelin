package com.ello.gelin.net

import com.shop.base.ext.lllog
import com.shop.base.ext.llloge
import okhttp3.*
import okhttp3.internal.closeQuietly
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.zip.GZIPInputStream

/**
 * @author dxl
 * @date 2022-03-31
 */
class LogInterceptor(block: (LogInterceptor.() -> Unit)? = null) : Interceptor {

    init {
        block?.invoke(this)
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        return kotlin.runCatching {
            chain.proceed(request)
        }.onFailure {
            llloge(it, "接口-请求")
        }.onSuccess {
            logRequest(request, chain.connection())
            logResponse(it)
        }.getOrThrow()
    }

    private fun logResponse(response: Response) {
        StringBuilder()
            .run {
//                response.headers.joinToString {
//                    appendLine("响应Header ： ${it.first} = ${it.second}")
//                }
//                appendLine("响应protocol : ${response.protocol} code = ${response.code} message = ${response.message}")
//                appendLine("响应 request Url ${response.request.url}")
//                    .appendLine("响应 sendRequestTime: ${response.sentRequestAtMillis} receiveResponseTime : ${response.receivedResponseAtMillis}")
                val peekBody = response.peekBody(1024 * 1024L)

                val encoding = response.header("Content-Encoding")
                if ("gzip".equals(encoding, true)) {
                    val bytes = peekBody.byteStream()
                    //是否启用了压缩
                    val gzipInputStream = GZIPInputStream(bytes)
                    val reader = BufferedReader(InputStreamReader(gzipInputStream))
                    var str:String?
                    while (reader.readLine().also { str = it } != null) {
                        appendLine(str)
                    }
                    reader.closeQuietly()
                }else {
                    appendLine(peekBody.string())
                }
                lllog(this.toString(), "接口-响应")
            }
    }

    /**
     * 打印请求
     */
    private fun logRequest(request: Request, connection: Connection?) {
        StringBuilder("\n").run {
            append("请求method : ${request.method} url: ${request.url} ")
            request.headers.forEach {
                if (it.first == "Authorization") {
                    append("\n").append("请求header: ${it.first}=${it.second}")
                }
            }
            if (request.method == "POST") {
                val body = request.body

                if (body is FormBody) {
                    append("\n").append("POST params:")
                    for (i in 0 until body.size) {
                        append("\n").append(body.name(i)).append("=")
                            .append(body.value(i))
                    }
                }
            }
            lllog(this.toString(), "接口-请求")
        }
    }
}