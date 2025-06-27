package com.colin.library.android.widget.banner.drawer

import android.R.attr.minWidth
import android.graphics.Canvas
import android.graphics.RectF
import com.colin.library.android.widget.banner.def.IndicatorMode
import com.colin.library.android.widget.banner.indicator.IIndicator

/**
 * 矩形Drawer
 */
open class RectDrawer internal constructor(indicator: IIndicator) : BaseDrawer(indicator) {
    internal var mRectF: RectF = RectF()

    override fun onDraw(canvas: Canvas) {
        if (!indicator.showIndicator()) return
        val count = indicator.getItemCount()
        if (isWidthEquals && indicator.getIndicatorMode() != IndicatorMode.NORMAL) {
            drawUncheckedSlider(canvas, count)
            drawCheckedSlider(canvas)
        } else {
            if (indicator.getIndicatorMode() == IndicatorMode.SCALE) {
                for (i in 0 until count) drawScaleSlider(canvas, i)
            } else {
                drawInequalitySlider(canvas, count)
            }
        }
    }

    private fun drawCheckedSlider(canvas: Canvas) {
        paint.color = indicator.getSelectColor()
        when (indicator.getIndicatorMode()) {
            IndicatorMode.SMOOTH -> drawSmoothSlider(canvas)
            IndicatorMode.WORM -> drawWormSlider(canvas)
            IndicatorMode.COLOR -> drawColorSlider(canvas)
        }
    }

    private fun drawScaleSlider(canvas: Canvas, index: Int) {
        val current = indicator.getPosition()
        val checkedColor = indicator.getSelectColor()
        val indicatorGap = indicator.getIndicatorSpace()
        val sliderHeight = indicator.getSelectHeight()
        val normalWidth = indicator.getNormalWidth()
        val checkedWidth = indicator.getSelectWidth()
        when {
            index < current -> {
                paint.color = indicator.getNormalColor()
                val left: Float = if (current == indicator.getItemCount() - 1) {
                    (index * normalWidth + index * indicatorGap) + (checkedWidth - normalWidth) * indicator.getScrollProgress()
                } else {
                    (index * normalWidth + index * indicatorGap)
                }
                mRectF.set(left, 0f, left + normalWidth, sliderHeight)
                drawRoundRect(canvas, sliderHeight, sliderHeight)
            }

            index == current -> {
                paint.color = checkedColor
                val slideProgress = indicator.getScrollProgress()
                if (current == indicator.getItemCount() - 1) {
                    val evaluate = evaluator.evaluate(
                        slideProgress, checkedColor, indicator.getNormalColor()
                    )
                    paint.color = (evaluate as Int)
                    val right =
                        (indicator.getItemCount() - 1) * (normalWidth + indicator.getIndicatorSpace()) + checkedWidth
                    val left = right - checkedWidth + (checkedWidth - normalWidth) * (slideProgress)
                    mRectF.set(left, 0f, right, sliderHeight)
                    drawRoundRect(canvas, sliderHeight, sliderHeight)
                } else {
                    if (slideProgress < 1) {
                        val evaluate = evaluator.evaluate(
                            slideProgress, checkedColor, indicator.getNormalColor()
                        )
                        paint.color = (evaluate as Int)
                        val left = index * normalWidth + index * indicatorGap
                        val right =
                            left + normalWidth + (checkedWidth - normalWidth) * (1 - slideProgress)
                        mRectF.set(left, 0f, right, sliderHeight)
                        drawRoundRect(canvas, sliderHeight, sliderHeight)
                    }
                }

                if (current + 1 == indicator.getItemCount()) {
                    if (slideProgress > 0) {
                        val evaluate = evaluator.evaluate(
                            1 - slideProgress, checkedColor, indicator.getNormalColor()
                        )
                        paint.color = evaluate as Int
                        val left = 0f
                        val right =
                            left + normalWidth + (checkedWidth - normalWidth) * slideProgress

                        mRectF.set(left, 0f, right, sliderHeight)
                        drawRoundRect(canvas, sliderHeight, sliderHeight)
                    }
                } else {
                    if (slideProgress > 0) {
                        val evaluate = evaluator.evaluate(
                            1 - slideProgress, checkedColor, indicator.getNormalColor()
                        )
                        paint.color = evaluate as Int
                        val right =
                            index * normalWidth + index * indicatorGap + normalWidth + (indicatorGap + checkedWidth)
                        val left =
                            right - (normalWidth) - (checkedWidth - normalWidth) * (slideProgress)
                        mRectF.set(left, 0f, right, sliderHeight)
                        drawRoundRect(canvas, sliderHeight, sliderHeight)
                    }
                }
            }

            else -> {
                if ((current + 1 != index || indicator.getScrollProgress() == 0f)) { // 避免多余绘制
                    paint.color = indicator.getNormalColor()
                    val left =
                        index * getMinWidth() + index * indicatorGap + (checkedWidth - getMinWidth())
                    mRectF.set(left, 0f, left + getMinWidth(), sliderHeight)
                    drawRoundRect(canvas, sliderHeight, sliderHeight)
                }
            }
        }
    }

    private fun drawUncheckedSlider(
        canvas: Canvas, pageSize: Int
    ) {
        for (i in 0 until pageSize) {
            paint.color = indicator.getNormalColor()
            val left =
                i * getMaxWidth() + i * +indicator.getIndicatorSpace() + (getMaxWidth() - getMinWidth())
            mRectF.set(left, 0f, left + minWidth, indicator.getNormalHeight())
            drawRoundRect(canvas, indicator.getNormalHeight(), indicator.getNormalHeight())
        }
    }

    private fun drawInequalitySlider(
        canvas: Canvas, pageSize: Int
    ) {
        var left = 0f
        for (i in 0 until pageSize) {
            val sliderWidth = (if (i == indicator.getPosition()) getMaxWidth() else getMinWidth())
            paint.color =
                if (i == indicator.getPosition()) indicator.getSelectColor() else indicator.getNormalColor()
            mRectF.set(left, 0f, left + sliderWidth, indicator.getSelectHeight())
            drawRoundRect(canvas, indicator.getSelectHeight(), indicator.getSelectHeight())
            left += sliderWidth + indicator.getIndicatorSpace()
        }
    }


    private fun drawColorSlider(canvas: Canvas) {
        val currentPosition = indicator.getPosition()
        val slideProgress = indicator.getScrollProgress()
        val left = currentPosition * minWidth + currentPosition * indicator.getIndicatorSpace()
        if (slideProgress < 0.99) {
            val evaluate = evaluator.evaluate(
                slideProgress, indicator.getSelectColor(), indicator.getNormalColor()
            )
            paint.color = (evaluate as Int)
            mRectF.set(left, 0f, left + minWidth, indicator.getSelectHeight())
            drawRoundRect(canvas, indicator.getSelectHeight(), indicator.getSelectHeight())
        }

        var nextSliderLeft = left + indicator.getIndicatorSpace() + indicator.getNormalWidth()
        if (currentPosition + 1 == indicator.getItemCount()) {
            nextSliderLeft = 0f
        }
        val evaluate = evaluator.evaluate(
            1 - slideProgress, indicator.getSelectColor(), indicator.getNormalColor()
        )
        paint.color = evaluate as Int
        mRectF.set(nextSliderLeft, 0f, nextSliderLeft + getMinWidth(), indicator.getSelectHeight())
        drawRoundRect(canvas, indicator.getSelectHeight(), indicator.getSelectHeight())
    }

    private fun drawWormSlider(canvas: Canvas) {
        val sliderHeight = indicator.getSelectHeight()
        val slideProgress = indicator.getScrollProgress()
        val currentPosition = indicator.getPosition()
        val distance = indicator.getIndicatorSpace() + indicator.getNormalWidth()
        val startCoordinateX = getOffsetX(currentPosition)
        val left = startCoordinateX + (distance * (slideProgress - 0.5f) * 2.0f).coerceAtLeast(
            0f
        ) - indicator.getNormalWidth() / 2
        val right = startCoordinateX + (distance * slideProgress * 2f).coerceAtMost(
            distance
        ) + indicator.getNormalWidth() / 2
        mRectF.set(left, 0f, right, sliderHeight)
        drawRoundRect(canvas, sliderHeight, sliderHeight)
    }

    private fun drawSmoothSlider(canvas: Canvas) {
        val currentPosition = indicator.getPosition()
        val indicatorGap = indicator.getIndicatorSpace()
        val sliderHeight = indicator.getSelectHeight()
        val left =
            currentPosition * getMaxWidth() + currentPosition * +indicatorGap + (getMaxWidth() + indicatorGap) * indicator.getScrollProgress()
        mRectF.set(left, 0f, left + getMaxWidth(), sliderHeight)
        drawRoundRect(canvas, sliderHeight, sliderHeight)
    }

    protected open fun drawRoundRect(
        canvas: Canvas, rx: Float, ry: Float
    ) {
        drawDash(canvas)
    }

    protected open fun drawDash(canvas: Canvas) {}
}
