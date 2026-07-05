package com.colin.android.demo.kotlin.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.colin.android.demo.kotlin.ui.components.BaseRefreshScreen
import com.colin.android.demo.kotlin.ui.components.TextListItem
import com.colin.android.demo.kotlin.ui.preview.ScreenPreviews
import com.colin.android.demo.kotlin.ui.theme.AppTheme
import com.colin.android.demo.kotlin.ui.widget.WidgetViewModel

@Composable
fun WidgetScreen(
    onNavigateToWeb: () -> Unit,
    onNavigateToVideo: () -> Unit,
    onOpenDrawer: () -> Unit,
    viewModel: WidgetViewModel = viewModel()
) {
    val items by viewModel.list.collectAsStateWithLifecycle(initialValue = emptyList())
    val isRefreshing by viewModel.showLoading.collectAsStateWithLifecycle(initialValue = false)

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    BaseRefreshScreen(
        title = "Widgets",
        items = items,
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.loadData() },
        onNavigationClick = onOpenDrawer
    ) { _, item ->
        TextListItem(
            text = item,
            onClick = {
                if (item.contains("Web", ignoreCase = true)) {
                    onNavigateToWeb()
                } else if (item.contains("Video", ignoreCase = true)) {
                    onNavigateToVideo()
                }
            }
        )
    }
}

@ScreenPreviews
@Composable
fun WidgetScreenPreview() {
    AppTheme {
        WidgetScreen(
            onNavigateToWeb = {},
            onNavigateToVideo = {},
            onOpenDrawer = {}
        )
    }
}
