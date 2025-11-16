package com.colin.android.demo.kotlin.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.colin.android.demo.kotlin.adapter.ItemAdapter
import com.colin.android.demo.kotlin.databinding.CustomRefreshListBinding

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025-11-16 10:27
 *
 * Des   :RefreshListLayout
 */
class RefreshListLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val binding =
        CustomRefreshListBinding.bind(inflate(context, com.colin.android.demo.kotlin.R.layout.custom_refresh_list, this))

    private val listAdapter: ItemAdapter = ItemAdapter()

    init {
        binding.list.adapter = listAdapter
    }
}
