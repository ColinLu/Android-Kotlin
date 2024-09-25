package com.colin.library.android.utils

import androidx.annotation.StringRes
import com.colin.library.android.utils.helper.UtilHelper

object ResourcesUtil {

    fun getString(@StringRes res: Int) = UtilHelper.getApplication().getString(res)

}