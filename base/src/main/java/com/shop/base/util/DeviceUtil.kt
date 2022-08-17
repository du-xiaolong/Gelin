package com.shop.base.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Process
import android.provider.Settings
import android.text.method.NumberKeyListener
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.shop.base.common.DApp

/**
 * 获取AndroidID
 */
fun getAndroidId(): String =
    Settings.System.getString(DApp.instance.contentResolver, Settings.Secure.ANDROID_ID)

fun getScreenWidth(context: Context) = context.resources.displayMetrics.widthPixels

fun getScreenHeight(context: Context) = context.resources.displayMetrics.heightPixels

fun getScreenWidthDp(context: Context) = context.resources.displayMetrics.run {
    (widthPixels / density).toInt()
}


fun getScreenHeightDp(context: Context): Int = context.resources.displayMetrics.run {
    (heightPixels / density).toInt()
}

/**
 * 重启App
 */
fun restartApp() {
    val intent = DApp.instance.packageManager.getLaunchIntentForPackage(DApp.instance.packageName)
    if (intent != null) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION)
        DApp.instance.startActivity(intent)
        Process.killProcess(Process.myPid())
    }
}

/**
 * 卸载应用
 */
fun uninstallApp() {
    val intent = Intent(Intent.ACTION_DELETE)
    intent.data = Uri.parse("package:${DApp.instance.packageName}")
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    DApp.instance.startActivity(intent)
}

/**
 * EditText获取焦点并弹出键盘
 */
fun EditText?.focusAndShowKeyboard() {
    this?.let {
        isFocusable = true
        isFocusableInTouchMode = true
        setSelection(text.length)
        requestFocus()
        val systemService = context.getSystemService(Context.INPUT_METHOD_SERVICE)
        if (systemService is InputMethodManager) {
            systemService.showSoftInput(this, 0)
        }
    }
}

fun EditText?.focusAndShowKeyboardDelayed() {
    this?.postDelayed({
        this.focusAndShowKeyboard()
    }, 200)
}


fun EditText?.hideInputKeyboard() {
    this?.let {
        val systemService = context.getSystemService(Context.INPUT_METHOD_SERVICE)
        if (systemService is InputMethodManager) {
            systemService.hideSoftInputFromWindow(windowToken, 0)
        }
    }
}

fun Activity.hideInputKeyboard() {
    currentFocus?.let {
        val systemService = getSystemService(Context.INPUT_METHOD_SERVICE)
        if (systemService is InputMethodManager) {
            systemService.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }
}

/**
 * 设置键盘弹出方式为数字
 */
fun EditText?.setNumberInputMethod() {
    this?.keyListener = object : NumberKeyListener() {
        override fun getInputType(): Int {
            return 3
        }

        override fun getAcceptedChars(): CharArray {
            return charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
        }

    }
}

