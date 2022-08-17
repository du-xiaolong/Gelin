package com.ello.gelin.net

import com.shop.base.net.IApi

/**
 * @author dxl
 * @date 2022-08-16  周二
 */
data class ApiResult<T>(
    override val msg: String? = null,
    override val code: Int?,
    override val data: T?,
    val status: Boolean
) :
    IApi<T>