package com.colin.android.demo.kotlin.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.colin.android.demo.kotlin.ui.components.BaseRefreshScreen
import com.colin.android.demo.kotlin.ui.components.TextListItem
import com.colin.android.demo.kotlin.ui.method.MethodViewModel
import com.colin.android.demo.kotlin.ui.preview.ScreenPreviews
import com.colin.android.demo.kotlin.ui.theme.AppTheme

@Composable
fun MethodScreen(
    onNavigateToLog: () -> Unit,
    onNavigateToNetwork: () -> Unit,
    onNavigateToAidl: () -> Unit,
    onNavigateToNfc: () -> Unit,
    onOpenDrawer: () -> Unit,
    viewModel: MethodViewModel = viewModel()
) {
    val items by viewModel.list.collectAsStateWithLifecycle(initialValue = emptyList())
    val isRefreshing by viewModel.showLoading.collectAsStateWithLifecycle(initialValue = false)

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    BaseRefreshScreen(
        title = "Methods",
        items = items,
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.loadData() },
        onNavigationClick = onOpenDrawer
    ) { _, item ->
        TextListItem(
            text = item,
            onClick = {
                when {
                    item.contains("Log", ignoreCase = true) -> onNavigateToLog()
                    item.contains("Http", ignoreCase = true) || item.contains("Network", ignoreCase = true) -> onNavigateToNetwork()
                    item.contains("AIDL", ignoreCase = true) -> onNavigateToAidl()
                    item.contains("NFC", ignoreCase = true) -> onNavigateToNfc()
                }
            }
        )
    }
}

@ScreenPreviews
@Composable
fun MethodScreenPreview() {
    AppTheme {
        MethodScreen(
            onNavigateToLog = {},
            onNavigateToNetwork = {},
            onNavigateToAidl = {},
            onNavigateToNfc = {},
            onOpenDrawer = {}
        )
    }
}
