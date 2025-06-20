package com.colin.android.demo.kotlin.ui.dialog

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.colin.android.demo.kotlin.adapter.StringAdapter
import com.colin.library.android.widget.base.BasePopupWindow

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-12-12 13:36
 *
 * Des   :ListPopupWindow
 */
class ListPopupWindow(
    private val builder: Builder
) : BasePopupWindow(builder) {
    override fun show(anchor: View, xoff: Int, yoff: Int, gravity: Int) {
        val list = builder.getView() as RecyclerView
        list.apply {
            layoutManager = LinearLayoutManager(builder.context)
            adapter = StringAdapter()
        }
        super.show(anchor, xoff, yoff, gravity)
    }

    class Builder(
        context: Context,
        width: Int = LayoutParams.WRAP_CONTENT,
        height: Int = LayoutParams.WRAP_CONTENT
    ) : BasePopupWindow.Builder<Builder, ListPopupWindow>(context, width, height) {
        private var layoutRes: Int = Resources.ID_NULL
        private var view: View? = null
        private var array: Array<String>? = null

        override fun getView(): View {
            return view ?: LayoutInflater.from(context).inflate(layoutRes, null, false)
        }

        fun setView(layoutRes: Int): Builder {
            this.layoutRes = layoutRes
            return this
        }

        fun setView(view: View): Builder {
            this.view = view
            return this
        }

        fun setArray(array: Array<String>): Builder {
            this.array = array
            return this
        }

        override fun build(): ListPopupWindow {
            return ListPopupWindow(this)
        }

    }

}