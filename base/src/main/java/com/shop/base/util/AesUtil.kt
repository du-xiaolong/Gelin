package com.shop.base.util

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * AES是加密方式 CBC是工作模式 PKCS5Padding是填充模式
 */
object AesUtil {

    private const val CBC_PKCS5_PADDING = "AES/CBC/PKCS5Padding"
    private const val AES = "AES"

    /**
     * 加密
     */
    fun encrypt(cleartext: String, key: String, iv: String): String? {
        if (cleartext.isEmpty()) return cleartext
        return kotlin.runCatching {
            val sKeySpec = SecretKeySpec(key.toByteArray(), AES)
            val cipher = Cipher.getInstance(CBC_PKCS5_PADDING)
            cipher.init(Cipher.ENCRYPT_MODE, sKeySpec, IvParameterSpec(iv.toByteArray()))
            val result = cipher.doFinal(cleartext.toByteArray())
            Base64.encodeToString(result, Base64.DEFAULT)
        }.getOrNull()
    }

    /**
     * 解密
     */
    fun decrypt(encrypted: String?, key: String, iv: String): String? {
        if (encrypted.isNullOrEmpty()) return null
        return kotlin.runCatching {
            val enc = Base64.decode(encrypted, Base64.DEFAULT)
            val sKeySpec = SecretKeySpec(key.toByteArray(), AES)
            val cipher = Cipher.getInstance(CBC_PKCS5_PADDING)
            cipher.init(Cipher.DECRYPT_MODE, sKeySpec, IvParameterSpec(iv.toByteArray()))
            val result = cipher.doFinal(enc)
            return String(result)
        }.getOrNull()
    }
}