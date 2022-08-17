package com.shop.base.view

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.CenterPopupView
import com.shop.base.R
import com.shop.base.databinding.DialogCommonBinding
import com.shop.base.ext.click
import com.shop.base.util.dp

/**
 * 通用对话框
 * @author dxl
 * @date 2022/7/6
 */
class CommonDialog(context: Context) : CenterPopupView(context) {

    private lateinit var vb: DialogCommonBinding

    //对话框文本内容
    private var content: String = ""

    //左边按钮点击事件
    private var onLeftButtonClick: (() -> Unit)? = null

    //右边按钮点击事件
    private var onRightButtonClick: (() -> Unit)? = null

    //左边按钮文本内容，为空或者null时不显示
    private var leftButton: String? = null

    //右边按钮文本内容，为空或者null时不显示
    private var rightButton: String? = null

    //图标
    @DrawableRes
    private var icon: Int = 0

    companion object {

        /**
         * 显示对话框
         *
         * @param context
         * @param content 对话框文本内容
         * @param icon 图标
         * @param leftButton 左边按钮文本内容，为空或者null时不显示
         * @param onLeftButtonClick 左边按钮点击事件
         * @param rightButton 右边按钮文本内容，为空或者null时不显示
         * @param onRightButtonClick 右边按钮点击事件
         */
        fun show(
            context: Context,
            content: String,
            @DrawableRes icon: Int,
            leftButton: String? = null,
            onLeftButtonClick: (() -> Unit)? = null,
            rightButton: String? = null,
            onRightButtonClick: (() -> Unit)? = null
        ) {
            XPopup.Builder(context)
                .dismissOnBackPressed(false)
                .dismissOnTouchOutside(false)
                .asCustom(CommonDialog(context).apply {
                    this.content = content
                    this.leftButton = leftButton
                    this.rightButton = rightButton
                    this.onLeftButtonClick = onLeftButtonClick
                    this.onRightButtonClick = onRightButtonClick
                    this.icon = icon
                })
                .show()
        }

        /**
         * 显示错误弹框
         *
         * @param context
         * @param content 对话框文本内容
         * @param leftButton 左边按钮文本内容，为空或者null时不显示
         * @param onLeftButtonClick 左边按钮点击事件
         * @param rightButton 右边按钮文本内容，为空或者null时不显示
         * @param onRightButtonClick 右边按钮点击事件
         */
        fun showError(
            context: Context,
            content: String,
            leftButton: String? = null,
            onLeftButtonClick: (() -> Unit)? = null,
            rightButton: String? = null,
            onRightButtonClick: (() -> Unit)? = null
        ) {
            show(
                context,
                content,
                R.drawable.ic_dialog_error,
                leftButton,
                onLeftButtonClick,
                rightButton,
                onRightButtonClick
            )
        }

        /**
         * 显示成功弹框
         *
         * @param context
         * @param content 对话框文本内容
         * @param leftButton 左边按钮文本内容，为空或者null时不显示
         * @param onLeftButtonClick 左边按钮点击事件
         * @param rightButton 右边按钮文本内容，为空或者null时不显示
         * @param onRightButtonClick 右边按钮点击事件
         */
        fun showSuccess(
            context: Context,
            content: String,
            leftButton: String? = null,
            onLeftButtonClick: (() -> Unit)? = null,
            rightButton: String? = null,
            onRightButtonClick: (() -> Unit)? = null
        ) {
            show(
                context,
                content,
                R.drawable.ic_dialog_success,
                leftButton,
                onLeftButtonClick,
                rightButton,
                onRightButtonClick
            )
        }

        /**
         * 显示警告弹框
         *
         * @param context
         * @param content 对话框文本内容
         * @param leftButton 左边按钮文本内容，为空或者null时不显示
         * @param onLeftButtonClick 左边按钮点击事件
         * @param rightButton 右边按钮文本内容，为空或者null时不显示
         * @param onRightButtonClick 右边按钮点击事件
         */
        fun showWarning(
            context: Context,
            content: String,
            leftButton: String? = null,
            onLeftButtonClick: (() -> Unit)? = null,
            rightButton: String? = null,
            onRightButtonClick: (() -> Unit)? = null
        ) {
            show(
                context,
                content,
                R.drawable.ic_dialog_warning,
                leftButton,
                onLeftButtonClick,
                rightButton,
                onRightButtonClick
            )
        }

    }


    override fun getImplLayoutId(): Int {
        return R.layout.dialog_common
    }

    override fun getMaxWidth(): Int {
        //限制最大宽度
        return 300.dp
    }

    override fun onCreate() {
        vb = DialogCommonBinding.bind(popupImplView)
        vb.ivClose.click {
            dismiss()
        }
        vb.tvInfo.text = content
        vb.ivIcon.setImageResource(icon)
        if (leftButton.isNullOrEmpty()) {
            vb.btnLeft.isVisible = false
        } else {
            vb.btnLeft.text = leftButton
            vb.btnLeft.click {
                onLeftButtonClick?.invoke()
                dismiss()
            }
        }
        if (rightButton.isNullOrEmpty()) {
            vb.btnRight.isVisible = false
        } else {
            vb.btnRight.text = rightButton
            vb.btnRight.click {
                onRightButtonClick?.invoke()
                dismiss()
            }
        }

    }


}