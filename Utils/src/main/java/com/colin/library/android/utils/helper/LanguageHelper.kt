package com.colin.library.android.utils.helper

import android.content.Context
import com.colin.library.android.utils.SpUtil
import java.util.Locale
import java.util.Objects

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-11-12
 * Des   :实现语言切换
 */
object LanguageHelper {
    private const val PREFS_NAME = "SP_Language"
    private const val LANGUAGE_KEY = "language_key"

    @JvmStatic
    public fun isSame(language: String?): Boolean {
        val current = getCurrent() ?: getSystemLanguage()
        return Objects.equals(language, current)
    }

    @JvmStatic
    public fun switch(context: Context, language: String?): Boolean {
        if (!isSame(language) && language != null) {
            val configuration = context.resources.configuration
            configuration.setLocale(Locale(language))
            context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
            return true
            // 需要重启
        }
        return false
    }

    @JvmStatic
    public fun getCurrent(): String? {
        return SpUtil.getSp(PREFS_NAME).getString(LANGUAGE_KEY, null)
    }

    @JvmStatic
    public fun getSystemLanguage() = Locale.getDefault().language

    @JvmStatic
    public fun getCurrentLocal(): Locale {
        return getCurrent()?.let {
            Locale(it)
        } ?: Locale.getDefault()
    }

}