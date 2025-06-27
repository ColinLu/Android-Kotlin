package com.colin.library.android.widget.banner.indicator

import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.annotation.Px
import androidx.viewpager.widget.ViewPager
import com.colin.library.android.utils.ZERO
import com.colin.library.android.widget.INDICATOR_NORMAL_COLOR
import com.colin.library.android.widget.INDICATOR_NORMAL_WIDTH
import com.colin.library.android.widget.INDICATOR_RADIUS
import com.colin.library.android.widget.INDICATOR_SELECTED_COLOR
import com.colin.library.android.widget.INDICATOR_SELECTED_WIDTH
import com.colin.library.android.widget.INDICATOR_SPACE
import com.colin.library.android.widget.banner.def.IndicatorMode
import com.colin.library.android.widget.banner.def.IndicatorStyle


interface IIndicator : ViewPager.OnPageChangeListener {
    fun getView(): View
    fun getItemCount(): Int = ZERO
    fun setItemCount(count: Int)
    fun getPosition(): Int = ZERO
    fun setPosition(position: Int)
    fun setItemCount(count: Int, position: Int) {}
    fun getIndicatorStyle(): Int = IndicatorStyle.CIRCLE
    fun setIndicatorStyle(@IndicatorStyle style: Int) {}
    fun getIndicatorMode(): Int = IndicatorMode.NORMAL
    fun setIndicatorMode(@IndicatorMode mode: Int) {}

    @FloatRange(from = 0.0, to = 1.0)
    fun getScrollProgress(): Float = ZERO.toFloat()

    @Px
    fun getSelectWidth(): Float = INDICATOR_SELECTED_WIDTH

    @Px
    fun getSelectHeight(): Float = INDICATOR_SELECTED_WIDTH
    fun setSelect(@Px width: Float, @Px height: Float) {}

    @Px
    fun getNormalWidth(): Float = INDICATOR_NORMAL_WIDTH

    @Px
    fun getNormalHeight(): Float = INDICATOR_NORMAL_WIDTH
    fun setNormal(@Px width: Float, @Px height: Float) {}

    @Px
    fun getSelectRadius(): Float = INDICATOR_RADIUS

    @Px
    fun getNormalRadius(): Float = INDICATOR_RADIUS
    fun setRadius(@Px select: Float, @Px normal: Float) {}

    @Px
    fun getIndicatorSpace(): Float = INDICATOR_SPACE
    fun setIndicatorSpace(@Px space: Float) {}

    @ColorInt
    fun getSelectColor(): Int = INDICATOR_SELECTED_COLOR

    @ColorInt
    fun getNormalColor(): Int = INDICATOR_NORMAL_COLOR

    fun setColor(@ColorInt select: Int, @ColorInt normal: Int)

    fun getOrientation(): Int = LinearLayout.HORIZONTAL

    fun setOrientation(orientation: Int) {}

    fun getGravity(): Int = Gravity.CENTER
    fun setGravity(gravity: Int) {}

    fun showIndicator() = false

    fun notifyDataChanged() {}


}
