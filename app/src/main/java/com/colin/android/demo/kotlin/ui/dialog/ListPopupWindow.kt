package com.colin.android.demo.kotlin.ui.dialog

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.colin.android.demo.kotlin.adapter.StringAdapter
import com.colin.library.android.base.BasePopupWindow
import com.colin.library.android.widgets.recycler.SpaceItemDecoration

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-12-12 13:36
 *
 * Des   :ListPopupWindow
 */
class ListPopupWindow(
    context: Context, private val builder: Builder
) : BasePopupWindow(
    context, builder.getView(), builder.width, builder.height, builder.focusable
) {
    override fun show(anchor: View) {
        val view = contentView as RecyclerView
        view.apply {
            this.layoutManager = LinearLayoutManager(this@ListPopupWindow.context)
            this.adapter = StringAdapter().apply {
                submitList(builder.list)
                onItemClickListener = builder.itemListener
            }
            this.addItemDecoration(SpaceItemDecoration(space = 5))
        }
        super.show(anchor)
    }

    class Builder(context: Context) : BasePopupWindow.Builder<Builder, ListPopupWindow>(context) {
        internal var layoutRes: Int = Resources.ID_NULL
        internal var list: List<String> = emptyList()
        internal var itemListener: ((View, String) -> Unit)? = null
        override fun getView(): View {
            return LayoutInflater.from(context).inflate(layoutRes, null, false)
        }

        fun setData(list: List<String>): Builder {
            this.list = list
            return this
        }

        fun setItemListener(listener: ((View, String) -> Unit)?): Builder {
            this.itemListener = listener
            return this
        }

        override fun build(): ListPopupWindow {
            val popupWindow = ListPopupWindow(context, this)
            popupWindow.contentView = getView()
            popupWindow.setBackgroundDrawable(background)
            popupWindow.width = width
            popupWindow.height = height
            popupWindow.isOutsideTouchable = outsideTouchable
            popupWindow.isFocusable = focusable
            popupWindow.setOnDismissListener(dismissListener)
            popupWindow.isClippingEnabled = clippingEnable
            return popupWindow
        }

    }

}