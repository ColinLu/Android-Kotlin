package com.colin.library.android.utils

import android.os.SystemClock
import android.widget.Toast
import com.colin.library.android.utils.helper.UtilHelper

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-10-10
 * Des   :Toast工具类，提供防抖和去重功能
 */
object ToastUtil {
    private var lastToastTime = 0L

    /**
     * 显示Toast
     *
     * @param msg 显示内容（支持 StringRes 或 CharSequence）
     * @param duration 显示时长，默认 SHORT
     */
    fun show(msg: Any?, duration: Int = Toast.LENGTH_SHORT) {
        val context = UtilHelper.getApplication()
        val text = when (msg) {
            is Int -> ResourcesUtil.getString(context, msg)
            is CharSequence -> msg
            else -> msg?.toString()
        }
        
        if (text.isNullOrEmpty()) return
        
        val current = SystemClock.elapsedRealtime()
        if (current - lastToastTime < TIMEOUT_TOAST) return
        lastToastTime = current

        Toast.makeText(context, text, duration).show()
    }
}
