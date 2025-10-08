package com.colin.library.android.widget.video.service

import android.content.Intent
import androidx.annotation.OptIn
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.colin.library.android.utils.Log
import com.colin.library.android.widget.video.VideoMediaManager

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025/10/7 10:46
 *
 * Des   :用户后台播放，且一直播放
 *
 *     private fun initPlayer(context: Context) {
 *         val token = SessionToken(context, ComponentName(context, VideoMediaService::class.java))
 *         val future =
 *             MediaController.Builder(context, token).buildAsync().also { controllerFuture = it }
 *         future.addListener(Runnable {
 *             this.player = future.get().also { it.addListener(this) }
 *         }, MoreExecutors.directExecutor())
 *     }
 */
@OptIn(UnstableApi::class)
class VideoMediaService : MediaSessionService(), Player.Listener {
    private var mediaSession: MediaSession? = null


    override fun onCreate() {
        super.onCreate()
        Log.e("VideoMediaService onCreate")
        VideoMediaManager.getMediaPlayer(this).also {
            it.addListener(this)
            // 基于已创建的ExoPlayer创建MediaSession
            mediaSession = MediaSession.Builder(this, it).setCallback(mediaSessionCallback).build()
            Log.d("VideoMediaService player init")
        }
    }


    override fun onDestroy() {
        Log.e("VideoMediaService onDestroy")
        VideoMediaManager.getMediaPlayer(this).let {
            it.removeListener(this)
            it.stop()
        }
        VideoMediaManager.release()
        mediaSession?.release()
        mediaSession = null
        super.onDestroy()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo) = mediaSession


    // region Player.Listener回调
    override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
            Player.STATE_IDLE -> Log.d("VideoMediaService:Idle/Not init")
            Player.STATE_BUFFERING -> Log.d("VideoMediaService: Buffering")
            Player.STATE_READY -> Log.d("VideoMediaService: Ready(first frame is available)")
            Player.STATE_ENDED -> Log.d("VideoMediaService: Play finish")
        }
    }

    // 后台任务移除时的智能处理
    override fun onTaskRemoved(rootIntent: Intent?) {
        if (mediaSession?.player?.isPlaying == true) {
            // 保持服务运行但移除前台状态
            stopForeground(STOP_FOREGROUND_DETACH)
        } else {
            stopSelf()
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        Log.d("VideoMediaService onIsPlayingChanged: ${if (isPlaying) "start playing" else "Pause/Stop"}")
    }

    override fun onVideoSizeChanged(videoSize: VideoSize) {
        Log.d("onVideoSizeChanged: ${videoSize.width}*${videoSize.height}")
        if (videoSize.width > 0 && videoSize.height > 0) {
            Log.d("VideoMediaService onVideoSizeChanged: first frame display finish")
        }
    }

    override fun onTracksChanged(tracks: Tracks) {
        tracks.groups.forEach { group ->
            Log.d("VideoMediaService Type: ${group.type}, Format: ${group.getTrackFormat(0).sampleMimeType}")
        }
    }

    override fun onPlayerError(error: PlaybackException) {
        Log.e("VideoMediaService Error: ${error.errorCodeName}")
    }

    private val mediaSessionCallback = object : MediaSession.Callback {
        override fun onDisconnected(
            session: MediaSession, controller: MediaSession.ControllerInfo
        ) {
            if (session.connectedControllers.isEmpty()) {
                Log.e("VideoMediaService stopSelf")
                stopSelf() // 无连接客户端时自动停止服务
            }
        }
    }
}