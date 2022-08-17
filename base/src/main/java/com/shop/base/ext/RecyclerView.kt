package com.shop.base.ext

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView
import com.shop.base.util.dp
import com.shop.base.view.LinearItemDecoration

/**
 *
 * @author dxl
 * @date 2022/4/1
 */

/**
 * RecyclerView添加分割线
 */
fun RecyclerView.addDivider(
    paddingLeftDp: Float = 0f,
    paddingRightDp: Float = 0f,
    dividerHeightPx: Int = 1,
    @ColorInt dividerColor: Int = Color.parseColor("#B6B6B6"),
    showLastLine: Boolean = false
) {
    this.addItemDecoration(
        LinearItemDecoration.Builder(context)
            .setSpan(dividerHeightPx)
            .setLeftPadding(paddingLeftDp.dp)
            .setRightPadding(paddingRightDp.dp)
            .setColor(dividerColor)
            .setShowLastLine(showLastLine)
            .build()
    )
}