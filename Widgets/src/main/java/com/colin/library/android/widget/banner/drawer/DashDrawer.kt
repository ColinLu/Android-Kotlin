package com.colin.library.android.widget.banner.drawer

import android.graphics.Canvas
import com.colin.library.android.widget.banner.indicator.IIndicator

/**
 * DashDrawer
 */
class DashDrawer internal constructor(indicator: IIndicator) : RectDrawer(indicator) {

    override fun drawDash(canvas: Canvas) {
        canvas.drawRect(mRectF, paint)
    }
}
