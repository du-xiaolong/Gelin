package com.shop.base.util

import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*

const val DATE_TIME_ZONE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ"
const val DATE_TIME_MS_ZONE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
const val DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss"
const val DATE_FORMAT = "yyyy-MM-dd"
const val DATE_MONTH_DAY_FORMAT = "MM-dd"
const val DATE_YEAR_MONTH = "yyyy-MM"
const val DATE_MONTH_DAY_HOUR_MINUTE = "MM-dd HH:mm"
const val MONTH = "MM"
const val TIME_FORMAT = "HH:mm:ss"
const val TIME_FORMAT2 = "HH:mm"


/**
 * 将Date格式时间转换为字符串 yyyy-MM-dd HH:mm:ss
 *
 * @param date   要转换的日期。
 * @param format 日期格式。如："yyyy-MM-dd"
 * @return 返回转换后的字符串，格式为yyyy-MM-dd HH:mm:ss
 */
fun Date.format(format: String = DATE_TIME_FORMAT): String =
    SimpleDateFormat(format, Locale.getDefault()).format(this)

/**
 * 将Date日期格式时间转换为字符串 yyyy-MM-dd
 *
 * @param date 要转换的日期。
 * @return 返回转换后的字符串，格式为yyyy-MM-dd
 */
fun Date.formatDateString() = format(DATE_FORMAT)

/**
 * 将Date日期格式时间转换为字符串 HH:mm:ss
 *
 * @param date
 * @return
 */
fun Date.formatTimeString() = format(TIME_FORMAT)


/**
 * 将日期字符串解析为日期Date。
 *
 * @param dateString 要转换的日期字符串，可能为长日期、短日期、带时区的日期。
 * @param format     日期格式。
 * @return 返回转换后的日期。
 */
fun String.parseDate(format: String = DATE_TIME_FORMAT): Date? =
    SimpleDateFormat(format, Locale.getDefault()).parse(this, ParsePosition(0))

/**
 * 获取星期几(1~7)
 */
fun Date.getWeek(): Int = Calendar.getInstance().run {
    time = this@getWeek
    get(Calendar.DAY_OF_WEEK).run {
        if (this == Calendar.SUNDAY) 7 else this - 1
    }
}

fun Date.getWeekString(): String =
    listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")[this.getWeek() - 1]

fun Date.getMonthOfYear(): Int = Calendar.getInstance().run {
    time = this@getMonthOfYear
    get(Calendar.MONTH) + 1
}

fun Date.getDayOfMonth(): Int = Calendar.getInstance().run {
    time = this@getDayOfMonth
    get(Calendar.DAY_OF_MONTH)
}


/**
 * 获取当前周的周一的日期
 * @param date 传入当前日期
 * @return
 */
fun Date.getThisWeekMonday(): Date {
    val cal = Calendar.getInstance()
    cal.time = this
    // 获得当前日期是一个星期的第几天
    val dayWeek = cal[Calendar.DAY_OF_WEEK]
    if (1 == dayWeek) {
        cal.add(Calendar.DAY_OF_MONTH, -1)
    }
    // 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
    cal.firstDayOfWeek = Calendar.MONDAY
    // 获得当前日期是一个星期的第几天
    val day = cal[Calendar.DAY_OF_WEEK]
    cal.add(Calendar.DATE, cal.firstDayOfWeek - day)
    return cal.time
}

/**
 * 获取间隔天数的日期
 */
fun Date.addDays(days: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.add(Calendar.DAY_OF_YEAR, days)
    return calendar.time
}

fun Date.addMonths(months: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.add(Calendar.MONTH, months)
    return calendar.time
}

/**
 * 是否同一天
 */
fun Date.isSameDayTo(date: Date?): Boolean {
    if (date == null) return false
    val calendar1 = Calendar.getInstance().apply { time = this@isSameDayTo }
    val calendar2 = Calendar.getInstance().apply { time = date }
    return calendar1.get(Calendar.ERA) == calendar2.get(Calendar.ERA) && calendar1.get(Calendar.YEAR) == calendar2.get(
        Calendar.YEAR
    ) && calendar1.get(
        Calendar.DAY_OF_YEAR
    ) == calendar2.get(Calendar.DAY_OF_YEAR)
}

fun Date.isToday(): Boolean = isSameDayTo(Date())


object DateUtil {
    /**
     * 获取当前日期（不含时分秒）
     *
     * @return 返回当前日期yyyy-MM-dd
     */
    fun getTodayString() = Date().formatDateString()

    /**
     * 获取现在时间字符串
     *
     * @return 返回字符串格式 yyyy-MM-dd HH:mm:ss
     */
    fun getNowDateTimeString() = Date().format()

    /**
     * 获取一年第一天
     */
    fun getFirstDayOfYear(): Date {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        calendar.clear()
        calendar.set(Calendar.YEAR, year)
        return calendar.time
    }

    /**
     * 将秒数格式化为01：22：33 时间格式，如果没有时，就是00：22：33
     */
    fun formatToHms(seconds: Long): String {
        val h = seconds / (60 * 60)
        val m = (seconds - h * 60 * 60) / 60
        val s = seconds % 60
        val hStr = if (h < 10) "0$h" else h.toString()
        val mStr = if (m < 10) "0$m" else m.toString()
        val sStr = if (s < 10) "0$s" else s.toString()
        return "$hStr:$mStr:$sStr"
    }

    /**
     * 将时间（秒）转换 为可读的时间
     */
    fun formatReadTime(seconds: Long):String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = seconds * 1000
        val calendarNow = Calendar.getInstance()
        //不是今年的，显示年月日时分
        if (calendar[Calendar.YEAR] != calendarNow.get(Calendar.YEAR) || calendar.get(Calendar.ERA) != calendarNow.get(Calendar.ERA)) {
            return SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(seconds * 1000))
        }
        //今天的
        if (calendar[Calendar.DAY_OF_YEAR] == calendarNow.get(Calendar.DAY_OF_YEAR)) {
            return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(seconds * 1000))
        }

        if (calendar[Calendar.DAY_OF_YEAR] == calendarNow.get(Calendar.DAY_OF_YEAR) - 1) {
            //昨天的
            return SimpleDateFormat("昨天 HH:mm", Locale.getDefault()).format(Date(seconds * 1000 - 24 * 60 * 60L))
        }
        //今年的
        return SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(Date(seconds * 1000))
    }

    /**
     * 将秒数格式化为01：22：33 时间格式  如果没有时，就是22：33
     */
    fun formatToHms2(seconds: Int): String {
        val h = seconds / (60 * 60)
        val m = (seconds - h * 60 * 60) / 60
        val s = seconds % 60
        val hStr = if (h < 10) "0$h" else h.toString()
        val mStr = if (m < 10) "0$m" else m.toString()
        val sStr = if (s < 10) "0$s" else s.toString()
        if (h == 0) {
            return "$mStr:$sStr"
        }
        return "$hStr:$mStr:$sStr"
    }


    /**
     * 根据时间戳(秒)生成年月日
     */
    fun getYearMonthDayByStamp(timeSeconds: Long): List<Int> {
        val calendar = Calendar.getInstance(Locale.getDefault())
        calendar.time = Date(timeSeconds * 1000)
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return listOf(year, month, day)
    }

    //当前秒
    val currentSeconds: Int
        get() = (System.currentTimeMillis() / 1000).toInt()

    fun getWeekString(dayOfWeek: Int): String =
        listOf("周日","周一", "周二", "周三", "周四", "周五", "周六")[dayOfWeek - 1]

}


