package com.colin.library.android.widget.banner.drawer

import android.graphics.Canvas
import com.colin.library.android.widget.banner.indicator.IIndicator

/**
 * Indicator Drawer Proxy.
 */
class DrawerProxy(indicator: IIndicator) : IDrawer {

    private lateinit var drawer: IDrawer

    init {
        init(indicator)
    }

    private fun init(indicator: IIndicator) {
        drawer = DrawerFactory.createDrawer(indicator)
    }

    fun setSwitchMode(indicator: IIndicator) {
        init(indicator)
    }

    override fun measureWidth(widthMeasureSpec: Int): Float {
        return drawer.measureWidth(widthMeasureSpec)
    }

    override fun measureHeight(heightMeasureSpec: Int): Float {
        return drawer.measureHeight(heightMeasureSpec)
    }

    override fun onMeasure(
        widthMeasureSpec: Int, heightMeasureSpec: Int
    ): IDrawer.MeasureSize {
        return drawer.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(
        changed: Boolean, left: Int, top: Int, right: Int, bottom: Int
    ) {
        drawer.onLayout(changed, left, top, right, bottom)
    }

    override fun onDraw(canvas: Canvas) {
        drawer.onDraw(canvas)
    }
}
