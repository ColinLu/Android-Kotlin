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
class TipsDialog : AppDialog<LayoutListBinding, TipsDialog>() {


    class Builder() : BaseDialog.Builder<TipsDialog>() {
        override fun build(): TipsDialog {
            return TipsDialog()
        }

    }
}