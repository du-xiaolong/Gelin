package com.shop.base.net

/**
 * 接口通用返回结果
 * @author dxl
 * @date 2022-03-21
 */
interface IApi<T> {
    val msg: String?
    val code: Int?
    val data: T?
    val isSuccess: Boolean
        get() = code == 200 || code == 0
    val isFail: Boolean
        get() = isSuccess.not()
}

inline fun <reified T : IApi<*>> T.requireSuccess(): T {
    if (isFail) throw ApiException(this)
    return this
}
