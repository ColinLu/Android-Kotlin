package com.colin.library.android.widget.video

import android.content.Context
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.datasource.okhttp.OkHttpDataSource
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
import com.colin.library.android.utils.Log
import com.colin.library.android.utils.helper.UtilHelper
import okhttp3.OkHttpClient
import java.io.File

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025/10/7 10:45
 *
 * Des   :管理视频多媒体构建+释放等操作
 */
@UnstableApi
object VideoMediaManager {
    // 缓冲设置（单位：毫秒）
    private const val MIN_BUFFER_MS = 15000 // 降低最小缓冲（首次加载更快）
    private const val MAX_BUFFER_MS = 30000 // 降低最大缓冲（减少内存占用）
    private const val PLAYBACK_BUFFER_MS = 3000 // 播放缓冲
    private const val REBUFFER_BUFFER_MS = 5000 // 重新缓冲

    // 缓存最大容量：200MB
    private const val MAX_CACHE_SIZE = 200 * 1024 * 1024L

    // ExoPlayer 实例（单例）
    private var exoPlayer: ExoPlayer? = null


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
     * 构建音频播放器
     */
    fun getAudioExoPlayer(context: Context): ExoPlayer {
        return ExoPlayer.Builder(context).build()
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


    /**
     * 创建并配置 MediaPlayer 实例
     * 包括 TrackSelector、LoadControl、RenderersFactory、MediaSourceFactory
     */
    fun createMediaPlayer(context: Context): ExoPlayer {
        return ExoPlayer.Builder(context)
            .setTrackSelector(createTrack(context))
            .setLoadControl(createLoadControl()).setRenderersFactory(
                DefaultRenderersFactory(context).apply {
                    setExtensionRendererMode(EXTENSION_RENDERER_MODE_ON)
                    setMediaCodecSelector(MediaCodecSelector.PREFER_SOFTWARE)
                    setEnableAudioFloatOutput(true)
                }).setMediaSourceFactory(createMediaSourceFactory())
            .setUsePlatformDiagnostics(true) // 启用原生日志
            .build()
    }

    /**
     * 释放资源，通常在应用退出或不再需要播放器时调用
     */
    fun release() {
        exoPlayer?.release()
        mediaCache.release()
    }

    /**
     * 创建轨道选择器，默认选择 SD 分辨率视频轨道
     */
    private fun createTrack(context: Context): TrackSelector {
        return DefaultTrackSelector(context).apply {
            parameters = buildUponParameters().setMaxVideoSizeSd().build() // 默认选择SD画质
        }
    }

    /**
     * 创建加载控制器，控制缓冲行为
     */
    private fun createLoadControl(): LoadControl {
        return DefaultLoadControl.Builder().setBufferDurationsMs(
            MIN_BUFFER_MS, MAX_BUFFER_MS, PLAYBACK_BUFFER_MS, REBUFFER_BUFFER_MS
        ).setPrioritizeTimeOverSizeThresholds(true).setBackBuffer(REBUFFER_BUFFER_MS, true).build()
    }

    private fun createMediaSourceFactory(): MediaSource.Factory {
        return DefaultMediaSourceFactory(createHttpDataSourceFactory()).apply {
            setLoadErrorHandlingPolicy(DefaultLoadErrorHandlingPolicy(3))
        }
    }

    private fun createHlsMediaSourceFactory(): MediaSource.Factory {
        return HlsMediaSource.Factory(createCacheDataSourceFactory()) // 使用缓存数据源
            .setAllowChunklessPreparation(true)  // 必需：解决首次元数据加载
            .setUseSessionKeys(true)            // 必需：支持加密流
            .setLoadErrorHandlingPolicy(DefaultLoadErrorHandlingPolicy(3))
    }

    /**
     * 播放器支持缓存
     */
    fun createCacheDataSourceFactory(): DataSource.Factory {
        return CacheDataSource.Factory().apply {
            setCache(mediaCache)
            setUpstreamDataSourceFactory(createHttpDataSourceFactory())
            setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
            setCacheKeyFactory { dataSpec ->
                // 改进：使用URI+时间戳避免缓存冲突
                "${dataSpec.uri}_${System.currentTimeMillis()}"
            }
        }
    }

    /**
     * 播放器不带缓存
     */
    fun createHttpDataSourceFactory(): DataSource.Factory {
        return OkHttpDataSource.Factory(
            OkHttpClient.Builder()
//                .addNetworkInterceptor(NetworkHelper.createLoggingInterceptor("media_http"))
//                .addNetworkInterceptor(VideoAesInterceptor())
                .build()
        )
    }


    private fun getCacheFile(context: Context = UtilHelper.getApplication()): File {
        val file =
            context.getExternalFilesDir("media_cache") ?: File(context.cacheDir, "media_cache")
        Log.e("getCacheFile:${file.path}")
        return file
    }

}