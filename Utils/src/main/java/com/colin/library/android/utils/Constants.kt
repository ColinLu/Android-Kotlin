package com.colin.library.android.utils

import java.util.TimeZone

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-09-11
 *
 * Des   :常量
 */
const val INVALID = -1
const val ZERO = 0
const val ONE_SECOND = 1000
const val TIMEOUT_CLICK = 500L
const val TIMEOUT_TOAST = ONE_SECOND

const val FORMAT_DAY_PATTERN = "yyyy-MM-dd"
const val FORMAT_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss:SSS"
const val FORMAT_TIME_HTTP = "EEE, dd MMM y HH:mm:ss 'GMT'"
val TIME_ZONE_GMT = TimeZone.getTimeZone("GMT")
val LINE_SEP = System.lineSeparator()
