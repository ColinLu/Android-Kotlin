package com.colin.library.android.network

import android.net.ParseException
import com.colin.library.android.network.data.ApiException
import com.colin.library.android.network.gson.IntegerTypeAdapter
import com.colin.library.android.network.gson.StringTypeAdapter
import com.colin.library.android.utils.Log
import com.colin.library.android.utils.NetUtil
import com.colin.library.android.utils.helper.UtilHelper
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import com.google.gson.Strictness
import com.google.gson.stream.MalformedJsonException
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import org.json.JSONException
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLException

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025-04-25 23:49
 *
 * Des   :NetworkHelper - 网络配置与管理
 */
object NetworkHelper {
    const val HTTP_ERROR = -2
    const val HTTP_TIMEOUT = -3
    const val HTTP_NETWORK_ERROR = 1002
    const val HTTP_PARSE_ERROR = 1001
    const val HTTP_SSL_ERROR = 1004
    const val HTTP_HOST_UNKNOWN = 1005
    const val HTTP_UNKNOWN = 1000

    private const val DEFAULT_RETRY = 3
    private const val DEFAULT_TIMEOUT = 10000L

    private val interceptors = mutableListOf<Interceptor>()
    private val networkInterceptors = mutableListOf<Interceptor>()

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(getOkHttpClient())
            .build()
    }

    @Volatile var baseUrl: String = ""
    @Volatile var retry: Int = DEFAULT_RETRY
    @Volatile var delay: Long = 0L
    @Volatile var timeout: Long = DEFAULT_TIMEOUT

    var gson: Gson = GsonBuilder()
        .setStrictness(Strictness.LENIENT)
        .registerTypeAdapter(Int::class.java, IntegerTypeAdapter())
        .registerTypeAdapter(String::class.java, StringTypeAdapter())
        .create()

    fun addInterceptor(interceptor: Interceptor) = apply {
        if (!interceptors.contains(interceptor)) interceptors.add(interceptor)
    }

    fun addNetworkInterceptor(interceptor: Interceptor) = apply {
        if (!networkInterceptors.contains(interceptor)) networkInterceptors.add(interceptor)
    }

    fun getOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .callTimeout(timeout, TimeUnit.MILLISECONDS)
            .connectTimeout(timeout, TimeUnit.MILLISECONDS)
            .readTimeout(timeout, TimeUnit.MILLISECONDS)
            .writeTimeout(timeout, TimeUnit.MILLISECONDS)
            .retryOnConnectionFailure(true)
        
        interceptors.forEach { builder.addInterceptor(it) }
        networkInterceptors.forEach { builder.addNetworkInterceptor(it) }
        
        if (UtilHelper.isDebug()) {
            builder.addInterceptor(HttpLoggingInterceptor { message ->
                Log.d("OkHttp", message)
            }.apply { level = Level.BODY })
        }
        
        return builder.build()
    }

    /**
     * 创建 API 接口实例
     */
    inline fun <reified T> create(): T = retrofit.create(T::class.java)

    internal suspend fun handleFailure(state: suspend (Int, String) -> Unit, e: Throwable) {
        val (code, message) = when (e) {
            is HttpException -> e.code() to (e.message ?: "HTTP Error")
            is ApiException -> e.code to e.msg
            is ConnectException -> HTTP_NETWORK_ERROR to "Network connection failed"
            is SSLException -> HTTP_SSL_ERROR to "SSL certificate error"
            is SocketException, is SocketTimeoutException -> HTTP_TIMEOUT to "Request timed out"
            is UnknownHostException -> HTTP_HOST_UNKNOWN to "Unknown host"
            is JsonParseException, is JSONException, is ParseException, is MalformedJsonException -> 
                HTTP_PARSE_ERROR to "Data parsing error"
            else -> HTTP_UNKNOWN to (e.message ?: "Unknown error")
        }
        state.invoke(code, message)
    }
}
