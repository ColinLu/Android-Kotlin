package com.colin.library.android.utils

import android.content.Context
import android.os.SystemClock
import android.text.TextUtils
import android.widget.Toast
import androidx.annotation.StringRes
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
     * 显示Toast（资源ID方式）
     *
     * @param res 字符串资源ID
     * @param duration 显示时长，默认SHORT
     */
    fun show(@StringRes res: Int, duration: Int = Toast.LENGTH_SHORT) {
        show(UtilHelper.getApplication(), ResourcesUtil.getString(UtilHelper.getApplication(),res), duration)
    }

    /**
     * 显示Toast（文本方式）
     *
     * @param text 显示文本
     * @param duration 显示时长，默认SHORT
     */
    fun show(text: CharSequence?, duration: Int = Toast.LENGTH_SHORT) {
        show(UtilHelper.getApplication(), text, duration)
    }

    /**
     * 显示Toast（内部方法，带防抖）
     *
     * @param context 上下文
     * @param text 显示文本
     * @param duration 显示时长
     */
    fun show(context: Context?, text: CharSequence?, duration: Int) {
        if (context == null || TextUtils.isEmpty(text)) return
        val current = SystemClock.elapsedRealtime()
        if (current - lastToastTime < TIMEOUT_TOAST) return
        lastToastTime = current
        Toast.makeText(context, text, duration).show()
    }
}
