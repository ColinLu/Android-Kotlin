package com.colin.android.demo.kotlin

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025-10-18 16:38
 *
 * Des   :ItemBean
 */
@Parcelize
data class ItemBean(val id: Int, val title: String) : Parcelable