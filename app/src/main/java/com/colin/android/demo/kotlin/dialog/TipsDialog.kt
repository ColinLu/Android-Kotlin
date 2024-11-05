package com.colin.android.demo.kotlin.dialog

import com.colin.android.demo.kotlin.app.AppDialog
import com.colin.android.demo.kotlin.databinding.LayoutListBinding
import com.colin.library.android.base.BaseDialog

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-10-31
 *
 * Des   :TODO
 */
class TipsDialog private constructor(builder: Builder) : AppDialog<LayoutListBinding>(builder) {

    companion object {
        @JvmStatic
        fun newBuilder() = Builder()

        @JvmStatic
        private fun newInstance(builder: Builder): TipsDialog {
            return TipsDialog(builder)
        }
    }


    class Builder : BaseDialog.Builder<Builder, TipsDialog>() {
        override fun build(): TipsDialog {
            return TipsDialog.newInstance(this)
        }
    }
}