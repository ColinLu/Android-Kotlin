package com.colin.android.demo.kotlin.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.colin.library.android.widget.scroll.NestedScrollTopWebView
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient

@Composable
fun AppWebView(
    url: String,
    onProgressChange: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var progress by remember { mutableIntStateOf(0) }

    Column(modifier = modifier.fillMaxSize()) {
        if (progress < 100) {
            LinearProgressIndicator(
                progress = { progress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
            )
        }
        AndroidView(
            factory = { context ->
                NestedScrollTopWebView(context).apply {
                    webViewClient = object : WebViewClient() {}
                    webChromeClient = object : WebChromeClient() {
                        override fun onProgressChanged(view: WebView?, newProgress: Int) {
                            progress = newProgress
                            onProgressChange(newProgress)
                        }
                    }
                    loadUrl(url)
                }
            },
            update = { webView ->
                // Optionally handle URL updates here if they are driven by state
            },
            modifier = Modifier.weight(1f)
        )
    }
}
