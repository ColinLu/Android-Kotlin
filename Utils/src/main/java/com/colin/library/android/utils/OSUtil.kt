package com.colin.library.android.utils

import android.os.Build
import android.text.TextUtils
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.Locale

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025-06-29 21:16
 *
 * Des   :OSUtil
 */
object OSUtil {

    const val ROM_MIUI: String = "MIUI"
    const val ROM_EMUI: String = "EMUI"
    const val ROM_FLYME: String = "FLYME"
    const val ROM_OPPO: String = "OPPO"
    const val ROM_SMARTISAN: String = "SMARTISAN"
    const val ROM_VIVO: String = "VIVO"
    const val ROM_QIKU: String = "QIKU"
    const val KEY_VERSION_MIUI: String = "ro.miui.ui.version.name"
    const val KEY_VERSION_EMUI: String = "ro.build.version.emui"
    const val KEY_VERSION_OPPO: String = "ro.build.version.opporom"
    const val KEY_VERSION_SMARTISAN: String = "ro.smartisan.version"
    const val KEY_VERSION_VIVO: String = "ro.vivo.os.version"

    var sName: String? = null
    var sVersion: String? = null

    fun isEmui() = check(ROM_EMUI)


    fun isMiui() = check(ROM_MIUI)


    fun isVivo() = check(ROM_VIVO)


    fun isOppo() = check(ROM_OPPO)


    fun isFlyme() = check(ROM_FLYME)


    fun is360() = check(ROM_QIKU) || check("360")


    fun isSmartisan() = check(ROM_SMARTISAN)


    fun getOSName() = Build.BRAND.uppercase(Locale.getDefault())


    fun getName(): String? {
        if (null == sName) check("")
        return sName
    }

    fun getVersion(): String? {
        if (null == sVersion) check("")
        return sVersion
    }

    fun check(rom: String?): Boolean {
        if (sName != null) return sName == rom
        if (!TextUtils.isEmpty(getProp(KEY_VERSION_MIUI).also { sVersion = it })) sName = ROM_MIUI
        else if (!TextUtils.isEmpty(getProp(KEY_VERSION_EMUI).also { sVersion = it })) sName =
            ROM_EMUI
        else if (!TextUtils.isEmpty(getProp(KEY_VERSION_OPPO).also { sVersion = it })) sName =
            ROM_OPPO
        else if (!TextUtils.isEmpty(getProp(KEY_VERSION_VIVO).also { sVersion = it })) sName =
            ROM_VIVO
        else if (!TextUtils.isEmpty(getProp(KEY_VERSION_SMARTISAN).also { sVersion = it })) sName =
            ROM_SMARTISAN
        else {
            sVersion = Build.DISPLAY
            if (sVersion!!.uppercase(Locale.getDefault()).contains(ROM_FLYME)) sName = ROM_FLYME
            else {
                sVersion = Build.UNKNOWN
                sName = Build.MANUFACTURER.uppercase(Locale.getDefault())
            }
        }
        return sName == rom
    }

    @Synchronized
    fun getProp(name: String): String? {
        var line: String? = null
        var input: BufferedReader? = null
        try {
            val p = Runtime.getRuntime().exec("getprop " + name)
            input = BufferedReader(InputStreamReader(p.inputStream), 1024)
            line = input.readLine()
            input.close()
        } catch (ex: IOException) {
            return null
        } finally {
            IOUtil.close(input)
        }
        return line
    }
}