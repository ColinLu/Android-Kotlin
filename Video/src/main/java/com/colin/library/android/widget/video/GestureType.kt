package com.colin.library.android.widget.video

import androidx.annotation.IntDef


@IntDef(
    GestureType.TYPE_NONE,
    GestureType.TYPE_VOLUME,
    GestureType.TYPE_BRIGHTNESS,
    GestureType.TYPE_PROGRESS
)
@Retention(AnnotationRetention.SOURCE)
annotation class GestureType() {
    companion object {
        const val TYPE_NONE = 0
        const val TYPE_VOLUME = 1
        const val TYPE_BRIGHTNESS = 2
        const val TYPE_PROGRESS = 3
    }
}
