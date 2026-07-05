package com.colin.android.demo.kotlin.ui.compose

import androidx.annotation.ArrayRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.colin.android.demo.kotlin.ui.components.BaseRefreshScreen
import com.colin.android.demo.kotlin.ui.components.TextListItem
import com.colin.android.demo.kotlin.ui.method.ModuleViewModel
import com.colin.android.demo.kotlin.ui.preview.ScreenPreviews
import com.colin.android.demo.kotlin.ui.theme.AppTheme

@Composable
fun ModuleScreen(
    title: String,
    @ArrayRes arrayRes: Int,
    onBackClick: () -> Unit,
    viewModel: ModuleViewModel = viewModel(),
    onItemClick: (String) -> Unit = {}
) {
    val items by viewModel.list.collectAsStateWithLifecycle(initialValue = emptyList())
    val isRefreshing by viewModel.showLoading.collectAsStateWithLifecycle(initialValue = false)

    LaunchedEffect(arrayRes) {
        viewModel.loadData(arrayRes)
    }

    BaseRefreshScreen(
        title = title,
        items = items,
        isRefreshing = isRefreshing,
        onRefresh = {
            // Need a way to trigger loadData again with same arrayRes
            // For now, let's assume it's one-time or we add it to ViewModel
        },
        onNavigationClick = onBackClick,
        navigationIcon = Icons.AutoMirrored.Filled.ArrowBack
    ) { _, item ->
        TextListItem(
            text = item,
            onClick = { onItemClick(item) }
        )
    }
}

@ScreenPreviews
@Composable
fun ModuleScreenPreview() {
    AppTheme {
        ModuleScreen(
            title = "Module Test",
            arrayRes = 0,
            onBackClick = {}
        )
    }
}
