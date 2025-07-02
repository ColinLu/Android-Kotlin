package com.colin.library.android.utils

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.IntRange
import com.colin.library.android.utils.helper.UtilHelper

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025-06-29 21:22
 *
 * Des   :AppUtil
 */
object AppUtil {
    fun getVersionName() = getPackageInfo()?.versionName

    fun getVersionCode(context: Context = UtilHelper.getApplication()): Long {
        val info = getPackageInfo(context) ?: return INVALID.toLong()
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            info.longVersionCode
        } else {
            info.versionCode.toLong()
        }
    }

    /**
     * 获取AndroidManifest.xml文件的信息
     *
     * @return
     */
    fun getPackageInfo(context: Context = UtilHelper.getApplication()): PackageInfo? {
        return getPackageInfo(context, context.packageName, 0)
    }


    fun getPackageInfo(
        context: Context = UtilHelper.getApplication(),
        packageName: String,
        @IntRange(from = 0) flags: Int
    ): PackageInfo? {
        try {
            return context.packageManager?.getPackageInfo(packageName, flags)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return null
    }
}