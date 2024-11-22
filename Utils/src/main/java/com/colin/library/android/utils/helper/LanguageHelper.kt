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
 * 3. lazy 实现单例（线程安全）
 *
 * 虽然 Kotlin object 已经提供了线程安全机制，但你也可以使用 lazy 来实现惰性单例，
 * 确保只有在第一次使用时才会创建实例。
 */
class LanguageHelper private constructor() {
    companion object {
        private const val PREFS_NAME = "SP_Language"
        private const val LANGUAGE_KEY = "language_key"
        val instance: LanguageHelper by lazy { LanguageHelper() }
    }


    fun theSame(language: String?): Boolean {
        val current = getCurrent() ?: getSystemLanguage()
        return Objects.equals(language, current)
    }

    fun switch(context: Context, language: String?): Boolean {
        if (!theSame(language) && language != null) {
            val configuration = context.resources.configuration
            configuration.setLocale(Locale(language))
            context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
            return true
            // 需要重启
        }
        return false
    }

    fun getCurrent(): String? {
        return SpUtil.getSp(PREFS_NAME).getString(LANGUAGE_KEY, null)
    }

    fun getSystemLanguage() = Locale.getDefault().language
    fun getSystemLanguages() = Locale.getAvailableLocales()
    fun getCurrentLocal(): Locale? {
        return getCurrent()?.let { Locale(it) }
    }

}