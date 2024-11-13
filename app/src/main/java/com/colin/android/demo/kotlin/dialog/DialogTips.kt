package com.colin.android.demo.kotlin.dialog

import android.os.Bundle
import com.colin.android.demo.kotlin.app.AppDialogFragment
import com.colin.android.demo.kotlin.databinding.DialogTipsBinding
import com.colin.library.android.base.BaseDialogFragment

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-10-31
 *
 * Des   :提示弹框
 */
class DialogTips private constructor(private val builder: Builder) :
    AppDialogFragment<DialogTipsBinding>(builder) {

    companion object {
        @JvmStatic
        fun newBuilder() = Builder()

        @JvmStatic
        private fun newInstance(builder: Builder): DialogTips {
            return DialogTips(builder)
        }
    }


    class Builder : BaseDialogFragment.Builder<Builder, DialogTips>() {
        internal var title: CharSequence? = null
        internal var message: CharSequence? = null
        fun title(title: CharSequence?): Builder {
            this.title = title
            return this
        }

        fun message(message: CharSequence?): Builder {
            this.message = message
            return this
        }

        override fun build(): DialogTips {
            return newInstance(this)
        }
    }

    override fun initView(bundle: Bundle?, savedInstanceState: Bundle?) {
        viewBinding.apply {
            textDialogTitle.text = builder.title
            textDialogMessage.text = builder.message
        }
    }

    override fun initData(bundle: Bundle?, savedInstanceState: Bundle?) {

    }
}