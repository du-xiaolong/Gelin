package com.shop.base.ext

import android.app.Activity
import androidx.fragment.app.Fragment
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * @author dxl
 * @date 2022-08-17  周三
 */

class Params<T : Any>(val key: String) : ReadOnlyProperty<Any?, T?> {

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        if (thisRef is Fragment) {
            return thisRef.arguments?.get(key) as T?
        } else if (thisRef is Activity) {
            return thisRef.intent?.extras?.get(key) as T?
        }
        return null
    }


}