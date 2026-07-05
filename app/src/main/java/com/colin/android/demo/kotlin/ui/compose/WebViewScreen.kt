package com.colin.android.demo.kotlin.ui.compose

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.colin.android.demo.kotlin.ui.components.AppTopBar
import com.colin.android.demo.kotlin.ui.components.AppWebView
import com.colin.android.demo.kotlin.ui.preview.ScreenPreviews
import com.colin.android.demo.kotlin.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebViewScreen(
    url: String,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Web View",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onBackClick
            )
        }
    ) { padding ->
        AppWebView(
            url = url,
            modifier = Modifier.padding(padding)
        )
    }
}

@ScreenPreviews
@Composable
fun WebViewScreenPreview() {
    AppTheme {
        WebViewScreen(url = "https://www.google.com", onBackClick = {})
    }
}
