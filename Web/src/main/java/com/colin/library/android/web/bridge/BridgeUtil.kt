package com.colin.library.android.web.bridge

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.webkit.WebView
import com.colin.library.android.utils.IOUtil
import com.colin.library.android.utils.encrypt.DecodeUtil
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025-07-01 11:24
 *
 * Des   :BridgeUtil
 */
object BridgeUtil {
    /*格式为   colin://return/{function}/returncontent*/
    const val COLIN_OVERRIDE_SCHEMA: String = "colin://"
    val COLIN_RETURN_DATA: String = COLIN_OVERRIDE_SCHEMA + "return/"
    val COLIN_FETCH_QUEUE: String = COLIN_RETURN_DATA + "_fetchQueue/"
    const val EMPTY_STR: String = ""
    const val SPLIT_MARK: String = "/"
    const val JAVASCRIPT_STR: String = "javascript:"
    const val UNDERLINE_STR: String = "_"
    const val CALLBACK_ID_FORMAT: String = "JAVA_CB_%s"
    const val JS_HANDLE_MESSAGE_FROM_JAVA: String =
        "javascript:WebViewJavascriptBridge._handleMessageFromNative('%s');"
    const val JS_FETCH_QUEUE_FROM_JAVA: String = "javascript:WebViewJavascriptBridge._fetchQueue();"
    const val BRIDGE_JS_FILE: String = "WebViewJavascriptBridge.js"
    const val BRIDGE_JS_FILE_NAME: String = "WebViewJavascriptBridge"

    /**
     * 例子 javascript:WebViewJavascriptBridge._fetchQueue(); --> _fetchQueue
     *
     * @param jsUrl url
     * @return 返回字符串，注意获取的时候判断空
     */
    fun parseFunctionName(jsUrl: String): String {
        return BridgeUtil.parseFunctionName(jsUrl, BRIDGE_JS_FILE_NAME)
    }

    /**
     * 通过URL 得到方法名
     *
     * @param jsUrl      html 网址
     * @param jsFileName 自定义js 名字 eg:WebViewJavascriptBridge
     * @return
     */
    fun parseFunctionName(jsUrl: String, jsFileName: String?): String {
        return jsUrl.replace("$JAVASCRIPT_STR$jsFileName.", EMPTY_STR)
            .replace("\\(.*\\);".toRegex(), EMPTY_STR)
    }

    /**
     * 获取到传递信息的body值
     * url = colin://return/_fetchQueue/[{"responseId":"JAVA_CB_2_3957",
     * "responseData":"Javascript Says Right back aka!"}]
     *
     * @param url url
     * @return 返回字符串，注意获取的时候判断空
     */
    fun getDataFromReturnUrl(url: String): String? {
        // return = [{"responseId":"JAVA_CB_2_3957","responseData":"Javascript Says Right back aka!"}]
        if (url.startsWith(COLIN_FETCH_QUEUE)) return url.replace(COLIN_FETCH_QUEUE, EMPTY_STR)

        // temp = _fetchQueue/[{"responseId":"JAVA_CB_2_3957","responseData":"Javascript Says Right back aka!"}]
        val temp = url.replace(COLIN_RETURN_DATA, EMPTY_STR)
        val functions: Array<String?> =
            temp.split(SPLIT_MARK.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        if (functions.size >= 2) {
            val sb = StringBuilder()
            for (i in 1..<functions.size) sb.append(functions[i])
            // return = [{"responseId":"JAVA_CB_2_3957","responseData":"Javascript Says Right back aka!"}]
            return sb.toString()
        }
        return null
    }

    // 获取到传递信息的方法
    // url = colin://return/_fetchQueue/[{"responseId":"JAVA_CB_1_360","responseData":"Javascript Says Right back aka!"}]
    fun getFunctionFromReturnUrl(url: String): String? {
        // temp = _fetchQueue/[{"responseId":"JAVA_CB_1_360","responseData":"Javascript Says Right back aka!"}]
        val temp = url.replace(COLIN_RETURN_DATA, EMPTY_STR)
        val functions: Array<String?> =
            temp.split(SPLIT_MARK.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        // functionAndData[0] = _fetchQueue
        if (functions.isNotEmpty()) return functions[0]
        return null
    }

    /**
     * js 文件将注入为第一个script引用
     *
     * @param view WebView
     * @param url  url
     */
    fun loadJs(view: WebView, url: String?) {
        var js = "var newscript = document.createElement(\"script\");"
        js += "newscript.src=\"" + url + "\";"
        js += "document.scripts[0].parentNode.insertBefore(newscript,document.scripts[0]);"
        view.loadUrl(JAVASCRIPT_STR + js)
    }

    fun loadJs(view: com.tencent.smtt.sdk.WebView, url: String?) {
        var js = "var newscript = document.createElement(\"script\");"
        js += "newscript.src=\"" + url + "\";"
        js += "document.scripts[0].parentNode.insertBefore(newscript,document.scripts[0]);"
        view.loadUrl(JAVASCRIPT_STR + js)
    }

    /**
     * 这里只是加载lib包中assets中的 WebViewJavascriptBridge.js
     *
     * @param view webview
     * @param path 路径
     */
    fun loadLocalJs(view: WebView, path: String) {
        val jsContent = assetFile2Str(view.context, path)
        view.loadUrl(JAVASCRIPT_STR + jsContent)
    }

    fun loadLocalJs(view: com.tencent.smtt.sdk.WebView, path: String) {
        val jsContent = assetFile2Str(view.context, path)
        view.loadUrl(JAVASCRIPT_STR + jsContent)
    }

    /**
     * 拦截地址的时候判断是否js
     *
     * @param view
     * @param url
     * @return
     */
    fun loadJsBridge(view: View, url: String?): Boolean {
        if (view !is BridgeJavascriptWeb) return false
        val newUrl = DecodeUtil.url(url, "UTF-8")
        if (newUrl.isNullOrEmpty()) return false
        if (newUrl.startsWith(COLIN_RETURN_DATA)) { // 如果是返回数据
            (view as BridgeJavascriptWeb).handlerReturnData(newUrl)
            return true
        } else if (newUrl.startsWith(COLIN_OVERRIDE_SCHEMA)) { //遍历 js消息反馈给Android 原生
            (view as BridgeJavascriptWeb).flushMessageQueue()
            return true
        }
        return false
    }

    /**
     * 注入Js
     *
     * @param view
     * @param url
     */
    fun insertJsBridge(view: WebView, url: String?) {
        if (view !is BridgeJavascriptWeb || url.isNullOrEmpty()) return
        //注入本地资源
        loadLocalJs(view, BRIDGE_JS_FILE)
        val webViewBridge: BridgeJavascriptWeb = view as BridgeJavascriptWeb
        val list = webViewBridge.getMessage()
        if (list.isNullOrEmpty().not()) {
            for (bridgeMessage in list) {
                //分发message 必须在主线程才分发成功
                webViewBridge.dispatchMessage(bridgeMessage)
            }
            webViewBridge.setMessage(null)
        }
    }

    fun insertJsBridge(view: com.tencent.smtt.sdk.WebView, url: String?) {
        if (view !is BridgeJavascriptWeb || TextUtils.isEmpty(url)) return
        //注入本地资源
        loadLocalJs(view, BRIDGE_JS_FILE)
        val webViewBridge: BridgeJavascriptWeb = view as BridgeJavascriptWeb
        val list: MutableList<BridgeMessage>? = webViewBridge.getMessage()
        if (list.isNullOrEmpty().not()) {
            for (bridgeMessage in list) {
                //分发message 必须在主线程才分发成功
                webViewBridge.dispatchMessage(bridgeMessage)
            }
            webViewBridge.setMessage(null)
        }
    }

    /**
     * 解析assets文件夹里面的代码,去除注释,取可执行的代码
     *
     * @param c      context
     * @param urlStr 路径
     * @return 可执行代码
     */
    private fun assetFile2Str(c: Context, urlStr: String): String? {
        var `in`: InputStream? = null
        try {
            `in` = c.assets.open(urlStr)
            val bufferedReader = BufferedReader(InputStreamReader(`in`))
            var line: String? = null
            val sb = java.lang.StringBuilder()
            do {
                line = bufferedReader.readLine()
                // 去除注释
                if (line != null && !line.matches("^\\s*//.*".toRegex())) {
                    sb.append(line)
                }
            } while (line != null)
            bufferedReader.close()
            `in`.close()
            return sb.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            IOUtil.close(`in`)
        }
        return null
    }
}