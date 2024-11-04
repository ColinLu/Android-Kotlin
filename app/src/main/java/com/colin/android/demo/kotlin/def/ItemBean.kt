package com.colin.android.demo.kotlin.def

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ItemBean(val id: Int, val title: String) : Parcelable
