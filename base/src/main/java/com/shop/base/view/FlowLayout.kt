package com.shop.base.view



import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import kotlin.math.max

/**
 * 流式布局
 * @author dxl
 * @date 2022/4/11
 */
class FlowLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    //所有行
    private val lines = mutableListOf<Line>()

    //当前行
    private var line: Line? = null


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        //实际可用大小
        val width = MeasureSpec.getSize(widthMeasureSpec) - paddingLeft - paddingRight
        val height = MeasureSpec.getSize(heightMeasureSpec) - paddingTop - paddingBottom

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        lines.clear()
        line = null

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            //测量所有child
            measureChild(child, widthMeasureSpec, heightMeasureSpec)
            if (line == null) {
                newLine()
            }

            //当前view需要的宽度
            val measuredWidth = child.measuredWidth
            if (line!!.children.isEmpty()) {
                line!!.addChild(child)
            } else {
                if (line!!.width + measuredWidth > width) {
                    //放不下了
                    newLine()
                }
                line!!.addChild(child)
            }
        }

        var totalHeight = paddingTop + paddingBottom
        lines.forEach { totalHeight += it.height }


        setMeasuredDimension(
            MeasureSpec.getSize(widthMeasureSpec),
            resolveSize(totalHeight, heightMeasureSpec)
        )
    }



    private fun newLine() {
        line = Line()
        lines.add(line!!)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var top = paddingTop
        lines.forEach {
            it.layout(paddingLeft, top )
            top += it.height
        }
    }


    inner class Line {
        val children = mutableListOf<View>()


        var height = 0
        var width = 0

        fun addChild(child: View) {
            children.add(child)
            height = max(height, child.measuredHeight)
            width += child.measuredWidth
        }

        fun layout(left: Int, top: Int) {
            var currentLeft = left
            for (child in children) {
                child.layout(
                    currentLeft,
                    top,
                    currentLeft + child.measuredWidth,
                    top + child.measuredHeight
                )

                currentLeft += child.measuredWidth
            }
        }
    }


}
