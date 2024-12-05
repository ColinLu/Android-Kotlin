package com.colin.library.android.utils.helper

import com.colin.library.android.utils.SpUtil
import java.util.Locale

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-11-12
 * Des   :实现语言切换
 * 3. lazy 实现单例（线程安全）
 *
 * 虽然 Kotlin object 已经提供了线程安全机制，但你也可以使用 lazy 来实现惰性单例，
 * 确保只有在第一次使用时才会创建实例。
 */
object LanguageHelper {
    private const val PREFS_NAME = "sp_local"
    private const val KEY_LOCAL_LANGUAGE = "KEY_LOCAL_LANGUAGE"
    private const val KEY_LOCAL_COUNTRY = "KEY_LOCAL_COUNTRY"

    /*获取系统语言*/
    @JvmStatic
    fun getSystemLocale() = Locale.getDefault()

    /*获取系统语言显示列表*/
    @JvmStatic
    fun getSystemLocales() = Locale.getAvailableLocales()

    /*获取App设置语言,如果没有设置，返回系统语言*/
    @JvmStatic
    fun getAppLocale(): Locale {
        val sp = SpUtil.getSp(PREFS_NAME)
        val language = sp.getString(KEY_LOCAL_LANGUAGE, null)
        val country = sp.getString(KEY_LOCAL_COUNTRY, null)
        return if (language.isNullOrBlank() || country.isNullOrBlank()) getSystemLocale()
        else Locale(language, country)
    }

    @JvmStatic
    fun saveLocale(locale: Locale?): Boolean {
        return SpUtil.getEdit(PREFS_NAME).apply {
            putString(KEY_LOCAL_LANGUAGE, locale?.language ?: "")
            putString(KEY_LOCAL_COUNTRY, locale?.country ?: "")
        }.commit()
    }

}