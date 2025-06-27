package com.colin.library.android.widget.banner.drawer

import android.graphics.Canvas

/**
 * IDrawer
 */
interface IDrawer {
    data class MeasureSize(val width: Float, val height: Float)

    fun measureWidth(widthMeasureSpec: Int): Float

    fun measureHeight(heightMeasureSpec: Int): Float

    fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int): MeasureSize

    fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int)

    fun onDraw(canvas: Canvas)


}
