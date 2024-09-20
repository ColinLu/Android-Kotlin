package com.colin.library.android.base

import android.R
import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.os.SystemClock
import android.view.Gravity
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.colin.library.android.utils.Constants

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-09-11
 *
 * Des   :TODO
 */
abstract class BaseDialog @JvmOverloads constructor(
    context: Context, theme: Int = android.R.style.Theme_Material_Dialog_NoActionBar
) : DialogFragment() {
    companion object {
        private var sShowTag: String? = null
        private var sLastTime: Long = 0
    }

    /*Dialog Title*/
//    var title: CharSequence? = null
//
//    @ColorInt
//    var titleColor: Int = Constants.INVALID


    /*Dialog Message*/
    var message: CharSequence? = null

    @ColorInt
    var messageColor: Int = Constants.INVALID


    /*Dialog LeftButton Text*/
    var leftButton: CharSequence? = null

    @ColorInt
    var leftButtonColor: Int = Constants.INVALID


    /*Dialog RightButton Text*/
    @ColorInt
    var rightButtonColor: Int = Constants.INVALID
    var rightButton: CharSequence? = null

    /*Dialog点击弹框之外是否可以消失 true 可以  false 不能*/
    var outViewCancel: Boolean = true

    /*Dialog布局文件*/
    @LayoutRes
    var layoutRes: Int = Resources.ID_NULL

    /*宽高显示比例  0 不处理 1 全屏占比*/
    @FloatRange(from = 0.0, to = 1.0)
    var dialogWidth: Float = 0.8f

    @FloatRange(from = 0.0, to = 1.0)
    var dialogHeight: Float = 0.0f

    /*Dialog显示位置*/
    var gravity: Int = Gravity.CENTER

    /*Dialog显示动画*/
    @StyleRes
    var dialogAnim: Int = R.style.Animation_Dialog

    var onShowListener: (DialogInterface) -> Unit = {}
    var onDismissListener: (DialogInterface) -> Unit = {}
    var onClickListener: (DialogInterface) -> Unit = {}

    fun show(activity: FragmentActivity?) {
        if (activity?.isFinishing == false) {
            show(activity.supportFragmentManager, activity::class.java.name)
        }
    }

    fun show(fragment: Fragment?) {
        if (fragment?.isAdded == true) {
            show(fragment.childFragmentManager, fragment::class.java.name)
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        if (!manager.isDestroyed && !isRepeatedShow(tag)) {
            super.show(manager, tag)
        }
    }

    override fun show(transaction: FragmentTransaction, tag: String?): Int {
        return if (!isRepeatedShow(tag)) {
            super.show(transaction, tag)
        } else Constants.INVALID
    }

    override fun showNow(manager: FragmentManager, tag: String?) {
        if (!manager.isDestroyed && !isRepeatedShow(tag)) {
            super.showNow(manager, tag)
        }
    }

    /**
     * 根据 tag 判断这个 Dialog 是否重复显示了
     *
     * @param tag Tag标记
     */
    private fun isRepeatedShow(tag: String?): Boolean {
        val result = tag == sShowTag && SystemClock.uptimeMillis() - sLastTime < 500
        sShowTag = tag
        sLastTime = SystemClock.uptimeMillis()
        return result
    }
}