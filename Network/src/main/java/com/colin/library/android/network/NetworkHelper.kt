package com.colin.library.android.network

import android.net.ParseException
import android.util.Log
import com.colin.library.android.network.data.ApiException
import com.colin.library.android.network.gson.IntegerTypeAdapter
import com.colin.library.android.network.gson.StringTypeAdapter
import com.colin.library.android.utils.NetUtil
import com.colin.library.android.utils.helper.UtilHelper
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
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
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025-04-25 23:49
 *
 * Des   :NetworkConfig
 */
object NetworkHelper {
    const val HTTP_EMPTY = -1
    const val HTTP_ERROR = -2
    const val HTTP_TIMEOUT = -3
    const val HTTP_UNAUTHORIZED = 401//"当前请求需要用户验证"
    const val HTTP_FORBIDDEN = 403//"当前请求需要用户验证"
    const val HTTP_NOT_FOUND = 404//"无法找到指定位置的资源"
    const val HTTP_REQUEST_TIMEOUT = 408//"请求超时"
    const val HTTP_INTERNAL_SERVER_ERROR = 500//"服务器错误"
    const val HTTP_BAD_GATEWAY = 502//"非法应答"
    const val HTTP_SERVICE_UNAVAILABLE = 503//"服务器未能应答"
    const val HTTP_GATEWAY_TIMEOUT = 504//"服务器未能应答"
    const val HTTP_UNKNOWN = 1000//"未知错误"
    const val HTTP_PARSE_ERROR = 1001//"解析错误"
    const val HTTP_NETWORK_ERROR = 1002//"网络异常，请尝试刷新"
    const val HTTP_SSL_ERROR = 1004//"证书出错"
    const val HTTP_UNKONW_HOST = 1005//"未知Host"

    private const val DELAY: Long = 0L
    private const val RETRY: Int = 3
    private const val TIMEOUT: Long = 10000L

    private val interceptors = mutableListOf<Interceptor>()

    private val networkInterceptors = mutableListOf<Interceptor>(createLoggingInterceptor())

    val retrofit: Retrofit by lazy {
        Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create(gson))
            .client(getOkHttpClient()).callbackExecutor(Executors.newSingleThreadExecutor()).build()
    }

    @Volatile
    var baseUrl: String = ""

    @Volatile
    var retry: Int = RETRY

    @Volatile
    var delay: Long = DELAY

    @Volatile
    var timeout: Long = TIMEOUT

    var gson: Gson =
        GsonBuilder().setLenient().registerTypeAdapter(Int::class.java, IntegerTypeAdapter())
            .registerTypeAdapter(String::class.java, StringTypeAdapter()).create()


    fun addInterceptor(interceptor: Interceptor) = apply {
        if (!interceptors.contains(interceptor)) interceptors.add(interceptor)
    }

    fun addNetworkInterceptors(interceptor: Interceptor) = apply {
        if (!networkInterceptors.contains(interceptor)) networkInterceptors.add(interceptor)
    }

    fun getOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder().callTimeout(timeout, TimeUnit.MILLISECONDS)
            .connectTimeout(timeout, TimeUnit.MILLISECONDS)
            .readTimeout(timeout, TimeUnit.MILLISECONDS)
            .writeTimeout(timeout, TimeUnit.MILLISECONDS).retryOnConnectionFailure(true)
        interceptors.forEach {
            builder.addInterceptor(it)
        }
        networkInterceptors.forEach {
            builder.addNetworkInterceptor(it)
        }
        return builder.build()
    }

    fun createLoggingInterceptor(tag: String = "okhttp") =
        HttpLoggingInterceptor { message ->
            Log.i(tag, message)
        }.also {
            it.level = if (UtilHelper.isDebug()) Level.BODY else Level.BASIC
        }

    fun createNetworkInterceptor() = object : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            if (NetUtil.isConnected()) {
                return chain.proceed(chain.request())
            } else {
                throw ApiException(HTTP_NETWORK_ERROR, "network error")
            }
        }
    }


    /**
     * 获取 apiService
     */
    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)
    inline fun <reified T> create(): T = create(T::class.java)


    internal suspend fun handleFailure(state: suspend (Int, String) -> Unit, e: Throwable) {
        when (e) {
            is HttpException -> state.invoke(e.code(), "$e")
            is ApiException -> state.invoke(e.code, e.msg)
            is ConnectException -> state.invoke(HTTP_NETWORK_ERROR, "$e")

            is javax.net.ssl.SSLException -> state.invoke(HTTP_SSL_ERROR, "$e")

            is java.net.SocketException, is java.net.SocketTimeoutException -> state.invoke(
                HTTP_TIMEOUT, "$e"
            )

            is java.net.UnknownHostException -> state.invoke(HTTP_UNKONW_HOST, "$e")

            is JsonParseException, is JSONException, is ParseException, is MalformedJsonException -> {
                state.invoke(HTTP_PARSE_ERROR, "$e")
            }

            else -> state.invoke(HTTP_UNKNOWN, "$e")

        }
    }
}
