package com.colin.library.android.widget.base

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025-05-26 09:50
 *
 * Des   :ILoad 需要刷新加载：适用于网络请求，读取数据库，请求数据
 */
interface ILoad {
    /*load data by sqlite、http等耗时动作*/
    fun loadData(refresh: Boolean)
}