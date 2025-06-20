package com.colin.android.demo.kotlin

import com.colin.library.android.network.data.AppResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025-04-28 16:41
 *
 * Des   :ApiService
 */
interface ApiService {
    //获取验证码
    @FormUrlEncoded
    @POST("/api/SmsCode/send")
    suspend fun login(@Field("phone") mobile: String): AppResponse<Boolean>

}