package com.colin.library.android.widget.banner.drawer

import android.graphics.Canvas
import com.colin.library.android.widget.banner.indicator.IIndicator

/**
 * 圆角Drawer
 */
class RoundRectDrawer internal constructor(indicator: IIndicator) : RectDrawer(indicator) {

    override fun drawRoundRect(canvas: Canvas, rx: Float, ry: Float) {
        canvas.drawRoundRect(mRectF, rx, ry, paint)
    }
}
