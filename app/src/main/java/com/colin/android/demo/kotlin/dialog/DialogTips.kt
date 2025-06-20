package com.colin.android.demo.kotlin.dialog

import android.os.Bundle
import com.colin.android.demo.kotlin.app.AppDialogFragment
import com.colin.android.demo.kotlin.databinding.DialogTipsBinding

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

    }


    override fun initView(bundle: Bundle?, savedInstanceState: Bundle?) {
        viewBinding.apply {
            textDialogTitle.text = title
            textDialogMessage.text = msg
        }
    }

    override fun initData(bundle: Bundle?, savedInstanceState: Bundle?) {

    }
}