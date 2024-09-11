package com.colin.library.android.base

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-09-09
 *
 * Des   :TODO
 */
abstract class BaseAdapter<ITEM>(
    private val list: ArrayList<ITEM> = arrayListOf(), @LayoutRes private val layoutRes: Int
) : RecyclerView.Adapter<BaseViewHolder>() {
    lateinit var context: Context

    /**
     * 空布局资源ID，如果不为ID_NULL,说明支持空布局
     */
    @LayoutRes
    var empty: Int = Resources.ID_NULL

    /**
     * 尾部布局资源ID，如果不为ID_NULL,说明支持尾部布局
     */
    @LayoutRes
    var footer: Int = Resources.ID_NULL

    /**
     * Item点击事件监听
     */
    var onItemClickListener: ((view: View, position: Int) -> Unit)? = null

    /**
     * Item长按事件监听
     */
    var onItemLongClickListener: ((view: View, position: Int) -> Boolean) = { view, position -> false }

    /**
     * 计算Adapter 适配器总共大小
     */
    final override fun getItemCount(): Int {
        if (shouldDisplayEmpty()) return 1
        else if (shouldDisplayFoot()) return list.size + 1
        else return list.size
    }

    final override fun getItemViewType(position: Int): Int {
        return if (shouldDisplayEmpty(position)) empty
        else if (shouldDisplayFoot(position)) footer
        else layoutRes
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        context = parent.context
        return BaseViewHolder(LayoutInflater.from(context).inflate(viewType, parent, false))
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        if (shouldDisplayEmpty(position)) bindEmptyViewHolder(holder, position)
        else if (shouldDisplayFoot(position)) bindFooterViewHolder(holder, position)
        else bindListViewHolder(holder, position)
    }

    /**
     * 清空数据
     */
    fun clear() {
        this.list.clear()
        notifyDataSetChanged()
    }

    abstract fun bindListViewHolder(holder: BaseViewHolder, position: Int)

    fun bindEmptyViewHolder(holder: BaseViewHolder, position: Int) {

    }

    fun bindFooterViewHolder(holder: BaseViewHolder, position: Int) {

    }

    /**
     * 判断当前Adapter是否需要显示空布局
     * ps:当list 数组为空显示，切empty 布局ID不为空
     */
    open fun shouldDisplayEmpty(): Boolean {
        return empty != Resources.ID_NULL && list.isEmpty()
    }

    open fun shouldDisplayEmpty(position: Int): Boolean {
        return shouldDisplayEmpty() && position == 0
    }

    /**
     * 判断当前Adapter是否需要显示尾部布局
     * ps:当list 数组为空显示，切empty 布局ID不为空
     */
    open fun shouldDisplayFoot(): Boolean {
        return footer != Resources.ID_NULL
    }

    open fun shouldDisplayFoot(position: Int): Boolean {
        return shouldDisplayFoot() && position + 1 == list.size
    }
}