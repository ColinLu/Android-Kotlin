package com.colin.android.demo.kotlin.adapter

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
class StringAdapter(array: ArrayList<String> = arrayListOf(), layoutRes: Int = R.layout.item_text) :
    BaseAdapter<String>(array, layoutRes = layoutRes) {


    override fun bindListViewHolder(
        holder: BaseViewHolder, item: String, position: Int, payloads: MutableList<Any>
    ) {
        holder.getTextView(R.id.item_text).text = item
        holder.itemView.onClick {
            onItemClickListener?.invoke(it, item, position)
        }
    }
}