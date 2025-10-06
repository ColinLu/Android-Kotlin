package com.colin.android.demo.kotlin.adapter

import android.annotation.SuppressLint
import com.colin.android.demo.kotlin.R
import com.colin.library.android.utils.ext.onClick
import com.colin.library.android.widget.base.BaseAdapter
import com.colin.library.android.widget.base.BaseViewHolder

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-09-13
 *
 * Des   :字符串 adapter
 */
class BannerAdapter(list: List<Int> = emptyList<Int>(), layoutRes: Int = R.layout.item_banner) :
    BaseAdapter<Int>(list, layoutRes = layoutRes) {

    @SuppressLint("SetTextI18n")
    override fun bindListViewHolder(
        holder: BaseViewHolder, item: Int, position: Int, payloads: MutableList<Any>
    ) {
        holder.getImageView(R.id.item_image).setImageResource(item)
        holder.getTextView(R.id.item_text).text = "position:$position"
        holder.itemView.onClick {
            onItemClickListener?.invoke(it, item, position)
        }
    }
}

