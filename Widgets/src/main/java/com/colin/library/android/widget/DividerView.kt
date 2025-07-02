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
    private lateinit var paint: Paint
    var orientation = LinearLayout.HORIZONTAL
        set(value) {
            if (field != value) {
                field = value
                requestLayout()
            }
        }


    @Px
    private var length = 0F
        set(value) {
            if (field == value) return
            field = value
            if (::paint.isInitialized.not()) return
            paint.strokeWidth = value
            invalidate()
        }


    @ColorInt
    private var color = Color.GRAY
        set(value) {
            if (field == value) return
            field = value
            if (::paint.isInitialized.not()) return
            paint.color = value
            invalidate()
        }

    @Px
    private var space = 0F

    @Px
    private var dash = 0F

    init {
        context.withStyledAttributes(attrs, R.styleable.DividerView, defStyleAttr, 0) {
            orientation = getInt(R.styleable.DividerView_android_orientation, orientation)
            space = getDimension(R.styleable.DividerView_space, space)
            length = getDimension(R.styleable.DividerView_length, length)
            dash = getDimension(R.styleable.DividerView_dash, dash)
            color = getColor(R.styleable.DividerView_color, color)
        }
        paint = Paint().apply {
            this.isAntiAlias = true
            this.style = Paint.Style.STROKE
            this.strokeWidth = length
            this.color = color
            setPathEffect(DashPathEffect(floatArrayOf(space, dash), 0f))
        }
    }


    override fun onDraw(canvas: Canvas) {
        if (::paint.isInitialized.not()) return
        if (orientation == LinearLayout.HORIZONTAL) {
            val center = height * 0.5f
            canvas.drawLine(0f, center, width.toFloat(), center, paint)
        } else {
            val center = width * 0.5f
            canvas.drawLine(center, 0f, center, height.toFloat(), paint)
        }
    }

}