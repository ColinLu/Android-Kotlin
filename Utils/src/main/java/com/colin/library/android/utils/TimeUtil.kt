package com.colin.library.android.utils

import androidx.collection.LruCache
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025-06-29 20:52
 *
 * Des   :TimeUtil
 */
object TimeUtil {

    private val SDF_THREAD_LOCAL: LruCache<String, ThreadLocal<SimpleDateFormat>?> =
        LruCache<String, ThreadLocal<SimpleDateFormat>?>(10)

    /*获取时间格式化*/
    fun getDateFormat(pattern: String): SimpleDateFormat {
        val dateLocal = SDF_THREAD_LOCAL.get(pattern) ?: getLocal(pattern).also {
            SDF_THREAD_LOCAL.put(pattern, it)
        }
        return dateLocal.get()
    }

    private fun getLocal(pattern: String): ThreadLocal<SimpleDateFormat> {
        return object : ThreadLocal<SimpleDateFormat>() {
            override fun initialValue(): SimpleDateFormat {
                return SimpleDateFormat(pattern, Locale.getDefault())
            }
        }
    }

    /**
     * 获取当前时间戳格式化字符串
     * yyyy-MM-dd HH:mm:ss:SSS
     *
     * @return
     */
    fun getTimeString(): String {
        return getTimeString(System.currentTimeMillis())
    }

    /**
     * 时间戳字符串格式化
     * yyyy-MM-dd HH:mm:ss:SSS
     *
     * @param time
     * @return
     */
    fun getTimeString(time: Long): String {
        return getTimeString(FORMAT_TIME_PATTERN, time)
    }

    /**
     * 当前时间制定格式化成字符串
     *
     * @param pattern 时间格式
     * @return
     */
    fun getTimeString(pattern: String): String {
        return getTimeString(pattern, System.currentTimeMillis())
    }

    /**
     * 格式化时间戳
     *
     * @param pattern
     * @param time
     * @return
     */
    fun getTimeString(pattern: String, time: Long): String {
        return getTimeString(getDateFormat(pattern), time)
    }

    /**
     * 格式化时间戳
     *
     * @param simpleDateFormat
     * @return
     */
    fun getTimeString(simpleDateFormat: SimpleDateFormat): String {
        return getTimeString(simpleDateFormat, System.currentTimeMillis())
    }

    /**
     * 格式化时间戳
     *
     * @param simpleDateFormat
     * @return
     */
    fun getTimeString(simpleDateFormat: SimpleDateFormat, time: Long): String {
        return simpleDateFormat.format(Date(time))
    }

    fun getTime(pattern: String?, time: String?): Long {
        if (pattern.isNullOrEmpty() || time.isNullOrEmpty()) return INVALID.toLong()
        try {
            val dateFormat = getDateFormat(pattern)
            val date = dateFormat.parse(time)
            return date?.time ?: INVALID.toLong()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return INVALID.toLong()
    }

    fun format(second: Long): String {
        var second = second
        val days = second / 86400 //转换天数
        second = second % 86400 //剩余秒数
        val hours = second / 3600 //转换小时
        second = second % 3600 //剩余秒数
        val minutes = second / 60 //转换分钟
        second = second % 60 //剩余秒数
        return if (days > 0) "${days}天${hours}小时${minutes}分${second}秒"
        else "${hours}小时${minutes}分${second}秒"
    }

    /**
     * 时间戳转换成时间格式
     *
     * @param duration
     * @return
     */
    fun formatDuration(duration: Long): String {
        return String.format(
            Locale.getDefault(),
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(duration),
            TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(duration)
            )
        )
    }

    /**
     * 更具时间字符串 转化成日期格式
     *
     * @param format
     * @param time
     * @return
     */
    fun getDateByString(format: String, time: String?): Date {
        if (time.isNullOrEmpty()) return Date()
        try {
            return getDateFormat(format).parse(time)
        } catch (e: ParseException) {
            e.printStackTrace()
            return Date()
        }
    }

    /**
     * 判断是否闰年
     *
     * @param year 年数
     * @return `true` yes, `false` no
     */
    fun isLeapYear(year: Int): Boolean {
        if (year <= 0) return false
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }

    /**
     * 根据年份、月份, 获取对应的天数 ( 完整天数, 无判断是否属于未来日期 )
     *
     * @param year  年数
     * @param month 月份
     * @return 指定年份所属的月份的天数
     */
    fun getMonthDayNumberAll(year: Int, month: Int): Int {
        var number = 31
        when (month) {
            1, 3, 5, 7, 8, 10, 12 -> number = 31
            2 -> number = if (isLeapYear(year)) 29 else 28
            4, 6, 9, 11 -> number = 30
        }
        return number
    }

    fun parseHttpTime(time: String?): Long {
        if (time.isNullOrEmpty()) return INVALID.toLong()
        try {
            return getDateFormat(FORMAT_TIME_HTTP).apply {
                timeZone = TIME_ZONE_GMT
            }.parse(time)?.time ?: INVALID.toLong()
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return INVALID.toLong()
    }
}