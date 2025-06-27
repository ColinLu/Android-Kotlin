package com.colin.library.android.widget.banner.drawer

import android.animation.ArgbEvaluator
import android.annotation.SuppressLint
import android.graphics.Paint
import android.widget.LinearLayout
import com.colin.library.android.widget.banner.indicator.IIndicator
import kotlin.math.max
import kotlin.math.min

/**
 * Drawer基类
 */
abstract class BaseDrawer internal constructor(internal val indicator: IIndicator) : IDrawer {
    internal val evaluator by lazy { ArgbEvaluator() }
    internal val paint: Paint = Paint().apply { isAntiAlias = true }


    protected val isWidthEquals: Boolean
        get() = indicator.getSelectWidth() == indicator.getNormalWidth()
                && indicator.getSelectHeight() == indicator.getNormalHeight()


    @SuppressLint("DrawAllocation")
    override fun onMeasure(
        widthMeasureSpec: Int, heightMeasureSpec: Int
    ): IDrawer.MeasureSize {
        return IDrawer.MeasureSize(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec))
    }

    override fun measureHeight(heightMeasureSpec: Int): Float {
        return if (indicator.getOrientation() == LinearLayout.HORIZONTAL) getMaxHeight()
        else (indicator.getNormalHeight() + indicator.getIndicatorSpace()) * (indicator.getItemCount() - 1) + indicator.getSelectHeight()
    }

    override fun measureWidth(widthMeasureSpec: Int): Float {
        return if (indicator.getOrientation() == LinearLayout.VERTICAL) getMaxWidth()
        else (indicator.getNormalWidth() + indicator.getIndicatorSpace()) * (indicator.getItemCount() - 1) + indicator.getSelectWidth()
    }

    override fun onLayout(
        changed: Boolean, left: Int, top: Int, right: Int, bottom: Int
    ) {
    }

    internal fun getMaxHeight() = max(indicator.getSelectHeight(), indicator.getNormalHeight())
    internal fun getMinHeight() = min(indicator.getSelectHeight(), indicator.getNormalHeight())
    internal fun getMaxWidth() = max(indicator.getSelectWidth(), indicator.getNormalWidth())
    internal fun getMinWidth() = min(indicator.getSelectWidth(), indicator.getNormalWidth())

    internal fun getOffsetX(position: Int): Float {
        val offset = if (position >= indicator.getPosition()) indicator.getSelectWidth() / 2 else indicator.getNormalWidth() / 2
        return offset + (indicator.getNormalWidth() + indicator.getIndicatorSpace()) * position
    }

    internal fun getOffsetY(height: Float = getMaxHeight()): Float {
        return height / 2
    }

}