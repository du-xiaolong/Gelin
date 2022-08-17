package com.shop.base.ext

import android.graphics.drawable.Drawable
import android.widget.TextView

/**
 *
 * @author dxl
 * @date 2022/5/20
 */

fun TextView.setLeftDrawable(drawable: Drawable?) {
    drawable?.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
    setCompoundDrawables(drawable, null, null, null)
}