package com.colin.library.android.base

import android.content.Context
import androidx.appcompat.app.AppCompatDialog

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-09-11
 *
 * Des   :TODO
 */
abstract class BaseDialog @JvmOverloads constructor(
    context: Context, themeResId: Int = android.R.style.Theme_Dialog
) : AppCompatDialog(context, if (themeResId > 0) themeResId else android.R.style.Theme_Dialog) {

}