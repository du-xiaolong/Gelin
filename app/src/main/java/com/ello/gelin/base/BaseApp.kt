package com.ello.gelin.base

import com.ello.gelin.utils.UmengUtil
import com.shop.base.common.DApp
import dagger.hilt.android.HiltAndroidApp

/**
 * @author dxl
 * @date 2022-08-16  周二
 */
@HiltAndroidApp
class BaseApp: DApp() {

    override fun init() {
        super.init()
        UmengUtil.init(this)
    }

}