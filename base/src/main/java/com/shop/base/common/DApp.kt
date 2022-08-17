package com.shop.base.common

import android.app.Application
import android.content.Context
import androidx.annotation.CallSuper
import com.shop.base.util.ActivityHelper
import com.tencent.mmkv.MMKV


/**
 * 主application
 * @author dxl
 * @date 2022-03-21
 */
open class DApp : Application() {

    companion object {
        lateinit var instance: DApp
        val appContext: Context by lazy { instance.applicationContext }

    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        init()
    }

    @CallSuper
    open fun init() {
        ActivityHelper.init(this)
        //初始化MMKV
        MMKV.initialize(this)
    }


}