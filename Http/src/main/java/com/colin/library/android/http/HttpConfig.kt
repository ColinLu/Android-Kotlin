//package com.colin.library.android.http
//
//import androidx.annotation.IntRange
//import okhttp3.OkHttpClient
//
///**
// * Author:ColinLu
// * E-mail:945919945@qq.com
// * Date  :2024-09-18
// * Des   :Http 网络请求配置
// */
//data class HttpConfig(val builder: Builder) {
//    val okHttpClient: OkHttpClient = builder.okHttpClient?:createOkhttpClient() //统一请求客户端
//    val readTimeout: Long = builder.readTimeout //读取时间
//    val writeTimeout: Long = builder.writeTimeout //写入时间
//    val connectTimeout: Long = builder.connectTimeout //连接时间
//    val userAgent: String = builder.userAgent
//    val acceptLanguage: String = builder.acceptLanguage
//
//
//    class Builder(baseUrl: String) {
//        var okHttpClient: OkHttpClient
//        var readTimeout: Long = 12
//        var writeTimeout: Long = 12
//        var connectTimeout: Long = 12
//        var acceptLanguage: String = Util.getAcceptLanguage()
//        var userAgent: String = Util.getUserAgent()
//
//
//        fun setOkHttpClient(okHttpClient: OkHttpClient) = apply {
//            this.okHttpClient = okHttpClient
//        }
//
//        fun setReadTimeout(@IntRange(from = 0, to = 60) timeout: Long) = apply {
//            this.readTimeout = timeout
//        }
//
//        fun setWriteTimeout(@IntRange(from = 0, to = 60) timeout: Long) = apply {
//            this.writeTimeout = timeout
//        }
//
//        fun setConnectTimeout(@IntRange(from = 0, to = 60) timeout: Long) = apply {
//            this.connectTimeout = timeout
//        }
//
//        fun setAcceptLanguage(acceptLanguage: String) = apply {
//            this.acceptLanguage = acceptLanguage
//        }
//
//        fun setUserAgent(userAgent: String) = apply {
//            this.userAgent = userAgent
//        }
//
//        fun build(): HttpConfig {
//            return HttpConfig(this)
//        }
//    }
//
//    companion object {
//        fun newBuilder(baseUrl: String): Builder {
//            return Builder(baseUrl)
//        }
//    }
//}