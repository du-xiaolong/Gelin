package com.shop.base.common

import android.os.Bundle
import android.view.WindowManager
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ktx.immersionBar
import com.shop.base.util.inflateBindingWithGeneric
import java.lang.reflect.ParameterizedType

/**
 * activity基类
 * @author dxl
 * @date 2022-03-21
 */
abstract class BaseVbActivity<VM : BaseViewModel, VB : ViewBinding> : BaseActivity() {
    lateinit var viewModel: VM
    lateinit var vb: VB

    protected val activity: BaseVbActivity<VM, VB>
        get() = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        beforeInit()

        //获取泛型的类
        val type =
            (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<VM>
        viewModel = ViewModelProvider(this).get(type)

        vb = inflateBindingWithGeneric(layoutInflater)
        setContentView(vb.root)

        initImmersionBar()

        observe()
        init(savedInstanceState)
    }

    open fun initImmersionBar() {
        if (fullScreen()) {
            immersionBar {
                keyboardEnable(true, getKeyboardMode())
                fullScreen(true)
                keyboardEnable(true)
                hideBar(BarHide.FLAG_HIDE_BAR)
            }
        } else {
            //设置沉浸式样式
            immersionBar {
                keyboardEnable(true, getKeyboardMode())
                statusBarDarkFont(statusDarkFont)
                navigationBarColor(android.R.color.white)
                navigationBarDarkIcon(true)
            }
        }
    }

    open val statusDarkFont: Boolean = true


    open fun getKeyboardMode() = (WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
            or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

    open fun fullScreen() = false

    open fun beforeInit() {

    }

    open fun init(savedInstanceState: Bundle?) {

    }

    open fun observe() {
        viewModel.dialogStatus.observe(this) {
            if (it)
                showLoading()
            else
                dismissLoading()
        }

        viewModel.progressLiveData.observe(this) {
            showLoading(it ?: "加载中")
        }
    }



}