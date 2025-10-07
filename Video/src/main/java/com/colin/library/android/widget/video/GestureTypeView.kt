package com.colin.library.android.widget.video

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import com.colin.library.android.utils.Log
import com.colin.library.android.utils.ext.visible
import com.colin.library.android.widget.video.GestureType.Companion.TYPE_BRIGHTNESS
import com.colin.library.android.widget.video.GestureType.Companion.TYPE_PROGRESS
import com.colin.library.android.widget.video.GestureType.Companion.TYPE_VOLUME
import com.colin.library.android.widget.video.databinding.VideoGestureTypeBinding
import java.util.Formatter
import java.util.Locale

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025-06-15 22:17
 *
 * Des   :GestureTypeView
 */
@OptIn(UnstableApi::class)
class GestureTypeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val DURATION_ANIM = 100L
        private const val THRESHOLD = 30

    }

    private val binding =
        VideoGestureTypeBinding.bind(inflate(context, R.layout.video_gesture_type, this))

    private val formatBuilder = StringBuilder()
    private val formatter = Formatter(formatBuilder, Locale.getDefault())

    @GestureType
    private var currentType = GestureType.TYPE_NONE
    private var videoWidth: Int = 0
    private var videoHeight: Int = 0
    private var typeCache: Number = 0
    private var typeValue: Number = 0
    private var timeDuration = 0L
    var onProgressListener: ((Int, Number) -> Unit)? = null
    @GestureType
    fun getCurrentType() = currentType

    fun start(type: Int, width: Int, height: Int, value: Number, duration: Number = 0) {
        currentType = type
        videoWidth = width
        videoHeight = height
        typeCache = value
        typeValue = value
        if (type == TYPE_VOLUME || type == TYPE_BRIGHTNESS) {
            binding.videoGestureType.setImageResource(getImageRes(type))
            binding.videoGestureProgress.progress = getProgress()
            binding.videoGestureType.visible(true)
            binding.videoGestureTime.visible(false)
        } else if (type == TYPE_PROGRESS && duration.toLong() > 0) {
            timeDuration = duration.toLong()
            binding.videoGestureTime.text = getTime()
            binding.videoGestureProgress.progress = getProgress()
            binding.videoGestureType.visible(false)
            binding.videoGestureTime.visible(true)
        }
        showTypeView(this, true)
    }


    fun progress(distance: Float) {
        Log.i("currentType:$currentType width:$videoWidth height:$videoHeight distance:$distance")
        if (videoWidth <= 0 || videoHeight <= 0 || distance == 0F) return
        if (currentType == TYPE_VOLUME || currentType == TYPE_BRIGHTNESS) {
            typeValue = (typeCache.toFloat() + distance / videoHeight).coerceIn(0F, 1F)
            binding.videoGestureType.setImageResource(getImageRes(currentType))
            binding.videoGestureProgress.progress = getProgress()
            onProgressListener?.invoke(currentType, typeValue)
        } else if (currentType == TYPE_PROGRESS && timeDuration > 0) {
            val offset = distance / THRESHOLD / videoWidth
            typeValue =
                (typeCache.toLong() + offset * timeDuration).toLong().coerceIn(0, timeDuration)
            binding.videoGestureTime.text = getTime()
            binding.videoGestureProgress.progress = getProgress()
        }
    }

    fun finish(cancel: Boolean) {
        if (currentType == TYPE_PROGRESS && !cancel) {
            onProgressListener?.invoke(currentType, typeValue.toLong())
        }
        if (currentType == TYPE_VOLUME || currentType == TYPE_BRIGHTNESS) {
            onProgressListener?.invoke(currentType, if (cancel) typeCache else typeValue)
        }
        showTypeView(this, false)
        currentType = GestureType.TYPE_NONE
        typeCache = 0
        typeValue = 0
    }

    private fun getTime(): String {
        val progressTime = Util.getStringForTime(formatBuilder, formatter, typeValue.toLong())
        val durationTime = Util.getStringForTime(formatBuilder, formatter, timeDuration)
        return "$progressTime / $durationTime"
    }

    private fun showTypeView(view: View, visible: Boolean) {
        val alpha = if (visible) 0F else 1F
        view.animate().alpha(alpha).setDuration(DURATION_ANIM)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    if (visible) view.visibility = VISIBLE
                }

                override fun onAnimationEnd(animation: Animator) {
                    if (!visible) view.visibility = GONE
                }
            }).start()
    }

    private fun getProgress(): Int {
        if (currentType == TYPE_VOLUME || currentType == TYPE_BRIGHTNESS) {
            return (typeValue.toFloat() * 100).toInt()
        } else if (currentType == TYPE_PROGRESS && timeDuration > 0) {
            return (typeValue.toLong() * 100F / timeDuration).toInt()
        }
        return 0
    }

    private fun getImageRes(type: Int): Int {
        return if (type == TYPE_VOLUME) {
            if (typeValue.toFloat() > 0) R.drawable.ic_video_volume_max else R.drawable.ic_video_volume_min
        } else {
            if (typeValue.toFloat() > 0) R.drawable.ic_video_light_max else R.drawable.ic_video_light_min
        }
    }
}