package com.colin.library.android.utils.helper

import android.os.Build
import android.util.Log
import com.colin.library.android.utils.AppUtil
import com.colin.library.android.utils.FORMAT_DAY_PATTERN
import com.colin.library.android.utils.FileUtil
import com.colin.library.android.utils.LINE_SEP
import com.colin.library.android.utils.OSUtil
import com.colin.library.android.utils.PathUtil
import com.colin.library.android.utils.TimeUtil
import java.io.File

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025-06-29 19:53
 *
 * Des   :CrashHelper
 */
object CrashHelper {
    fun init(onCrashListener: OnCrashListener) {
        Thread.setDefaultUncaughtExceptionHandler(CrashHandler(null, null, onCrashListener))
    }

    fun init(fileName: String, onCrashListener: OnCrashListener) {
        Thread.setDefaultUncaughtExceptionHandler(CrashHandler(null, fileName, onCrashListener))
    }

    fun init(folder: File, fileName: String, onCrashListener: OnCrashListener) {
        Thread.setDefaultUncaughtExceptionHandler(CrashHandler(folder, fileName, onCrashListener))
    }

    fun createFile(): File = PathUtil.getExternalCache() ?: PathUtil.getInternalCache()

    fun createFileName(): String {
        return UtilHelper.getApplication().packageName + "_" + TimeUtil.getTimeString(
            FORMAT_DAY_PATTERN
        ) + ".txt"
    }

    private class CrashHandler(
        folder: File?, fileName: String?, onCrashListener: OnCrashListener?
    ) : Thread.UncaughtExceptionHandler {
        private var folder: File?
        private var fileName: String?
        private val onCrashListener: OnCrashListener?

        init {
            this.folder = folder
            this.fileName = fileName
            this.onCrashListener = onCrashListener
        }

        @Synchronized
        override fun uncaughtException(t: Thread, e: Throwable) {
            ThreadHelper.doAsync {
                val sb = StringBuilder()
                sb.append("****************************** Log Start ******************************")
                    .append(LINE_SEP).append("Time Of Crash      : ")
                    .append(TimeUtil.getTimeString()).append(LINE_SEP)
                    .append("OS name            : ").append(OSUtil.getName()).append(LINE_SEP)
                    .append("OS version         : ").append(OSUtil.getVersion()).append(LINE_SEP)
                    .append("Device Manufacturer: ").append(Build.MANUFACTURER).append(LINE_SEP)
                    .append("Device Model       : ").append(Build.MODEL).append(LINE_SEP)
                    .append("Android Version    : ").append(Build.VERSION.RELEASE).append(LINE_SEP)
                    .append("Android SDK        : ").append(Build.VERSION.SDK_INT).append(LINE_SEP)
                    .append("App VersionName    : ").append(AppUtil.getVersionName())
                    .append(LINE_SEP).append("App VersionCode    : ")
                    .append(AppUtil.getVersionCode()).append(LINE_SEP).append(LINE_SEP)
                    .append(Log.getStackTraceString(e)).append(LINE_SEP)
                    .append("****************************** Log End ******************************")
                    .append(LINE_SEP)
                onCrashListener?.crash(e, sb.toString())
                if (!FileUtil.isDir(folder)) folder = createFile()
                if (fileName.isNullOrBlank()) fileName = createFileName()
                val file = File(folder, fileName!!)
                Log.e("CrashHelper", file.absolutePath)
                FileUtil.write(file, sb.toString(), true)
            }
        }
    }

    /**//////////////////////////////////////////////////////////////////////// */ // interface
    /**//////////////////////////////////////////////////////////////////////// */
    interface OnCrashListener {
        fun crash(error: Throwable, info: String)
    }

}