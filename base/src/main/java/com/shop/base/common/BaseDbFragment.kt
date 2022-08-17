package com.shop.base.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.shop.base.util.inflateBindingWithGeneric
import java.lang.reflect.ParameterizedType

/**
 * fragment基类，使用dataBinding
 * @author dxl
 * @date 2022-03-21
 */
abstract class BaseDbFragment<VM : BaseViewModel, VB : ViewDataBinding>(@LayoutRes val layoutId: Int) :
    BaseFragment() {

    lateinit var viewModel: VM
    lateinit var vb: VB

    var firstLoad = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe()
        initView()
        initData()
    }

    override fun onResume() {
        super.onResume()
        if (firstLoad) {
            firstLoad = false
            lazyLoad()
        }
    }


    open fun lazyLoad() {

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initViewModel()
        vb = DataBindingUtil.inflate(inflater, layoutId, container, false)
        vb.lifecycleOwner = this
        vb.setVariable(viewModel.variableId(), viewModel)
        vb.executePendingBindings()
        return vb.root
    }

    open fun initData() {

    }

    open fun initView() {

    }

    open fun observe() {
        viewModel.dialogStatus.observe(viewLifecycleOwner) {
            if (it) {
                showLoading()
            } else {
                dismissLoading()
            }
        }

        viewModel.progressLiveData.observe(viewLifecycleOwner) {
            showLoading(it ?: "加载中")
        }
    }

    private fun initViewModel() {
        val type =
            (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<VM>
        viewModel = ViewModelProvider(this).get(type)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        if (::vb.isInitialized) {
            vb.unbind()
        }
    }

}