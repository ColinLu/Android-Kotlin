package com.colin.library.android.web

import android.annotation.SuppressLint
import android.app.Application
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings.LOAD_DEFAULT
import android.webkit.WebSettings.LayoutAlgorithm.SINGLE_COLUMN
import android.webkit.WebSettings.PluginState.ON_DEMAND
import com.colin.library.android.utils.PathUtil
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.QbSdk.PreInitCallback
import com.tencent.smtt.sdk.TbsListener
import com.tencent.smtt.sdk.WebSettings

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025-07-01 13:34
 *
 * Des   :WebUtil
 */
object WebUtil {
    const val TEXT_ENCODE = "UTF-8"
    const val TEXT_HTML = "text/html"
    const val WEB_PATH = "app_web"
    fun init(application: Application, listener: TbsListener?, callback: PreInitCallback?) {
        // 用TBS的 ”dex2oat优化方案“
        val map = HashMap<String, Any>()
        map[TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER] = true
        map[TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE] = true
        QbSdk.initTbsSettings(map)
        QbSdk.setDownloadWithoutWifi(true)
        QbSdk.setCoreMinVersion(QbSdk.CORE_VER_ENABLE_202207)
        listener?.let { QbSdk.setTbsListener(it) }
        callback?.let { QbSdk.initX5Environment(application, it) }
    }

    @Suppress("DEPRECATION")
    @SuppressLint("SetJavaScriptEnabled")
    fun updateSetting(
        view: android.webkit.WebView, javaScriptEnabled: Boolean = false
    ): android.webkit.WebSettings {
        return view.settings.apply {
            // 设置WebView是否允许执行JavaScript脚本，默认false，不允许。
            this.javaScriptEnabled = javaScriptEnabled
            //支持通过JS打开新窗口
            this.javaScriptCanOpenWindowsAutomatically = true
            //设置编码格式
            this.defaultTextEncodingName = TEXT_ENCODE
            //设置字体大小
            this.defaultFontSize = 16
            //设置 WebView 支持的最小字体大小，默认为 8
            this.minimumFontSize = 10

            //设置自适应屏幕，两者合用
            this.useWideViewPort = true //将图片调整到适合webview的大小
            this.loadWithOverviewMode = true // 缩放至屏幕的大小

            // 缩放操作
            this.setSupportZoom(true)//支持缩放，默认为true。是下面那个的前提。
            this.builtInZoomControls = true  //设置内置的缩放控件。若为false，则该WebView不可缩放
            this.displayZoomControls = false //隐藏原生的缩放控件

            // 设置页面文本的尺寸,百分比，默认NORMAL
            this.textZoom = 100
            //支持自动加载图片
            this.loadsImagesAutomatically = true
            //设置可以访问文件
            this.allowFileAccess = true
            //设置缓存模式
            this.cacheMode = LOAD_DEFAULT
            //是否开启DOM缓存
            this.domStorageEnabled = true
            // 数据库存储API是否可用
            this.databaseEnabled = true
            // 缓存路径
            this.databasePath = PathUtil.getInternalPath(view.context, WEB_PATH)
            // WebView是否下载图片资源，默认为true
            this.loadsImagesAutomatically = true
            // 设置WebView是否支持多窗口
            this.setSupportMultipleWindows(false)
            //是否阻塞加载网络图片  协议http or https
            this.blockNetworkImage = false
            //允许加载本地文件html  file协议
            this.allowFileAccess = true
            //将图片调整到适合webView的大小 useWideViewPort
            this.useWideViewPort = true
            // 是否允许WebView度超出以概览的方式载入页面
            this.loadWithOverviewMode = true
            this.setNeedInitialFocus(true)
            // 定位是否可用
            this.setGeolocationEnabled(true)
            //通过 file url 加载的 Javascript 读取其他的本地文件 .建议关闭
            this.allowFileAccessFromFileURLs = false
            //允许通过 file url 加载的 Javascript 可以访问其他的源，包括其他的文件和 http，https 等其他的源
            this.allowUniversalAccessFromFileURLs = false
            this.pluginState = ON_DEMAND
            this.layoutAlgorithm = SINGLE_COLUMN
        }
    }

    @Suppress("DEPRECATION")
    @SuppressLint("SetJavaScriptEnabled")
    fun updateSetting(
        view: com.tencent.smtt.sdk.WebView, javaScriptEnabled: Boolean = false
    ): com.tencent.smtt.sdk.WebSettings {
        return view.settings.apply {
            // 设置WebView是否允许执行JavaScript脚本，默认false，不允许。
            this.javaScriptEnabled = javaScriptEnabled
            //支持通过JS打开新窗口
            this.javaScriptCanOpenWindowsAutomatically = true
            //设置编码格式
            this.defaultTextEncodingName = TEXT_ENCODE
            //设置字体大小
            this.defaultFontSize = 16
            //设置 WebView 支持的最小字体大小，默认为 8
            this.minimumFontSize = 10

            //设置自适应屏幕，两者合用
            this.useWideViewPort = true //将图片调整到适合webview的大小
            this.loadWithOverviewMode = true // 缩放至屏幕的大小

            // 缩放操作
            this.setSupportZoom(true)//支持缩放，默认为true。是下面那个的前提。
            this.builtInZoomControls = true  //设置内置的缩放控件。若为false，则该WebView不可缩放
            this.displayZoomControls = false //隐藏原生的缩放控件

            // 设置页面文本的尺寸,百分比，默认NORMAL
            this.textZoom = 100
            //支持自动加载图片
            this.loadsImagesAutomatically = true
            //设置可以访问文件
            this.allowFileAccess = true
            //设置缓存模式
            this.cacheMode = LOAD_DEFAULT
            //是否开启DOM缓存
            this.domStorageEnabled = true
            // 数据库存储API是否可用
            this.databaseEnabled = true
            // 缓存路径
            this.databasePath = PathUtil.getInternalPath(view.context, WEB_PATH)
            // WebView是否下载图片资源，默认为true
            this.loadsImagesAutomatically = true
            // 设置WebView是否支持多窗口
            this.setSupportMultipleWindows(false)
            //是否阻塞加载网络图片  协议http or https
            this.blockNetworkImage = false
            //允许加载本地文件html  file协议
            this.allowFileAccess = true
            //将图片调整到适合webView的大小 useWideViewPort
            this.useWideViewPort = true
            // 是否允许WebView度超出以概览的方式载入页面
            this.loadWithOverviewMode = true
            this.setNeedInitialFocus(true)
            // 定位是否可用
            this.setGeolocationEnabled(true)
            //通过 file url 加载的 Javascript 读取其他的本地文件 .建议关闭
            this.setAllowFileAccessFromFileURLs(false)
            //允许通过 file url 加载的 Javascript 可以访问其他的源，包括其他的文件和 http，https 等其他的源
            this.setAllowUniversalAccessFromFileURLs(false)
            this.pluginState = WebSettings.PluginState.ON_DEMAND
            this.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        }
    }

    fun loadHtml(webView: android.webkit.WebView, html: String) {
        webView.loadDataWithBaseURL(null, html, TEXT_HTML, TEXT_ENCODE, null)
    }

    fun loadHtml(webView: com.tencent.smtt.sdk.WebView, html: String) {
        webView.loadDataWithBaseURL(null, html, TEXT_HTML, TEXT_ENCODE, null)
    }

    fun destroy(webView: android.webkit.WebView?) {
        try {
            //销毁Webview
            //在关闭了Activity时，如果Webview的音乐或视频，还在播放。就必须销毁Webview
            //但是注意：webview调用destory时,webview仍绑定在Activity上
            //这是由于自定义webview构建时传入了该Activity的context对象
            //因此需要先从父容器中移除webview,然后再销毁webview:
            val parent = webView?.parent as? ViewGroup ?: return
            webView.loadDataWithBaseURL(null, "", TEXT_HTML, TEXT_ENCODE, null)
            parent.removeView(webView)
            webView.visibility = View.GONE // 把destroy()延后
            webView.clearMatches()
            ////这个api仅仅清除自动完成填充的表单数据，并不会清除WebView存储到本地的数据
            webView.clearFormData()
            webView.clearSslPreferences()
            //清除当前webview访问的历史记录:只会webview访问历史记录里的所有记录除了当前访问记录
            webView.clearHistory()
            //清除网页访问留下的缓存:由于内核缓存是全局的因此这个方法不仅仅针对webview而是针对整个应用程序.
            webView.clearCache(true)
            webView.destroy()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun destroy(webView: com.tencent.smtt.sdk.WebView?) {
        try {
            //销毁Webview
            //在关闭了Activity时，如果Webview的音乐或视频，还在播放。就必须销毁Webview
            //但是注意：webview调用destory时,webview仍绑定在Activity上
            //这是由于自定义webview构建时传入了该Activity的context对象
            //因此需要先从父容器中移除webview,然后再销毁webview:
            val parent = webView?.parent as? ViewGroup ?: return
            webView.loadDataWithBaseURL(null, "", TEXT_HTML, TEXT_ENCODE, null)
            parent.removeView(webView)
            webView.visibility = View.GONE // 把destroy()延后
            webView.clearMatches()
            ////这个api仅仅清除自动完成填充的表单数据，并不会清除WebView存储到本地的数据
            webView.clearFormData()
            webView.clearSslPreferences()
            //清除当前webview访问的历史记录:只会webview访问历史记录里的所有记录除了当前访问记录
            webView.clearHistory()
            //清除网页访问留下的缓存:由于内核缓存是全局的因此这个方法不仅仅针对webview而是针对整个应用程序.
            webView.clearCache(true)
            webView.destroy()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}