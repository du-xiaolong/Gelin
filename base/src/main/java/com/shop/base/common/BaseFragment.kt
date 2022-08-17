package com.shop.base.common

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.enums.PopupAnimation
import com.lxj.xpopup.impl.LoadingPopupView
import com.shop.base.R
import com.shop.base.util.EventBusUtil

/**
 * Fragment基类
 * @author dxl
 * @date 2022-5-10
 */
open class BaseFragment : Fragment() {

    private var loadingDialog: LoadingPopupView? = null

    //启动activity
    private var launcher: ActivityResultLauncher<Intent>? = null

    //启动activity回传数据
    private var onResult: ((ActivityResult) -> Unit)? = null

    open fun registerEventBus() = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (registerEventBus()) {
            EventBusUtil.register(this)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        baseInit()
    }

    open fun baseInit() {

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            onResult?.invoke(it)
        }
    }


    fun launch(intent: Intent, onResult: ((ActivityResult) -> Unit)? = null) {
        this.onResult = onResult
        launcher?.launch(intent)
    }

    fun showLoading(text: String = "加载中", dismissOnBack: Boolean = false) {
        if (loadingDialog == null) {
            loadingDialog = XPopup.Builder(requireContext()).dismissOnBackPressed(dismissOnBack)
                .popupAnimation(PopupAnimation.NoAnimation)
                .asLoading(text, R.layout.layout_loading_dialog)
        }
        loadingDialog?.setTitle(text)?.show()
    }

    fun dismissLoading() {
        loadingDialog?.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (registerEventBus()) {
            EventBusUtil.unregister(this)
        }
    }


}
