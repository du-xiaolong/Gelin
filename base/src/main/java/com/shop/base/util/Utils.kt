package com.shop.base.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import android.graphics.drawable.GradientDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@ColorInt
fun getPrimaryColor(context: Context, attrId: Int): Int {
    val typedValue = TypedValue()
    context.theme.resolveAttribute(attrId, typedValue, true)
    return ContextCompat.getColor(context, typedValue.resourceId)
}

fun Context?.getActivity(): Activity? {
    if (this == null) return null
    if (this is Activity) return this
    if (this is ContextWrapper) {
        val baseContext = this.baseContext
        if (baseContext is Activity) {
            return baseContext
        }
    }
    return null
}

val Number.dp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    ).toInt()

/**
 * 延迟执行
 */
fun LifecycleOwner.delayDo(timeMills: Long, block: (() -> Unit)? = null) {
    lifecycleScope.launch {
        delay(timeMills)
        runCatching {
            block?.invoke()
        }
    }
}


/**
 * 动态设置shape背景的颜色
 */
fun setDrawableShapeColor(
    context: Context,
    view: View,
    @ColorInt newColor: Int
) {
    val drawable = view.background
    if (drawable is GradientDrawable) {
        drawable.setColor(newColor)
    }
}

/**
 * 判断网络是否连接
 */
fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val nw = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            //for other device how are able to connect with Ethernet
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    } else {
        val nwInfo = connectivityManager.activeNetworkInfo ?: return false
        return nwInfo.isConnected
    }
}
