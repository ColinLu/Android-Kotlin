package com.colin.library.android.base

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.os.SystemClock
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
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
abstract class BaseDialog : DialogFragment(), IBase {
    val TAG = this::class.simpleName!!

    init {
        arguments = Bundle()
    }

    abstract fun createView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View

    abstract fun initWindow(window: Window)

    abstract fun createDialog(savedInstanceState: Bundle?): Dialog?


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        L.d(TAG, "onCreateView layoutRes:${layoutRes()}")
        return if (layoutRes() != Resources.ID_NULL) {
            inflater.inflate(layoutRes(), container, false)
        } else createView(inflater, container, savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        L.d(TAG, "onCreateDialog")
        return createDialog(savedInstanceState) ?: super.onCreateDialog(savedInstanceState)
    }

    override fun onStart() {
        L.d(TAG, "onStart")
        val window = dialog?.window ?: return
        initWindow(window)
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
        private var mShowTag: String? = null
        private var mLastTime: Long = 0
        private var mTitle: CharSequence? = null
        private var mWidth = FrameLayout.LayoutParams.WRAP_CONTENT
        private var mHeight = FrameLayout.LayoutParams.WRAP_CONTENT
        private var mOffsetX = 0F
        private var mOffsetY = 0F
        private var mGravity = Gravity.CENTER
        private var mAnimation = android.R.style.Animation_Dialog
        private var mBuildMethod: ((Dialog?, View?) -> Unit)? = null
        private var mDismissMethod: (() -> Unit)? = null
        private var mNegativeButtonMethod: ((Dialog?, View?) -> Unit)? = null
        private var mPositiveButtonMethod: ((Dialog?, View?) -> Unit)? = null

    }


    @Suppress("UNCHECKED_CAST")
    abstract class Builder<Returner, DIALOG>(
        val theme: Int = android.R.style.Theme_Dialog, val layoutRes: Int = Resources.ID_NULL
    ) {
        fun setTitle(title: CharSequence): Returner {
            mTitle = title
            return this as Returner
        }

        fun setSize(width: Int, height: Int): Returner {
            mWidth = width
            mHeight = height
            return this as Returner
        }

        fun setOffset(offsetX: Float, offsetY: Float, gravity: Int = Gravity.CENTER): Returner {
            mOffsetX = offsetX
            mOffsetY = offsetY
            mGravity = gravity
            return this as Returner
        }

        abstract fun build(): DIALOG
    }


}


//        private var mWidth = WRAP_CONTENT
//        private var mHeight = WRAP_CONTENT
//        private var mGravity = CENTER
//        private var mOffsetX = 0
//        private var mOffsetY = 0
//        private var mAnimation = R.style.DialogBaseAnimation
//
//        fun setSize(mWidth: Int, mHeight: Int): T {
//            this.mWidth = mWidth
//            this.mHeight = mHeight
//            @Suppress("UNCHECKED_CAST")
//            return this as T
//        }
//
//        fun setGravity(mGravity: Int): T {
//            this.mGravity = mGravity
//            @Suppress("UNCHECKED_CAST")
//            return this as T
//        }
//
//        fun setOffsetX(mOffsetX: Int): T {
//            this.mOffsetX = mOffsetX
//            @Suppress("UNCHECKED_CAST")
//            return this as T
//        }
//
//        fun setOffsetY(mOffsetY: Int): T {
//            this.mOffsetY = mOffsetY
//            @Suppress("UNCHECKED_CAST")
//            return this as T
//        }
//
//        fun setAnimation(mAnimation: Int): T {
//            this.mAnimation = mAnimation
//            @Suppress("UNCHECKED_CAST")
//            return this as T
//        }
//
//        protected abstract fun build(): D
//
//        protected fun clear() {
//            this.mWidth = WRAP_CONTENT
//            this.mHeight = WRAP_CONTENT
//            this.mGravity = CENTER
//            this.mOffsetX = 0
//            this.mOffsetY = 0
//        }
//
//        companion object {
//            const val WRAP_CONTENT = -2
//            const val CENTER = -1
//        }
//    }

////    abstract class Builder<T out Builder, D out BaseDialog> {
//        private int mWidth = WRAP_CONTENT;
//        private int mHeight = WRAP_CONTENT;
//        private int mGravity = CENTER;
//        private int mOffsetX = 0;
//        private int mOffsetY = 0;
//        private int mAnimation = R.style.DialogBaseAnimation;
//
//        public T setSize(int mWidth, int mHeight) {
//            this.mWidth = mWidth;
//            this.mHeight = mHeight;
//            return (T) this;
//        }
//
//        public T setGravity(int mGravity) {
//            this.mGravity = mGravity;
//            return (T) this;
//        }
//
//        public T setOffsetX(int mOffsetX) {
//            this.mOffsetX = mOffsetX;
//            return (T) this;
//        }
//
//        public T setOffsetY(int mOffsetY) {
//            this.mOffsetY = mOffsetY;
//            return (T) this;
//        }
//
//        public T setAnimation(int mAnimation) {
//            this.mAnimation = mAnimation;
//            return (T) this;
//        }
//
//        protected abstract D build();
//
//        protected void clear() {
//            this.mWidth = WRAP_CONTENT;
//            this.mHeight = WRAP_CONTENT;
//            this.mGravity = CENTER;
//            this.mOffsetX = 0;
//            this.mOffsetY = 0;
//        }
//    }




