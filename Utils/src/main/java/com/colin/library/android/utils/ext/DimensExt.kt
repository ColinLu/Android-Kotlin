package com.colin.library.android.utils.ext

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025-05-01 03:39
 *
 * Des   :尺寸单位转换扩展函数
 */
import android.util.TypedValue
import com.colin.library.android.utils.ResourcesUtil


/**
 * 将Float值转换为dp（设备独立像素）
 *
 * @return 转换后的像素值
 */
fun Float.dp() = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP, this, ResourcesUtil.getResources().displayMetrics
)


/**
 * 将Int值转换为dp（设备独立像素）
 *
 * @return 转换后的像素值（整数）
 */
fun Int.dp() = this.toFloat().dp().toInt()

/**
 * 将Float值转换为px（像素）
 *
 * @return 转换后的像素值
 */
fun Float.px() = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_PX, this, ResourcesUtil.getResources().displayMetrics
)

/**
 * 将Int值转换为px（像素）
 *
 * @return 转换后的像素值（整数）
 */
fun Int.px() = this.toFloat().px().toInt()

/**
 * 将Float值转换为sp（缩放独立像素）
 *
 * @return 转换后的像素值
 */
fun Float.sp() = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_SP, this, ResourcesUtil.getResources().displayMetrics
)


/**
 * 将Int值转换为sp（缩放独立像素）
 *
 * @return 转换后的像素值（整数）
 */
fun Int.sp() = this.toFloat().sp().toInt()
