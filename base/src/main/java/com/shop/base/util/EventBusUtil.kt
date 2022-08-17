package com.shop.base.util

import org.greenrobot.eventbus.EventBus

/**
 *
 * @author dxl
 * @date 2022-03-21
 */
object EventBusUtil {

    fun register(subscriber: Any) {
        EventBus.getDefault().register(subscriber)
    }

    fun unregister(subscriber: Any) {
        EventBus.getDefault().unregister(subscriber)
    }

    fun <T> sendEvent(event: Event<T>) {
        EventBus.getDefault().post(event)
    }

    fun <T> sendStickyEvent(event: Event<T>) {
        EventBus.getDefault().postSticky(event)
    }

    fun <T> sendEvent(code: Int, data: T?) {
        sendEvent(Event(code, data))
    }

    fun <T> sendStickyEvent(code: Int, data: T?) {
        sendStickyEvent(Event(code, data))
    }

}


data class Event<T>(val code: Int, val data: T?)