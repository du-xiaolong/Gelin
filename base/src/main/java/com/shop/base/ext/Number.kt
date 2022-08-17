package com.shop.base.ext

import java.math.BigDecimal
import java.math.RoundingMode

/**
 *
 * @author dxl
 * @date 2022/5/9
 */

//"20.300" -> "20.30"
fun String?.scaleToString(scale: Int = 2): String {
    return BigDecimal(this).setScale(scale, RoundingMode.HALF_UP).toPlainString()
}

//"20.300" -> "20.3"
fun String?.scaleToStringStripZero(scale: Int): String {
    return BigDecimal(this).setScale(scale, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString()
}

//异常情况都返回0
fun String?.toBigDecimal(): BigDecimal {
    return kotlin.runCatching {
        BigDecimal(this)
    }.getOrDefault(BigDecimal.ZERO)
}