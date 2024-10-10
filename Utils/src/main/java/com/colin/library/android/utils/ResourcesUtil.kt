package com.colin.library.android.utils

import androidx.annotation.StringRes
import com.colin.library.android.utils.helper.UtilHelper

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-10-10
 * Des   :资源工具类
 */
object ResourcesUtil {
    /*资源ID转String*/
    fun getString(@StringRes res: Int) = UtilHelper.getApplication().getString(res)

}