package com.colin.library.android.widget.banner.drawer

import com.colin.library.android.widget.banner.def.IndicatorStyle
import com.colin.library.android.widget.banner.indicator.IIndicator

/**
 * Indicator Drawer Factory.
 */
internal object DrawerFactory {
    fun createDrawer(indicator: IIndicator): IDrawer {
        return when (indicator.getIndicatorStyle()) {
            IndicatorStyle.DASH -> DashDrawer(indicator)
            IndicatorStyle.ROUND_RECT -> RoundRectDrawer(indicator)
            else -> CircleDrawer(indicator)
        }
    }
}
