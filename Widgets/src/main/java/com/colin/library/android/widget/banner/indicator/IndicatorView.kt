package com.colin.library.android.widget.banner.indicator

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.LinearLayout
import com.colin.library.android.widget.banner.drawer.DrawerProxy
import kotlin.div

/**
 * The Indicator in BannerViewPager，this include three indicator styles,as below:
 * [com.colin.library.android.widget.banner.def.IndicatorStyle.CIRCLE]
 * [com.colin.library.android.widget.banner.def.IndicatorStyle.DASH]
 * [com.colin.library.android.widget.banner.def.IndicatorStyle.ROUND_RECT]
 */
class IndicatorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : IIndicatorView(context, attrs, defStyleAttr) {

    private val proxy by lazy {
        DrawerProxy(this)
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (!showIndicator()) return
        val value = proxy.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(value.width.toInt(), value.height.toInt())
    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (!showIndicator()) return
        super.onLayout(changed, left, top, right, bottom)
        proxy.onLayout(changed, left, top, right, bottom)
    }

    override fun onDraw(canvas: Canvas) {
        if (!showIndicator()) return
        if (getOrientation() == LinearLayout.VERTICAL) {
            canvas.rotate(90f, width / 2f, width / 2f)
        }
        proxy.onDraw(canvas)
    }

    override fun setIndicatorStyle(style: Int) {
        if (indicatorStyle != style) {
            indicatorStyle = style
            proxy.setSwitchMode(this)
            notifyDataChanged()
        }
    }

    override fun setIndicatorMode(mode: Int) {
        if (indicatorMode != mode) {
            indicatorMode = mode
            proxy.setSwitchMode(this)
            notifyDataChanged()
        }
    }

}
