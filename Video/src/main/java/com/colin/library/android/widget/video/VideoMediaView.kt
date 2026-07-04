package com.colin.library.android.widget.video

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.annotation.OptIn
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import com.colin.library.android.utils.Log
import com.colin.library.android.widget.video.GestureType.Companion.TYPE_BRIGHTNESS
import com.colin.library.android.widget.video.GestureType.Companion.TYPE_NONE
import com.colin.library.android.widget.video.GestureType.Companion.TYPE_PROGRESS
import com.colin.library.android.widget.video.GestureType.Companion.TYPE_VOLUME
import kotlinx.coroutines.Runnable
import kotlin.math.abs

/**
 * 增强版视频播放控件，整合MediaManager并提供完善的控制功能
 * 1.解密过程缓慢，目前使用站位图
 */
@OptIn(UnstableApi::class)
class VideoMediaView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : PlayerView(context, attrs, defStyleAttr), Player.Listener {
    companion object Companion {
        const val ONE_SECOND = 1_000L
        const val DELAY_TIME = 50L
        const val TOUCH_RANGE = 50F
    }

    private val gestureDetector by lazy {
        GestureDetector(context, gestureListener).apply { setIsLongpressEnabled(false) }
    }
    private val gestureTypeView by lazy {
        findViewById<GestureTypeView>(R.id.videoGestureType)?.apply {
            onProgressListener = ::updateGestureTypeValue
        }
    }

    //音频管理器
    private val audioManager by lazy {
        ContextCompat.getSystemService(context, AudioManager::class.java)
    }

    //窗口,控制大小和透明度
    private val window = if (context is Activity) context.window else null
    private var discardTouchEvent = false
    var onProgressListener: ((Long) -> Unit)? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        onRelease()
        super.onDetachedFromWindow()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> if (maybeDiscardTouchEvent(event)) return true
            MotionEvent.ACTION_MOVE -> if (discardTouchEvent) return true
            MotionEvent.ACTION_UP -> gestureListener.finish(false)
            MotionEvent.ACTION_CANCEL -> gestureListener.finish(true)
        }
        gestureDetector.onTouchEvent(event)
        return true
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return discardTouchEvent || super.onInterceptTouchEvent(ev)
    }

    override fun onResume() {
        super.onResume()
        player?.let {
            // 重新准备
            it.prepare()
            // 恢复播放
            it.playWhenReady = true
        }
    }

    override fun onPause() {
        super.onPause()
        player?.let {
            it.playWhenReady = false
            it.stop()
        }
    }

    fun onRelease() {
        removeCallbacks(progressRunnable)
        removeCallbacks(updateGestureTypeValueRunnable(TYPE_NONE, 0))
        player?.let {
            it.removeListener(this)
            it.release()
        }
        player = null
    }

    fun setFullScreen(isFullscreen: Boolean) {
        setFullscreenButtonState(isFullscreen)

    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        if (isPlaying) {
            postDelayed(progressRunnable, ONE_SECOND)
        } else {
            removeCallbacks(progressRunnable)
        }
    }

    fun bind(player: Player) {
        this.player = player.also { it.addListener(this) }
    }

    fun isPlaying() = player?.isPlaying == true


    fun setVolume(volume: Float) {
        player?.volume = volume
        //设置系统音量
        audioManager?.let {
            val max = it.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            val music = (volume * max).toInt().coerceIn(0, max)
            it.setStreamVolume(AudioManager.STREAM_MUSIC, music, AudioManager.FLAG_PLAY_SOUND)
        }
    }

    /**
     * 跳转到指定位置
     */
    fun seekTo(positionMs: Long) {
        player?.seekTo(positionMs)?.also { Log.d("seekTo position: ${positionMs}ms") }
    }

    fun togglePlayController() {
        if (isControllerFullyVisible) hideController()
        else showController()
    }

    fun togglePlay() {
        player?.let { it.playWhenReady = !it.playWhenReady }
    }

    fun play(play: Boolean = true) {
        player?.let { it.playWhenReady = play }
    }

    fun play(item: MediaItem, play: Boolean = true) {
        player?.apply {
            setMediaItem(item)
            playWhenReady = play
            if (play) this.prepare()
        }
    }

    private val gestureListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onDoubleTap(e: MotionEvent): Boolean {
            togglePlay()
            return true
        }

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            togglePlayController()
            return true
        }

        override fun onScroll(
            e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float
        ): Boolean {
            if (e1 == null || gestureTypeView == null) return true
            gestureType(gestureTypeView!!, e1, e2)
            return true
        }

        fun finish(cancel: Boolean): Boolean {
            discardTouchEvent = false
            gestureTypeView?.finish(cancel)
            return true
        }

        private fun gestureType(view: GestureTypeView, e1: MotionEvent, e2: MotionEvent) {
            val duration = player?.duration ?: return
            val deltaX = e2.x - e1.x // X轴方向
            val deltaY = e1.y - e2.y // 反转Y轴方向
            val type = view.getCurrentType()
            Log.i("currentType:$type deltaX:$deltaX deltaY:$deltaY")
            when (type) {
                TYPE_NONE -> {
                    if (abs(deltaX) > abs(deltaY)) {
                        val current = player?.currentPosition ?: 0
                        view.start(TYPE_PROGRESS, width, height, current, duration)
                        return
                    }
                    if (e1.x > width / 2) {
                        view.start(TYPE_VOLUME, width, height, player?.volume ?: 0F)
                        return
                    }
                    if (e1.x < width / 2) {
                        view.start(TYPE_BRIGHTNESS, width, height, videoSurfaceView?.alpha ?: 0F)
                        return
                    }
                }

                TYPE_VOLUME -> {
                    view.progress(deltaY)
                    return
                }

                TYPE_BRIGHTNESS -> {
                    view.progress(deltaY)
                    return
                }

                TYPE_PROGRESS -> {
                    view.progress(deltaX)
                    return
                }
            }
        }
    }

    /**
     * 播放进度更新
     */
    private val progressRunnable = object : Runnable {
        override fun run() {
            removeCallbacks(this)
            if (isPlaying()) {
                val position = player?.currentPosition ?: 0L
                onProgressListener?.invoke(position)
                postDelayed(this, ONE_SECOND)
            }
        }
    }


    private fun updateGestureTypeValue(@GestureType type: Int, number: Number) {
        removeCallbacks(updateGestureTypeValueRunnable(type, number))
        postDelayed(updateGestureTypeValueRunnable(type, number), DELAY_TIME)
    }

    private fun updateGestureTypeValueRunnable(@GestureType type: Int, number: Number) = Runnable {
        Log.i("progress:type:$type number:$number")
        if (type == TYPE_VOLUME) {
            setVolume(number.toFloat())
        } else if (type == TYPE_BRIGHTNESS) {
            val alpha = number.toFloat()
            videoSurfaceView?.alpha = alpha
            window?.let { it.attributes.buttonBrightness = alpha }
        } else if (type == TYPE_PROGRESS) {
            player?.seekTo(number.toLong())
        }
    }

    private fun maybeDiscardTouchEvent(event: MotionEvent): Boolean {
        discardTouchEvent =
            gestureTypeView != null && (event.x < TOUCH_RANGE || event.x > width - TOUCH_RANGE || event.y < TOUCH_RANGE || event.y > height - TOUCH_RANGE)
        return discardTouchEvent
    }
}


