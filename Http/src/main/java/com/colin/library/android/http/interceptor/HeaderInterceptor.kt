//package com.colin.library.android.http.interceptor
//
//import android.util.Log
//import okhttp3.Interceptor
//import okhttp3.Response
//
///**
// * Author:ColinLu
// * E-mail:945919945@qq.com
// * Date  :2024-09-13
// * Des   :头信息拦截器:添加头信息
// */
//class HeaderInterceptor: Interceptor {
//    override fun intercept(chain: Interceptor.Chain): Response {
//        val request = chain.request()
//        val newBuilder = request.newBuilder()
//        newBuilder.addHeader("Content-type", "application/json; charset=utf-8")
//        val host = request.url.host
//        val url = request.url.toString()
//
//        //给有需要的接口添加Cookies
//        if (!host.isNullOrEmpty() && (url.contains(COLLECTION_WEBSITE) || url.contains(
//                NOT_COLLECTION_WEBSITE
//            ) || url.contains(ARTICLE_WEBSITE) || url.contains(COIN_WEBSITE))
//        ) {
//            val cookies = CookiesManager.getCookies()
//            Log.e("http->HeaderInterceptor:", "cookies:$cookies")
//            if (!cookies.isNullOrEmpty()) {
//                newBuilder.addHeader(KEY_COOKIE, cookies)
//            }
//        }
//        return chain.proceed(newBuilder.build())
//    }
//}