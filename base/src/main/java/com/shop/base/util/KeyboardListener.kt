package com.yinjiuyy.base.util

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver

/**
 * 键盘弹起监听
 * @author dxl
 * @date 2022-03-21
 */
class KeyboardListener(activity: Activity) {

    private val rootView: View = activity.window.decorView

    var rootViewVisibleHeight = 0

    private var onChanged: ((Boolean, Int) -> Unit)? = null


    private val onGlobalLayoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {

            //获取当前根视图在屏幕上显示的大小
            val r = Rect()
            rootView.getWindowVisibleDisplayFrame(r)

            val visibleHeight = r.height()
            if (rootViewVisibleHeight == 0) {
                rootViewVisibleHeight = visibleHeight
                return
            }

            //根视图显示高度没有变化，可以看作软键盘显示／隐藏状态没有改变
            if (rootViewVisibleHeight == visibleHeight) {
                return
            }

            //根视图显示高度变小超过200，可以看作软键盘显示了
            if (rootViewVisibleHeight - visibleHeight > 200) {
                onChanged?.invoke(true, rootViewVisibleHeight - visibleHeight)
                rootViewVisibleHeight = visibleHeight
                return
            }

            //根视图显示高度变大超过200，可以看作软键盘隐藏了
            if (visibleHeight - rootViewVisibleHeight > 200) {
                onChanged?.invoke(false, visibleHeight - rootViewVisibleHeight)
                rootViewVisibleHeight = visibleHeight
                return
            }
        }

    }

    init {
        rootView.viewTreeObserver.addOnGlobalLayoutListener(onGlobalLayoutListener)
    }

    fun addChangeEvent(onChanged: (Boolean, Int) -> Unit) {
        this.onChanged = onChanged

    }

    fun removeChangeEvent() {
        rootView.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalLayoutListener)
    }


}