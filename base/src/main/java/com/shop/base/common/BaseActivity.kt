package com.shop.base.common

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.enums.PopupAnimation
import com.lxj.xpopup.impl.LoadingPopupView
import com.shop.base.R
import com.shop.base.ext.lllog
import com.shop.base.util.EventBusUtil
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * activity基类
 * @author dxl
 * @date 2022-5-10
 */
abstract class BaseActivity : AppCompatActivity() {

    private var loadingDialog: LoadingPopupView? = null

    private var mCompositeDisposable: CompositeDisposable? = null

    open fun registerEventBus() = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //当前页面
        lllog(this.localClassName, "页面")
        if (registerEventBus()) {
            EventBusUtil.register(this)
        }
    }

    fun showLoading(text: String = "加载中", dismissOnBack: Boolean = false) {
        if (loadingDialog == null) {
            loadingDialog = XPopup.Builder(this).dismissOnBackPressed(dismissOnBack)
                .popupAnimation(PopupAnimation.NoAnimation)
                .dismissOnTouchOutside(false)
                .asLoading(text, R.layout.layout_loading_dialog)
        }
        loadingDialog?.setTitle(text)?.show()
    }

    fun dismissLoading() {
        loadingDialog?.dismiss()
    }

    fun Disposable?.disposeOnDestroy() {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = CompositeDisposable()
        }
        this?.let { mCompositeDisposable?.add(it) }
    }


    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable?.dispose()
        if (registerEventBus()) {
            EventBusUtil.unregister(this)
        }
    }



}
