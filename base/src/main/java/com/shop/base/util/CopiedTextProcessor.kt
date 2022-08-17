package com.shop.base.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.shop.base.common.DApp

object CopiedTextProcessor {

    /**
     * 观察剪贴板变化
     */
    fun observeClipboard(block: (text: String) -> Unit) {
        val clipboardManager = getClipManager()
        clipboardManager.addPrimaryClipChangedListener {
            getClipText()?.apply { block(this) }
        }
    }

    fun getClipText(): String? {
        val primaryClip = getClipManager().primaryClip
        if (primaryClip != null && primaryClip.itemCount > 0) {
            return primaryClip.getItemAt(0)?.text?.toString()
        }
        return null
    }

    private fun getClipManager() =
        DApp.instance.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager


    /**
     * 复制文本
     */
    fun copyText(text: String, block: (() -> Unit)? = null) {
        val clipboardManager = getClipManager()
        clipboardManager.setPrimaryClip(ClipData.newPlainText(null, text))
        block?.invoke()
    }


}