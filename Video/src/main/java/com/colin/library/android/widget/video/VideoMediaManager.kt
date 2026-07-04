package com.colin.library.android.widget.video

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.LoadControl
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.mediacodec.MediaCodecSelector
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.trackselection.TrackSelector
import androidx.media3.exoplayer.upstream.DefaultLoadErrorHandlingPolicy
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.colin.library.android.utils.Log
import com.colin.library.android.utils.helper.UtilHelper
import com.colin.library.android.widget.video.datasource.CustomVideoDataSource
import com.colin.library.android.widget.video.service.VideoMediaService
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import java.io.File

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025/10/7 10:45
 *
 * Des   :管理视频多媒体构建+释放等操作
 */
@UnstableApi
object VideoMediaManager : Player.Listener {
    // 缓冲设置（单位：毫秒）
    private const val MIN_BUFFER_MS = 15_000 // 降低最小缓冲（首次加载更快）
    private const val MAX_BUFFER_MS = 30_000 // 降低最大缓冲（减少内存占用）
    private const val PLAYBACK_BUFFER_MS = 3_000 // 播放缓冲
    private const val REBUFFER_BUFFER_MS = 5_000 // 重新缓冲

    // 缓存最大容量：200MB
    private const val MAX_CACHE_SIZE = 1L * 1024 * 1024 * 1024

    // ExoPlayer 实例（单例）
    private var exoPlayer: ExoPlayer? = null
    private var controllerFuture: ListenableFuture<MediaController>? = null


    /**
     * 管理本地媒体缓存目录，使用 LRU 算法清除缓存
     * 路径为应用缓存目录下的 media_cache 文件夹
     */
    val mediaCache by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        SimpleCache(
            getCacheFile().apply {
                if (!exists()) mkdirs()
            }, LeastRecentlyUsedCacheEvictor(MAX_CACHE_SIZE), null, null, false, false
        )
    }


    /**
     * 获取或创建 ExoPlayer 实例,构建的时候不能prepare()
     * @param context 上下文：考虑内存泄漏，所以应该避免使用Activity/Fragment上下文
     * @return ExoPlayer 实例，建议用后台MediaService初始化
     */
    fun getMediaPlayer(context: Context): ExoPlayer {
        return exoPlayer ?: createMediaPlayer(context.applicationContext).also {
            it.repeatMode = Player.REPEAT_MODE_OFF
            it.playWhenReady = false
            exoPlayer = it
        }
    }

    // 连接后台MediaSessionService
    fun connectSessionToken(context: Context) {
        val componentName = ComponentName(context, VideoMediaService::class.java)
        val token = SessionToken(context, componentName)
        val future = MediaController.Builder(context, token)
            .buildAsync().also { controllerFuture = it }
        future.addListener(
            { future.get().also { it.addListener(this) } },
            MoreExecutors.directExecutor()
        )
    }


    /**
     * 创建并配置 MediaPlayer 实例
     * 包括 TrackSelector、LoadControl、RenderersFactory、MediaSourceFactory
    //     */
    fun createMediaPlayer(context: Context): ExoPlayer {
        return ExoPlayer.Builder(context)
            .setWakeMode(C.WAKE_MODE_NETWORK)
            .setTrackSelector(createTrack(context))
            .setLoadControl(createLoadControl())
            .setRenderersFactory(
                DefaultRenderersFactory(context).apply {
                    setExtensionRendererMode(EXTENSION_RENDERER_MODE_ON)
                    setMediaCodecSelector(MediaCodecSelector.PREFER_SOFTWARE)
                    setEnableAudioFloatOutput(true)
                }).setMediaSourceFactory(createMediaSourceFactory(context))
            .setUsePlatformDiagnostics(true) // 启用原生日志
            .build()
    }


    /**
     * 释放资源，通常在应用退出或不再需要播放器时调用
     */
    fun release() {
        controllerFuture?.get()?.release()
        exoPlayer?.release()
        mediaCache.release()
    }

    /**
     * 创建轨道选择器，默认选择 SD 分辨率视频轨道
     */
    fun createTrack(context: Context): TrackSelector {
        return DefaultTrackSelector(context).apply {
            parameters = buildUponParameters().setMaxVideoSizeSd().build() // 默认选择SD画质
        }
    }

    /**
     * 创建加载控制器，控制缓冲行为
     */
    fun createLoadControl(): LoadControl {
        return DefaultLoadControl.Builder().setBufferDurationsMs(
            MIN_BUFFER_MS, MAX_BUFFER_MS, PLAYBACK_BUFFER_MS, REBUFFER_BUFFER_MS
        ).setPrioritizeTimeOverSizeThresholds(true).setBackBuffer(REBUFFER_BUFFER_MS, true).build()
    }

    fun createMediaSourceFactory(context: Context): MediaSource.Factory {
        return DefaultMediaSourceFactory(createDataSourceFactory(context)).apply {
            setLoadErrorHandlingPolicy(DefaultLoadErrorHandlingPolicy(3))
        }
    }

    fun createHlsMediaSourceFactory(context: Context): MediaSource.Factory {
        return HlsMediaSource.Factory(createCacheDataSourceFactory(context)) // 使用缓存数据源
            .setAllowChunklessPreparation(true)  // 必需：解决首次元数据加载
            .setUseSessionKeys(true)            // 必需：支持加密流
            .setLoadErrorHandlingPolicy(DefaultLoadErrorHandlingPolicy(3))
    }

    /**
     * 播放器支持缓存
     */
    fun createCacheDataSourceFactory(context: Context): DataSource.Factory {
        return CacheDataSource.Factory().apply {
            setCache(mediaCache)
            setUpstreamDataSourceFactory(createDataSourceFactory(context))
            setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
            setCacheKeyFactory { dataSpec ->
                // 修复：使用稳定的 URI 作为缓存 Key，避免使用时间戳导致缓存失效
                dataSpec.key ?: dataSpec.uri.toString()
            }
        }
    }

    /**
     * 播放器不带缓存
     */
    fun createDataSourceFactory(context: Context): DataSource.Factory {
        return CustomVideoDataSource.Factory(context)
//        return OkHttpDataSource.Factory(
//            OkHttpClient.Builder()
////                .addNetworkInterceptor(NetworkHelper.createLoggingInterceptor("media_http"))
////                .addNetworkInterceptor(VideoAesInterceptor())
//                .build()
//        )
    }


    private fun getCacheFile(context: Context = UtilHelper.getApplication()): File {
        val file =
            context.getExternalFilesDir("media_cache") ?: File(context.cacheDir, "media_cache")
        Log.e("getCacheFile:${file.path}")
        return file
    }

}