package com.colin.library.android.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.core.content.withStyledAttributes

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025-07-01 10:32
 *
 * Des   :DividerView
 */
class DividerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val INTERVALS = floatArrayOf(6f, 2f)

    private val paint by lazy {
        Paint().apply {
            this.isAntiAlias = true
            this.style = this@DividerView.lineStyle
            this.strokeWidth = this@DividerView.lineStrike
            this.color = this@DividerView.lineColor
            this.setPathEffect(DashPathEffect(INTERVALS, 0F))
        }
    }
    var orientation = LinearLayout.HORIZONTAL
        set(value) {
            if (field != value) {
                field = value
                requestLayout()
            }
        }

    @Px
    var lineStrike = 0F
        set(value) {
            if (field == value) return
            field = value
            paint.strokeWidth = value
            invalidate()
        }


    @ColorInt
    var lineColor = Color.GRAY
        set(value) {
            if (field == value) return
            field = value
            paint.color = value
            invalidate()
        }

    var lineStyle = Paint.Style.STROKE
        set(value) {
            if (field == value) return
            field = value
            paint.style = value
            invalidate()
        }

    fun setPath(phase: Float, vararg intervals: Float) {
        paint.setPathEffect(DashPathEffect(intervals, phase))
        invalidate()
    }

    fun setPath(effect: DashPathEffect) {
        paint.setPathEffect(effect)
        invalidate()
    }

    init {
        context.withStyledAttributes(attrs, R.styleable.DividerView, defStyleAttr, 0) {
            orientation = getInt(R.styleable.DividerView_android_orientation, orientation)
            lineStyle = getPaintStyle(getInt(R.styleable.DividerView_lineStyle, 0))
            lineStrike = getDimension(R.styleable.DividerView_lineStrike, lineStrike)
            lineColor = getColor(R.styleable.DividerView_lineColor, lineColor)
        }
    }


    override fun onDraw(canvas: Canvas) {
        if (orientation == LinearLayout.HORIZONTAL) {
            val center = height * 0.5f
            canvas.drawLine(0f, center, width.toFloat(), center, paint)
        } else {
            val center = width * 0.5f
            canvas.drawLine(center, 0f, center, height.toFloat(), paint)
        }
    }

    private fun getPaintStyle(style: Int): Paint.Style {
        return if (style == 1) Paint.Style.STROKE
        else if (style == 2) Paint.Style.FILL_AND_STROKE
        else Paint.Style.FILL
    }

}