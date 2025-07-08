package com.colin.library.android.utils

import android.os.Build

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025-06-29 21:16
 *
 * Des   :OSUtil
 */
object OSUtil {

    const val ROM_EMUI: String = "EMUI"
    const val ROM_MIUI: String = "MIUI"
    const val ROM_FLYME: String = "FLYME"
    const val ROM_VIVO: String = "VIVO"
    const val ROM_OPPO: String = "OPPO"
    const val ROM_ONEPLUS: String = "ONEPLUS"
    const val ROM_LETV: String = "LETV"
    const val ROM_360: String = "360"
    const val ROM_QIKU: String = "QIKU"
    const val ROM_SMARTISAN: String = "SMARTISAN"

    private const val KEY_VERSION_EMUI: String = "ro.build.version.emui" //华为
    private const val KEY_VERSION_MIUI: String = "ro.miui.ui.version.name" //华为
    private const val KEY_VERSION_VIVO: String = "ro.vivo.os.version" //vivo
    private const val KEY_VERSION_OPPO: String = "ro.build.version.opporom" //OPPO
    private const val KEY_VERSION_ONEPLUS: String = "ro.rom.version" //1加
    private const val KEY_VERSION_LETV: String = "ro.letv.release.version" //乐视
    private const val KEY_VERSION_360: String = "ro.build.uiversion" //360
    private const val KEY_VERSION_SMARTISAN: String = "ro.smartisan.version" //锤子

    private var sName: String? = null
    private var sVersion: String? = null
    fun getName(): String? {
        if (null == sName) check("")
        return sName
    }

    fun getVersion(): String? {
        if (null == sVersion) check("")
        return sVersion
    }

    private fun check(rom: String): Boolean {
        sName?.let {
            return it.equals(rom, ignoreCase = true)
        } ?: getProp(KEY_VERSION_EMUI)?.let {
            sName = ROM_EMUI
            sVersion = it
        } ?: getProp(KEY_VERSION_MIUI)?.let {
            sName = ROM_MIUI
            sVersion = it
        } ?: getProp(KEY_VERSION_OPPO)?.let {
            sName = ROM_OPPO
            sVersion = it
        } ?: getProp(KEY_VERSION_VIVO)?.let {
            sName = ROM_VIVO
            sVersion = it
        } ?: getProp(KEY_VERSION_ONEPLUS)?.let {
            sName = ROM_ONEPLUS
            sVersion = it
        } ?: getProp(KEY_VERSION_LETV)?.let {
            sName = ROM_LETV
            sVersion = it
        } ?: getProp(KEY_VERSION_360)?.let {
            sName = ROM_360
            sVersion = it
        } ?: getProp(KEY_VERSION_SMARTISAN)?.let {
            sName = ROM_SMARTISAN
            sVersion = it
        } ?: getBySystem()
        return sName.equals(rom, ignoreCase = true)
    }

    fun getProp(name: String): String? {
        val result = CommandUtil.execCmd("getprop $name", 1024)
        return if (result.isNullOrEmpty()) null else result
    }

    private fun getBySystem() {
        if (Build.DISPLAY.contains(ROM_FLYME, true)) {
            sName = ROM_FLYME
            sVersion = Build.DISPLAY
            return
        }
        if (Build.DISPLAY.contains(ROM_QIKU, true)) {
            sName = ROM_QIKU
            sVersion = Build.DISPLAY
            return
        }
        if (Build.DISPLAY.contains(ROM_360, true)) {
            sName = ROM_360
            sVersion = Build.DISPLAY
            return
        }
        sName = Build.MANUFACTURER.uppercase()
        sVersion = Build.DISPLAY
    }
}