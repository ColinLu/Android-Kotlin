package com.colin.library.android.utils

import android.view.View.MeasureSpec
import androidx.annotation.Px
import kotlin.math.min

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025-07-02 15:56
 *
 * Des   :ViewUtil
 */
object ViewUtil {
    /**
     * Android 中的 MeasureSpec 有以下三种 mode：
     * 1.EXACTLY
     * 含义：父容器已经为子 View 指定了一个确定的大小，子 View 必须使用这个指定的大小。
     * 使用场景：
     * 当 View 的宽高设置为具体数值时（例如 android:layout_width="100dp"）。
     * 或者在某些情况下设置为 match_parent（填充父容器），此时父容器会给出一个确切的大小。
     * 2.AT_MOST
     * 含义：子 View 可以尽可能大，但不能超过父容器给定的最大值。
     * 使用场景：
     * 当 View 的宽高设置为 wrap_content，表示子 View 应该根据内容决定自己的大小，但最大不超过父容器允许的范围。
     * 3.UNSPECIFIED
     * 含义：父容器没有对子 View 施加任何约束，子 View 可以想要多大就多大。
     * 使用场景：
     * 主要用于系统内部，比如 ScrollView 中的子 View。
     * 一般开发者很少遇到，除非在自定义 View 的 onMeasure() 方法中处理特殊逻辑。
     */
    fun getMeasureDimen(measureSpec: Int, @Px suggested: Int): Int {
        return when (MeasureSpec.getMode(measureSpec)) {
            //父容器已经为子View指定了一个确定的大小，子View必须使用这个指定的大小
            MeasureSpec.EXACTLY -> MeasureSpec.getSize(measureSpec)
            //直接使用提供的大小
            MeasureSpec.AT_MOST -> min(MeasureSpec.getSize(measureSpec), suggested)
            //未指定模式：自由测量，通常按内容需求来定
            MeasureSpec.UNSPECIFIED -> suggested
            else -> suggested
        }
    }

}