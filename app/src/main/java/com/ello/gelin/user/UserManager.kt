package com.ello.gelin.user

import com.shop.base.util.SpUtil

/**
 * @author dxl
 * @date 2022-08-16  周二
 */
object UserManager {

    var dytCookie: String = ""
        get() {
            if (field.isEmpty()) {
                field = SpUtil.get("dyt_cookie", "")
            }
            return field
        }
        set(value) {
            field = value
            SpUtil.put("dyt_cookie", value)
        }

    var dlcCookie: String = ""
        get() {
            if (field.isEmpty()) {
                field = SpUtil.get("dlc_cookie", "")
            }
            return field
        }
        set(value) {
            field = value
            SpUtil.put("dlc_cookie", value)
        }
}