package com.colin.android.demo.kotlin.ui.dialog

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import com.colin.library.android.base.BasePopupWindow

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-12-12 13:36
 *
 * Des   :ListPopupWindow
 */
class ListPopupWindow(
    context: Context, builder: BasePopupWindow.Builder<*, *>
) : BasePopupWindow(
    context, builder.getView(), builder.width, builder.height, builder.focusable
) {

    class Builder(context: Context) : BasePopupWindow.Builder<Builder, ListPopupWindow>(context) {
        var view: View? = null
        var layoutRes: Int = Resources.ID_NULL
        override fun getView(): View {
            return view ?: LayoutInflater.from(context).inflate(layoutRes, null, false).also {
                view = it
            }
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