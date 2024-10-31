package com.colin.library.android.base

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.colin.library.android.base.def.IBase


/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-09-11
 *
 * Des   :Dialog基类:最简单的业务逻辑定义
 */
abstract class BaseDialog<out DIALOG> : DialogFragment(), IBase {

    init {
        val TAG = this::class.simpleName!!
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
        return if (layoutRes() != Resources.ID_NULL) {
            inflater.inflate(layoutRes(), container, false)
        } else createView(inflater, container, savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return createDialog(savedInstanceState) ?: super.onCreateDialog(savedInstanceState)
    }

    override fun onStart() {
        val window = dialog?.window ?: return
        initWindow(window)
        super.onStart()
    }


    fun show(activity: FragmentActivity?) {
        if (activity?.isFinishing == false) {
            show(activity.supportFragmentManager, activity::class.java.simpleName)
        }
    }

    fun show(fragment: Fragment?) {
        if (fragment?.isAdded == true) {
            show(fragment.childFragmentManager, fragment::class.java.simpleName)
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        if (!manager.isDestroyed && !isRepeatedShow(tag)) {
            super.show(manager, tag)
        }
    }

    override fun show(transaction: FragmentTransaction, tag: String?): Int {
        return if (!isRepeatedShow(tag)) super.show(transaction, tag) else -1
    }

    override fun showNow(manager: FragmentManager, tag: String?) {
        if (!manager.isDestroyed && !isRepeatedShow(tag)) super.showNow(manager, tag)
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

    companion object {
        private var sShowTag: String? = null
        private var sLastTime: Long = 0
        private var mTitle: CharSequence? = null
        private var mBuildMethod: ((Dialog?, View?) -> Unit)? = null
        private var mDismissMethod: (() -> Unit)? = null
        private var mNegativeButtonMethod: ((Dialog?, View?) -> Unit)? = null
        private var mPositiveButtonMethod: ((Dialog?, View?) -> Unit)? = null

    }


    abstract class Builder<DIALOG>(val layoutRes: Int = Resources.ID_NULL) {
        fun title(title: CharSequence) = apply {
            mTitle = title
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




