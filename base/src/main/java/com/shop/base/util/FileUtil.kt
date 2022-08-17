package com.shop.base.util

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.FileUtils
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.shop.base.common.DApp
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt


/**
 * @author dxl
 * @date 2022-03-21
 */

object FileUtil {


    fun getUriForFile(context: Context, file: File): Uri {
        return getUriForFile(context, file, "")
    }

    fun getUriForFile(context: Context, filePath: String): Uri {
        return getUriForFile(context, File(filePath))
    }

    fun getUriForFile(context: Context, file: File, authority: String): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(context, authority, file)
        } else {
            Uri.fromFile(file)
        }
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    private fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        if (uri == null) {
            return null
        }
        val column = MediaStore.Images.Media.DATA
        val projection = arrayOf(column)
        var result: String? = null
        kotlin.runCatching {
            val cursor =
                context.contentResolver.query(uri, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                result = cursor.getString(index)
            }
            cursor?.close()
        }
        return result
    }


    fun getFilePathByUri(context: Context, uri: Uri): String? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && DocumentsContract.isDocumentUri(
                context,
                uri
            )
        ) {
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":")
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri =
                    ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        id.toLong()
                    )
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                when (type) {
                    "image" -> {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    }
                    "video" -> {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    }
                    "audio" -> {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                }
                val selection = MediaStore.Images.Media._ID + "=?"
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } // MediaStore (and general)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return uriToFileApiQ(context, uri)
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            // Return the remote address
            return if (isGooglePhotosUri(uri)) {
                uri.lastPathSegment
            } else getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }


    fun uri2File(uri: Uri?): File? {
        uri ?: return null
        val path = getFilePathByUri(DApp.appContext, uri) ?:return null
        val file = File(path)
        if (file.exists().not()) {
            return null
        }
        return file
    }


    @SuppressLint("Range")
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun uriToFileApiQ(context: Context, uri: Uri): String? {
        var file: File? = null
        //android10以上转换
        if (uri.scheme == ContentResolver.SCHEME_FILE) {
            file = uri.path?.let { File(it) }
        } else if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            //把文件复制到沙盒目录
            val contentResolver = context.contentResolver
            val cursor = contentResolver.query(uri, null, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                val displayName =
                    cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                kotlin.runCatching {
                    val inputStream = contentResolver.openInputStream(uri)
                    val cache = File(
                        context.externalCacheDir?.absolutePath,
                        "${(Math.random() * 1000).roundToInt()}${displayName}"
                    )
                    val fos = FileOutputStream(cache)
                    inputStream?.let { FileUtils.copy(it, fos) }
                    file = cache
                    fos.close()
                    inputStream?.close()
                    cursor.close()
                }
            }
        }
        return file?.absolutePath
    }

    /**
     * 获取 Uri Cursor 对应条件的数据行 display_name 字段
     * @param uri [Uri]
     * @return 对应条件的数据行 display_name 字段
     */
    fun getDisplayNameColumn(context: Context, uri: Uri): String? {
        val column = OpenableColumns.DISPLAY_NAME
        val projection = arrayOf(column)

        kotlin.runCatching {
            val cursor = context.contentResolver.query(
                uri, projection, null, null, null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex: Int = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(columnIndex).also { cursor.close() }
            }
        }
        return null
    }


}