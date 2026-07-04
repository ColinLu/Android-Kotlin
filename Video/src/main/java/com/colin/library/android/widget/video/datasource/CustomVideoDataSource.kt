package com.colin.library.android.widget.video.datasource

import android.content.Context
import android.net.Uri
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.datasource.TransferListener
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.okhttp.OkHttpDataSource
import com.colin.library.android.utils.Log
import com.colin.library.android.widget.video.VideoMediaManager
import okhttp3.OkHttpClient
import java.io.IOException

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025/10/7 10:51
 *
 * Des   : 视频数据源适配器，兼容 raw、asset、网络资源（HTTP/HTTPS）及缓存读取。支持播放多种媒体格式：mp3, mp4, m3u8 等。
 */
@UnstableApi
class CustomVideoDataSource(private val context: Context) : DataSource {
    private val networkDataSource: DataSource by lazy { createNetworkDataSource() }
    private val rawDataSource: DataSource by lazy { RawResourceDataSource(context) }
    private val assetDataSource: DataSource by lazy {
        DefaultDataSource.Factory(context).createDataSource()
    }
    private val listeners = mutableListOf<TransferListener>()
    private var currentDataSource: DataSource? = null

    override fun addTransferListener(listener: TransferListener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
            currentDataSource?.addTransferListener(listener)
        }
    }

    override fun getUri(): Uri? = currentDataSource?.uri

    /**
     * 打开指定 DataSpec 的数据源
     */
    override fun open(spec: DataSpec): Long {
        return when (resolveDataSourceType(spec.uri)) {
            is DataSourceType.Raw -> {
                Log.d("Opening raw resource data source")
                setupDataSource(rawDataSource, spec)
            }

            is DataSourceType.Asset -> {
                Log.d("Opening asset data source")
                setupDataSource(assetDataSource, spec)
            }

            is DataSourceType.Network -> {
                Log.d("Opening network data source")
                setupDataSource(networkDataSource, spec)
            }
        }
    }

    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        return try {
            currentDataSource?.read(buffer, offset, length)
                ?: throw IOException("DataSource not initialized or opened")
        } catch (e: Exception) {
            Log.e("Error reading from data source $e")
            throw e
        }
    }

    override fun close() {
        Log.d("Closing current data source")
        listeners.clear()
        currentDataSource?.close()
        currentDataSource = null
    }


    private fun setupDataSource(dataSource: DataSource, spec: DataSpec): Long {
        currentDataSource?.close()
        currentDataSource = dataSource.apply {
            listeners.forEach { addTransferListener(it) }
        }
        return dataSource.open(spec)
    }

    /**
     * 判断 URI 类型并返回对应的数据源类型
     */
    private fun resolveDataSourceType(uri: Uri): DataSourceType {
        return when {
            isRawResource(uri) -> DataSourceType.Raw
            uri.scheme == "asset" -> DataSourceType.Asset
            else -> DataSourceType.Network
        }
    }

    private fun isRawResource(uri: Uri): Boolean {
        return uri.scheme == "android.resource" && uri.host == context.packageName && uri.pathSegments.firstOrNull()
            ?.toIntOrNull() != null
    }

    /**
     * 创建网络数据源（包含缓存）
     */
    private fun createNetworkDataSource(): DataSource {
        return CacheDataSource.Factory().apply {
            setCache(VideoMediaManager.mediaCache)
            setUpstreamDataSourceFactory(createHttpDataSourceFactory())
            setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
            // 修复：使用稳定的 URI 作为缓存 Key，避免使用时间戳导致缓存失效
            setCacheKeyFactory { dataSpec -> dataSpec.key ?: dataSpec.uri.toString() }
        }.createDataSource()
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


//        return OkHttpDataSource.Factory(okHttpClient)
//            .setUserAgent(Util.getUserAgent(context, context.packageName))
    }

    /**
     * 数据源类型密封类，用于统一判断逻辑
     */
    sealed class DataSourceType {
        object Raw : DataSourceType()
        object Asset : DataSourceType()
        object Network : DataSourceType()
    }

    class Factory(private val context: Context) : DataSource.Factory {
        override fun createDataSource(): DataSource {
            return CustomVideoDataSource(context)
        }
    }
}