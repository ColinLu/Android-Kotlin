package com.colin.library.android.widget.banner.def

import androidx.annotation.IntDef

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025-05-24 12:09
 *
 * Des   :BannerIndicatorDef
 */


@IntDef(IndicatorStyle.CIRCLE, IndicatorStyle.DASH, IndicatorStyle.ROUND_RECT)
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD)
annotation class IndicatorStyle {
    companion object {
        const val CIRCLE = 0
        const val DASH = 1 shl 1
        const val ROUND_RECT = 1 shl 2
    }
}


@IntDef(
    IndicatorMode.NORMAL,
    IndicatorMode.SMOOTH,
    IndicatorMode.WORM,
    IndicatorMode.COLOR,
    IndicatorMode.SCALE
)
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD)
annotation class IndicatorMode {
    companion object {
        const val NORMAL = 0
        const val SMOOTH = 2
        const val WORM = 3
        const val SCALE = 4
        const val COLOR = 5
    }
}