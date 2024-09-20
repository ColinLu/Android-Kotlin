package com.colin.library.android.http

import android.os.Build
import android.text.TextUtils
import java.util.Locale

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-09-18
 *
 * Des   :网络操作工具类
 */
object Util {
    const val FORMAT_WEB_USER_AGENT =
        "Mozilla/5.0 (Linux; U; Android %s) AppleWebKit/533.1 (KHTML, like Gecko) Version/5.0 %sSafari/533.1"

    fun getAcceptLanguage(): String {
        val locale = Locale.getDefault()
        val language = locale.language
        val country = locale.country
        val sb = StringBuilder(language)
        if (!TextUtils.isEmpty(country)) {
            sb.append('-').append(country).append(',').append(language).append(";q=0.8")
        }
        return sb.toString()
    }

    fun getUserAgent(): String {
        val sb = StringBuilder()
        // Add version
        val version = Build.VERSION.RELEASE
        if (!TextUtils.isEmpty(version)) sb.append(version)
        else sb.append("1.0") // default to "1.0"

        sb.append("; ")

        val locale = Locale.getDefault()
        val language = locale.language
        if (!TextUtils.isEmpty(language)) {
            sb.append(language.lowercase(locale))
            val country = locale.country
            if (!TextUtils.isEmpty(country)) sb.append("-").append(country.lowercase(locale))
        } else sb.append("en") // default to "en"


        // add the model for the release build
        if ("REL" == Build.VERSION.CODENAME) {
            val model = Build.MODEL
            if (!TextUtils.isEmpty(model)) sb.append("; ").append(model)
        }
        val id = Build.ID
        if (!TextUtils.isEmpty(id)) sb.append(" Build/").append(id)
        return String.format(FORMAT_WEB_USER_AGENT, sb, "Mobile ")
    }

}