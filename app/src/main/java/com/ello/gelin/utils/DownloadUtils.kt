package com.ello.gelin.utils

import com.shop.base.ext.requireNotNull
import okhttp3.*
import okhttp3.internal.closeQuietly
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

/**
 * @author dxl
 * @date 2022-08-26  周五
 */
object DownloadUtils {

    fun download(url: String?, saveFile: File, listener: DownloadListener) {
        if (url.isNullOrEmpty()) {
            listener.onResult(Result.failure(IllegalArgumentException("图片链接为空")))
            return
        }
        OkHttpClient().newCall(Request.Builder().url(url).build())
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    listener.onResult(Result.failure(e))
                }

                override fun onResponse(call: Call, response: Response) {
                    listener.onStart()
                    if (!saveFile.exists()) {
                        saveFile.createNewFile()
                    }
                    var inputStream: InputStream? = null
                    val fileOutputStream: FileOutputStream
                    val buf = ByteArray(2048)
                    var len: Int
                    try {
                        val body = response.body.requireNotNull { "下载失败" }
                        inputStream = body.byteStream()
                        val total = body.contentLength()
                        fileOutputStream = FileOutputStream(saveFile)
                        var sum = 0L
                        while (inputStream.read(buf).also { len = it } != -1) {
                            fileOutputStream.write(buf, 0, len)
                            sum += len
                            val progress = (sum * 1.0f / total * 100).toInt()
                            //下载中更新进度条
                            listener.onProgress(progress)
                        }
                        fileOutputStream.flush()
                        listener.onResult(Result.success(saveFile))
                    } catch (e: Exception) {
                        listener.onResult(Result.failure(e))
                    } finally {
                        inputStream?.closeQuietly()
                    }
                }

            })
    }

    interface DownloadListener {
        fun onStart() {}
        fun onProgress(progress: Int) {}
        fun onResult(result: Result<File>) {}
    }
}