package com.colin.library.android.utils

import android.content.Context
import android.text.TextUtils
import android.widget.Toast
import androidx.annotation.StringRes
import com.colin.library.android.utils.helper.UtilHelper

object ToastUtil {
    private var lastShowTime = 0L
    fun show(@StringRes res: Int, duration: Int = Toast.LENGTH_SHORT) {
        show(UtilHelper.getApplication(), ResourcesUtil.getString(res), duration)
    }

    fun show(text: CharSequence?, duration: Int = Toast.LENGTH_SHORT) {
        show(UtilHelper.getApplication(), text, duration)
    }

    fun show(context: Context?, text: CharSequence?, duration: Int) {
        if (context == null || TextUtils.isEmpty(text)) return
        val current = System.currentTimeMillis()
        if (current - lastShowTime < Constants.TOAST_SHOW_DEF) return
        lastShowTime = current
        Toast.makeText(context, text, duration).show()

    }

}