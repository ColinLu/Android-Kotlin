package com.colin.library.android.widget.banner.drawer

import android.graphics.Canvas
import android.graphics.RectF
import com.colin.library.android.widget.banner.def.IndicatorMode
import com.colin.library.android.widget.banner.indicator.IIndicator

/**
 * 圆形Drawer
 */
class CircleDrawer internal constructor(indicator: IIndicator) : BaseDrawer(indicator) {

    private val rectF = RectF()


    override fun onDraw(canvas: Canvas) {
        if (!indicator.showIndicator()) return
        drawNormal(canvas, indicator.getItemCount())
        drawSelected(canvas, indicator.getPosition())
    }

    private fun drawNormal(canvas: Canvas, count: Int) {
        paint.color = indicator.getNormalColor()
        val width = indicator.getNormalWidth()
        for (i in 0 until count) {
            drawCircle(canvas, getOffsetX(i), width / 2, width / 2)
        }
    }

    private fun drawSelected(canvas: Canvas, position: Int) {
        paint.color = indicator.getSelectColor()
        when (indicator.getIndicatorMode()) {
            IndicatorMode.NORMAL, IndicatorMode.SMOOTH -> drawCircleSmooth(canvas, position)
            IndicatorMode.WORM -> drawCircleWorm(canvas, position)
            IndicatorMode.SCALE -> drawScaleSlider(canvas, position)
            IndicatorMode.COLOR -> drawColor(canvas, position)
        }
    }

    private fun drawCircleSmooth(canvas: Canvas, position: Int) {
        val offsetX = getOffsetX(position)
        val offsetY = getMaxHeight() / 2
        drawCircle(canvas, offsetX, getMaxHeight() / 2, offsetY)
    }

    private fun drawCircleWorm(canvas: Canvas, position: Int) {
        val height = indicator.getSelectHeight()
        val space = indicator.getIndicatorSpace()
        val progress = indicator.getScrollProgress()
        val distance = space + indicator.getNormalWidth()
        val offsetX = getOffsetX(position)
        val left =
            offsetX + (distance * (progress - 0.5f) * 2.0f).coerceAtLeast(0f) - indicator.getSelectWidth() / 2
        val right =
            offsetX + (distance * progress * 2f).coerceAtMost(distance) + indicator.getSelectWidth() / 2
        rectF.set(left, 0F, right, height)
        canvas.drawRoundRect(rectF, height, height, paint)
    }

    private fun drawScaleSlider(canvas: Canvas, position: Int) {
        val progress = indicator.getScrollProgress()
        val offsetX = getOffsetX(position)
        val offsetY = getOffsetY(indicator.getSelectHeight())
        if (progress < 1) {
            val evaluate = evaluator.evaluate(
                progress, indicator.getSelectColor(), indicator.getNormalColor()
            )
            paint.color = (evaluate as Int)
            val radius =
                indicator.getSelectWidth() / 2 - (indicator.getSelectWidth() / 2 - indicator.getNormalWidth() / 2) * progress
            drawCircle(canvas, offsetX, offsetY, radius)
        }

        if (position + 1 == indicator.getItemCount()) {
            val evaluate = evaluator.evaluate(
                progress, indicator.getNormalColor(), indicator.getSelectColor()
            )
            paint.color = evaluate as Int
            val nextOffsetX = getMaxWidth() / 2
            val nextRadius =
                getMinWidth() / 2 + (getMaxWidth() / 2 - getMinWidth() / 2) * (progress)
            drawCircle(canvas, nextOffsetX, offsetY, nextRadius)
        } else {
            if (progress > 0) {
                val evaluate = evaluator.evaluate(
                    progress, indicator.getNormalColor(), indicator.getSelectColor()
                )
                paint.color = evaluate as Int
                val nextOffsetX =
                    offsetX + indicator.getIndicatorSpace() + indicator.getNormalWidth()
                val nextRadius =
                    indicator.getNormalWidth() / 2 + (indicator.getSelectWidth() / 2 - indicator.getNormalWidth() / 2) * progress
                drawCircle(canvas, nextOffsetX, offsetY, nextRadius)
            }
        }
    }

    private fun drawColor(canvas: Canvas, position: Int) {
        val progress = indicator.getScrollProgress()
        val offsetX = getOffsetX(position)
        val offsetY = getOffsetY()
        paint.color = evaluator.evaluate(
            progress, indicator.getSelectColor(), indicator.getNormalColor()
        ) as Int
        drawCircle(canvas, offsetX, offsetY, indicator.getSelectWidth() / 2)
    }


    private fun drawCircle(
        canvas: Canvas, coordinateX: Float, coordinateY: Float, radius: Float
    ) {
        canvas.drawCircle(coordinateX, coordinateY, radius, paint)
    }
}
