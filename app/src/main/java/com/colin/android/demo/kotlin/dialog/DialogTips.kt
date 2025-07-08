package com.colin.android.demo.kotlin.dialog

import android.os.Bundle
import android.view.Gravity
import com.colin.android.demo.kotlin.app.AppDialogFragment
import com.colin.android.demo.kotlin.databinding.DialogTipsBinding
import com.colin.library.android.utils.ext.onClick

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-10-31
 *
 * Des   :提示弹框
 */
class DialogTips private constructor(
    private val title: CharSequence, private val msg: CharSequence
) : AppDialogFragment<DialogTipsBinding>() {

    companion object {
        const val EXTRAS_TITLE = "title"
        const val EXTRAS_MSG = "msg"
        fun newInstance(title: CharSequence, msg: CharSequence): DialogTips {
            val fragment = DialogTips(title, msg).also {
                it.arguments = Bundle().apply {
                    putCharSequence(EXTRAS_TITLE, title)
                    putCharSequence(EXTRAS_MSG, msg)
                }
                it.width = 0.8F
                it.height = 0.5F
                it.gravity = Gravity.CENTER
            }
            return fragment
        }
    }


    override fun initView(bundle: Bundle?, savedInstanceState: Bundle?) {
        viewBinding.apply {
            onClick(btCancel, btSure) {
                when (it) {
                    btSure -> {
                        dismiss()
                        sure.invoke(it)
                    }

                    btCancel -> {
                        dismiss()
                        cancel.invoke(it)
                    }
                }
            }
        }
    }

    override fun initData(bundle: Bundle?, savedInstanceState: Bundle?) {
        viewBinding.apply {
            tvTitle.text = title
            tvMsg.text = msg
        }
    }
}