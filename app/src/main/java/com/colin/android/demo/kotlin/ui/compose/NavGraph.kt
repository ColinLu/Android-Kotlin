package com.colin.android.demo.kotlin.ui.compose

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.colin.android.demo.kotlin.R
import com.colin.android.demo.kotlin.ui.components.BaseRefreshScreen
import com.colin.android.demo.kotlin.ui.components.TextListItem
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.net.URLEncoder

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Method : Screen("method")
    data object Widget : Screen("widget")
    data object Log : Screen("log")
    data object Network : Screen("network")
    data object Aidl : Screen("aidl")
    data object Nfc : Screen("nfc")
    data object WebIndex : Screen("web_index")
    data object Video : Screen("video")
    data object Gallery : Screen("gallery")
    data object Slideshow : Screen("slideshow")
    data object WebView : Screen("web_view/{url}") {
        fun createRoute(url: String) = "web_view/$url"
    }
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    onOpenDrawer: () -> Unit
) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(onOpenDrawer = onOpenDrawer)
        }
        composable(Screen.Method.route) {
            MethodScreen(
                onNavigateToLog = { navController.navigate(Screen.Log.route) },
                onNavigateToNetwork = { navController.navigate(Screen.Network.route) },
                onNavigateToAidl = { navController.navigate(Screen.Aidl.route) },
                onNavigateToNfc = { navController.navigate(Screen.Nfc.route) },
                onOpenDrawer = onOpenDrawer
            )
        }
        composable(Screen.Widget.route) {
            WidgetScreen(
                onNavigateToWeb = { navController.navigate(Screen.WebIndex.route) },
                onNavigateToVideo = { navController.navigate(Screen.Video.route) },
                onOpenDrawer = onOpenDrawer
            )
        }
        composable(Screen.Video.route) {
            VideoScreen(onBackClick = { navController.popBackStack() })
        }
        composable(Screen.Gallery.route) {
            BaseRefreshScreen(
                title = "Gallery",
                items = listOf("Item 1", "Item 2"),
                isRefreshing = false,
                onRefresh = {},
                onNavigationClick = onOpenDrawer
            ) { _, item -> TextListItem(text = item, onClick = {}) }
        }
        composable(Screen.Slideshow.route) {
            BaseRefreshScreen(
                title = "Slideshow",
                items = listOf("Slide 1", "Slide 2"),
                isRefreshing = false,
                onRefresh = {},
                onNavigationClick = onOpenDrawer
            ) { _, item -> TextListItem(text = item, onClick = {}) }
        }
        composable(Screen.Log.route) {
            val TAG = "LogScreen"
            val JSON = "{\n" + "  \"sites\": {\n" + "    \"site\": [\n" + "      {\n" + "        \"id\": \"1\",\n" + "        \"name\": \"菜鸟教程\",\n" + "        \"url\": \"www.runoob.com\"\n" + "      },\n" + "      {\n" + "        \"id\": \"2\",\n" + "        \"name\": \"菜鸟工具\",\n" + "        \"url\": \"www.jyshare.com\"\n" + "      },\n" + "      {\n" + "        \"id\": \"3\",\n" + "        \"name\": \"Google\",\n" + "        \"url\": \"www.google.com\"\n" + "      }\n" + "    ]\n" + "  }\n" + "}"
            val XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" + "<sites>\n" + "    <site>\n" + "        <id>1</id>\n" + "        <name>菜鸟教程</name>\n" + "        <url>www.runoob.com</url>\n" + "    </site>\n" + "    <site>\n" + "        <id>2</id>\n" + "        <name>菜鸟工具</name>\n" + "        <url>www.jyshare.com</url>\n" + "    </site>\n" + "    <site>\n" + "        <id>3</id>\n" + "        <name>Google</name>\n" + "        <url>www.google.com</url>\n" + "    </site>\n" + "</sites>\n"
            
            ModuleScreen(
                title = "Log",
                arrayRes = R.array.log_list,
                onBackClick = { navController.popBackStack() },
                onItemClick = { item ->
                    when (item) {
                        "Log V" -> com.colin.library.android.utils.Log.v(item)
                        "Log V with Tag" -> com.colin.library.android.utils.Log.v(item, TAG)
                        "Log D" -> com.colin.library.android.utils.Log.d(item)
                        "Log D with Tag" -> com.colin.library.android.utils.Log.d(item, TAG)
                        "Log I" -> com.colin.library.android.utils.Log.i(item)
                        "Log I with Tag" -> com.colin.library.android.utils.Log.i(item, TAG)
                        "Log W" -> com.colin.library.android.utils.Log.w(item)
                        "Log W with Tag" -> com.colin.library.android.utils.Log.w(item, TAG)
                        "Log Json" -> com.colin.library.android.utils.Log.json(json = JSON)
                        "Log Json with Tag" -> com.colin.library.android.utils.Log.json(JSON, TAG)
                        "Log Xml" -> com.colin.library.android.utils.Log.xml(xml = XML)
                        "Log Xml with Tag" -> com.colin.library.android.utils.Log.xml(XML, TAG)
                        "Log Error" -> com.colin.library.android.utils.Log.log(Throwable("is error"))
                        else -> com.colin.library.android.utils.Log.log(item)
                    }
                }
            )
        }
        composable(Screen.WebIndex.route) {
            WebIndexScreen(
                onNavigateToWeb = { url -> 
                    val encodedUrl = URLEncoder.encode(url, "UTF-8")
                    navController.navigate(Screen.WebView.createRoute(encodedUrl)) 
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.WebView.route,
            arguments = listOf(navArgument("url") { type = NavType.StringType })
        ) { backStackEntry ->
            val url = backStackEntry.arguments?.getString("url")?.let {
                URLDecoder.decode(it, "UTF-8")
            } ?: ""
            WebViewScreen(url = url, onBackClick = { navController.popBackStack() })
        }
        composable(Screen.Network.route) {
            val items = listOf(
                "Init Network (BaseUrl)",
                "Send Login SMS (Test Request)",
                "Test Network Status"
            )
            BaseRefreshScreen(
                title = "Network",
                items = items,
                isRefreshing = false,
                onRefresh = { },
                onNavigationClick = { navController.popBackStack() },
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack
            ) { _, item ->
                TextListItem(
                    text = item,
                    onClick = {
                        when (item) {
                            "Init Network (BaseUrl)" -> {
                                com.colin.library.android.network.NetworkHelper.baseUrl = "https://api.example.com/"
                            }
                        }
                    }
                )
            }
        }
        composable(Screen.Aidl.route) {
            AidlScreen(onBackClick = { navController.popBackStack() })
        }
        composable(Screen.Nfc.route) {
            val context = LocalContext.current
            val scope = rememberCoroutineScope()
            ModuleScreen(
                title = "NFC",
                arrayRes = R.array.nfc_list,
                onBackClick = { navController.popBackStack() },
                onItemClick = { item ->
                    when (item) {
                        "Check NFC Support" -> {
                            val result = com.colin.nfc.focus.NFCFocus.manager.checkNfcSupport()
                            com.colin.library.android.utils.ToastUtil.show("NFC Support: $result")
                        }
                        "Request Usage Stats Permission" -> {
                            scope.launch {
                                val result = com.colin.nfc.focus.NFCFocus.manager.requestUsageStatsPermission(context as android.app.Activity)
                                com.colin.library.android.utils.ToastUtil.show("Permission Result: ${result.isSuccess}")
                            }
                        }
                        "Get Current Focus Session" -> {
                            scope.launch {
                                val session = com.colin.nfc.focus.NFCFocus.manager.getCurrentSession()
                                com.colin.library.android.utils.ToastUtil.show("Current Session: ${session?.sceneName ?: "None"}")
                            }
                        }
                        "Stop Focus Mode" -> {
                            scope.launch {
                                val session = com.colin.nfc.focus.NFCFocus.manager.getCurrentSession()
                                if (session != null) {
                                    val result = com.colin.nfc.focus.NFCFocus.manager.stopFocus(session.sessionId)
                                    com.colin.library.android.utils.ToastUtil.show("Stop Focus: ${result.isSuccess}")
                                } else {
                                    com.colin.library.android.utils.ToastUtil.show("No active session")
                                }
                            }
                        }
                        "Get Today's Stats" -> {
                            scope.launch {
                                val stats = com.colin.nfc.focus.NFCFocus.manager.getTodayStats()
                                com.colin.library.android.utils.ToastUtil.show("Today's focus count: ${stats.size}")
                            }
                        }
                    }
                }
            )
        }
    }
}
