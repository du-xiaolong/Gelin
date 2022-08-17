package com.shop.base.util

import android.annotation.SuppressLint
import android.content.Context
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.enums.PopupAnimation
import com.lxj.xpopup.impl.LoadingPopupView
import com.shop.base.R

/**
 *
 * @author dxl
 * @date 2022/4/30
 */
object LoadingUtils {

    @SuppressLint("StaticFieldLeak")
    private var loadingDialog: LoadingPopupView? = null

    fun showLoading(context: Context, text: String = "加载中") {
        if (loadingDialog == null) {
            loadingDialog = XPopup.Builder(context)
                .popupAnimation(PopupAnimation.NoAnimation)
                .dismissOnTouchOutside(false)
                .dismissOnBackPressed(false)
                .asLoading(text, R.layout.layout_loading_dialog)
        }
        loadingDialog?.setTitle(text)?.show()
    }

    fun dismissLoading() {
        loadingDialog?.dismiss()
    }
}