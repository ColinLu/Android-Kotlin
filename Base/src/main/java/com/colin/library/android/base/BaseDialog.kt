package com.colin.library.android.base

import android.app.Dialog
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.SystemClock
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.Window
import androidx.annotation.AnimRes
import androidx.annotation.Dimension
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.colin.library.android.base.def.IBase
import com.colin.library.android.utils.L


/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-09-11
 *
 * Des   :Dialog基类:最简单的业务逻辑定义
 */
abstract class BaseDialog protected constructor(private val builder: Builder<*, *>) :
    DialogFragment(builder.layoutRes), IBase {
    internal val TAG = this::class.simpleName!!

    @LayoutRes
    internal var layoutRes = Resources.ID_NULL

    @AnimRes
    internal var animation: Int = android.R.style.Animation_Dialog

    @Dimension
    internal var width: Int = DEFAULT_DIALOG_WINDOW_WIDTH

    @Dimension
    internal var height: Int = DEFAULT_DIALOG_WINDOW_HEIGHT

    @Dimension
    internal var offsetX: Int = DEFAULT_DIALOG_WINDOW_OFFSET

    @Dimension
    internal var offsetY: Int = DEFAULT_DIALOG_WINDOW_OFFSET
    internal var gravity: Int = DEFAULT_DIALOG_WINDOW_GRAVITY
    internal var title: CharSequence? = null
    internal var message: CharSequence? = null

    init {
        arguments = Bundle().apply {
            putCharSequence(EXTRAS_BASE_TITLE, builder.title)
            putCharSequence(EXTRAS_BASE_MESSAGE, builder.message)
            putInt(EXTRAS_BASE_WIDTH, builder.width)
            putInt(EXTRAS_BASE_HEIGHT, builder.height)
            putInt(EXTRAS_BASE_OFFSET_X, builder.offsetX)
            putInt(EXTRAS_BASE_OFFSET_Y, builder.offsetY)
            putInt(EXTRAS_BASE_GRAVITY, builder.gravity)
            putInt(EXTRAS_BASE_ANIMATION, builder.animation)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        L.d(TAG, "onCreate style:${builder.style} theme:${builder.theme} cancelable:${builder.cancelable}")
        setStyle(builder.style, builder.theme)
        isCancelable = builder.cancelable
        layoutRes = builder.layoutRes
        super.onCreate(savedInstanceState)
    }

    /**
     * dialog 布局两种实现方式
     * 1. builder构建的时候，layoutRes传布局id
     * 2.实现createView方法
     *     override fun createView(
     *         inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
     *     ): View {
     *         _viewBinding = reflectViewBinding(inflater, container)
     *         return viewBinding.root
     *     }
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        L.d(TAG, "onCreateView layoutRes:${layoutRes()}")
        return if (layoutRes() != Resources.ID_NULL) {
            inflater.inflate(layoutRes(), container, false)
        } else createView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        L.d(TAG, "onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        initView(savedInstanceState)
        initData(arguments)
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        L.d(TAG, "onCreateDialog")
        return createDialog(savedInstanceState) ?: super.onCreateDialog(savedInstanceState)
    }

    override fun onStart() {
        L.d(TAG, "onStart")
        dialog?.window?.let { initWindow(dialog!!, it) }
        super.onStart()
    }

    override fun onResume() {
        L.d(TAG, "onResume")
        super.onResume()
    }

    override fun onPause() {
        L.d(TAG, "onPause")
        super.onPause()
    }

    override fun onStop() {
        L.d(TAG, "onStop")
        super.onStop()
    }

    fun show(activity: FragmentActivity?) {
        L.d(TAG, "show activity.isFinishing:${activity?.isFinishing}")
        activity.takeIf { it?.isFinishing == false }?.let {
            show(it.supportFragmentManager, it::class.java.simpleName)
        }
    }

    fun show(fragment: Fragment?) {
        L.d(TAG, "show fragment.isAdded:${fragment?.isAdded}")
        fragment.takeIf { it?.isAdded == true }?.let {
            show(it.childFragmentManager, it::class.java.simpleName)
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        L.d(TAG, "show manager:${manager.isDestroyed} isRepeatedShow:${isRepeatedShow(tag)}")
        manager.takeIf { it.isDestroyed.not() && isRepeatedShow(tag).not() }?.let {
            super.show(manager, tag)
        }
    }

    override fun show(transaction: FragmentTransaction, tag: String?): Int {
        L.d(TAG, "show isRepeatedShow:${isRepeatedShow(tag)}")
        return if (isRepeatedShow(tag).not()) super.show(transaction, tag) else -1
    }

    override fun showNow(manager: FragmentManager, tag: String?) {
        L.d(TAG, "showNow manager:${manager.isDestroyed} isRepeatedShow:${isRepeatedShow(tag)}")
        if (manager.isDestroyed.not() && isRepeatedShow(tag).not()) super.showNow(manager, tag)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        initData(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.apply {
            title?.let { putCharSequence(EXTRAS_BASE_TITLE, it) }
            message?.let { putCharSequence(EXTRAS_BASE_MESSAGE, it) }
            putInt(EXTRAS_BASE_ANIMATION, animation)
            putInt(EXTRAS_BASE_LAYOUT, layoutRes)
            putInt(EXTRAS_BASE_WIDTH, width)
            putInt(EXTRAS_BASE_HEIGHT, height)
            putInt(EXTRAS_BASE_OFFSET_X, offsetX)
            putInt(EXTRAS_BASE_OFFSET_Y, offsetY)
        }
    }

    override fun layoutRes() = layoutRes

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initData(bundle: Bundle?) {
        bundle?.let {
            if (it.containsKey(EXTRAS_BASE_LAYOUT)) {
                layoutRes = it.getInt(EXTRAS_BASE_LAYOUT, layoutRes)
            }
            if (it.containsKey(EXTRAS_BASE_ANIMATION)) {
                animation = it.getInt(EXTRAS_BASE_ANIMATION, animation)
            }
            if (it.containsKey(EXTRAS_BASE_TITLE)) {
                title = it.getCharSequence(EXTRAS_BASE_TITLE, null)
            }
            if (it.containsKey(EXTRAS_BASE_MESSAGE)) {
                message = it.getCharSequence(EXTRAS_BASE_MESSAGE, null)
            }
            if (it.containsKey(EXTRAS_BASE_WIDTH)) {
                width = it.getInt(EXTRAS_BASE_WIDTH, width)
            }
            if (it.containsKey(EXTRAS_BASE_HEIGHT)) {
                height = it.getInt(EXTRAS_BASE_HEIGHT, height)
            }
            if (it.containsKey(EXTRAS_BASE_OFFSET_X)) {
                offsetX = it.getInt(EXTRAS_BASE_OFFSET_X, offsetX)
            }
            if (it.containsKey(EXTRAS_BASE_OFFSET_Y)) {
                offsetY = it.getInt(EXTRAS_BASE_OFFSET_Y, offsetY)
            }
        }
    }

    /*设置dialog window 属性*/
    open fun initWindow(dialog: Dialog, window: Window) {
        window.apply {
            // 透明背景
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            decorView.setPadding(0, 0, 0, 0)
            attributes.apply {
                //window 宽度
                width = this@BaseDialog.width
                height = this@BaseDialog.height
                //设置偏移量
                x = this@BaseDialog.offsetX
                y = this@BaseDialog.offsetY
                //设置对齐方式
                gravity = this@BaseDialog.gravity
                //设置动画
                windowAnimations = this@BaseDialog.animation
            }
        }
    }

    /*重写布局View*/
    abstract fun createView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View

    /*重新创建dialog*/
    abstract fun createDialog(savedInstanceState: Bundle?): Dialog?

    /**
     * 根据 tag 判断这个 Dialog 是否重复显示了
     *
     * @param tag Tag标记
     */
    private fun isRepeatedShow(tag: String?): Boolean {
        val result = tag == mShowTag && SystemClock.uptimeMillis() - mLastTime < 500
        mShowTag = tag
        mLastTime = SystemClock.uptimeMillis()
        return result
    }

    companion object {
        const val DEFAULT_DIALOG_STYLE = STYLE_NO_TITLE
        const val DEFAULT_DIALOG_CANCELABLE = true
        const val DEFAULT_DIALOG_WINDOW_WIDTH = LayoutParams.MATCH_PARENT
        const val DEFAULT_DIALOG_WINDOW_HEIGHT = LayoutParams.MATCH_PARENT
        const val DEFAULT_DIALOG_WINDOW_OFFSET = 0
        const val DEFAULT_DIALOG_WINDOW_GRAVITY = Gravity.CENTER
        const val EXTRAS_BASE_LAYOUT = "EXTRAS_BASE_LAYOUT"
        const val EXTRAS_BASE_ANIMATION = "EXTRAS_BASE_ANIMATION"
        const val EXTRAS_BASE_TITLE = "EXTRAS_BASE_TITLE"
        const val EXTRAS_BASE_MESSAGE = "EXTRAS_BASE_MESSAGE"
        const val EXTRAS_BASE_WIDTH = "EXTRAS_BASE_WIDTH"
        const val EXTRAS_BASE_HEIGHT = "EXTRAS_BASE_HEIGHT"
        const val EXTRAS_BASE_OFFSET_X = "EXTRAS_BASE_OFFSET_X"
        const val EXTRAS_BASE_OFFSET_Y = "EXTRAS_BASE_OFFSET_Y"
        const val EXTRAS_BASE_GRAVITY = "EXTRAS_BASE_GRAVITY"
        private var mShowTag: String? = null
        private var mLastTime: Long = 0
    }


    @Suppress("UNCHECKED_CAST")
    abstract class Builder<Returner, DIALOG>(
        val layoutRes: Int = Resources.ID_NULL,
        val style: Int = DEFAULT_DIALOG_STYLE,
        val theme: Int = R.style.Base_Dialog,
        val cancelable: Boolean = DEFAULT_DIALOG_CANCELABLE
    ) {
        @AnimRes
        internal var animation = android.R.style.Animation_Dialog

        internal var title: CharSequence? = null
        internal var message: CharSequence? = null

        @Dimension
        internal var width = DEFAULT_DIALOG_WINDOW_WIDTH

        @Dimension
        internal var height = DEFAULT_DIALOG_WINDOW_HEIGHT

        @Dimension
        internal var offsetX = DEFAULT_DIALOG_WINDOW_OFFSET

        @Dimension
        internal var offsetY = DEFAULT_DIALOG_WINDOW_OFFSET

        internal var gravity = DEFAULT_DIALOG_WINDOW_GRAVITY


        fun animation(@AnimRes animation: Int): Returner {
            this.animation = animation
            return this as Returner
        }

        fun title(title: CharSequence?): Returner {
            this.title = title
            return this as Returner
        }

        fun message(message: CharSequence?): Returner {
            this.message = message
            return this as Returner
        }

        fun size(@Dimension width: Int, @Dimension height: Int): Returner {
            this.width = width
            this.height = height
            return this as Returner
        }

        fun offset(@Dimension offsetX: Int, @Dimension offsetY: Int): Returner {
            this.offsetX = offsetX
            this.offsetY = offsetY
            return this as Returner
        }

        fun gravity(gravity: Int): Returner {
            this.gravity = gravity
            return this as Returner
        }

        abstract fun build(): DIALOG
    }


}




