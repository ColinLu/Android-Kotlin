package com.colin.android.demo.kotlin.def

import android.os.Parcelable
import androidx.annotation.ArrayRes
import com.colin.library.android.utils.helper.UtilHelper
import kotlinx.parcelize.Parcelize

@Parcelize
data class ItemBean(val id: Int, val title: String) : Parcelable {
    companion object {
        fun initList(@ArrayRes idRes: Int): List<ItemBean> {
            val array = UtilHelper.getApplication().resources.getStringArray(idRes)
            return array.mapIndexed { index, s -> ItemBean(index, s) }
        }
    }
}


