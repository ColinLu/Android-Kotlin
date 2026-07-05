package com.colin.android.demo.kotlin.ui.compose

import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.colin.android.demo.kotlin.ui.components.BaseRefreshScreen
import com.colin.android.demo.kotlin.ui.components.TextListItem
import com.colin.android.demo.kotlin.ui.widget.web.WebViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.colin.android.demo.kotlin.ui.preview.ScreenPreviews
import com.colin.android.demo.kotlin.ui.theme.AppTheme

@Composable
fun WebIndexScreen(
    onNavigateToWeb: (String) -> Unit,
    onBackClick: () -> Unit,
    viewModel: WebViewModel = viewModel()
) {
    val items by viewModel.history.observeAsState(initial = emptyList())
    val isRefreshing by viewModel.showLoading.collectAsStateWithLifecycle(initialValue = false)

    LaunchedEffect(Unit) {
        viewModel.loadData(true)
    }

    BaseRefreshScreen(
        title = "Web Index",
        items = items,
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.loadData(true) },
        onNavigationClick = onBackClick,
        navigationIcon = Icons.AutoMirrored.Filled.ArrowBack
    ) { _, item ->
        TextListItem(
            text = item,
            onClick = { onNavigateToWeb(item) }
        )
    }
}

@ScreenPreviews
@Composable
fun WebIndexScreenPreview() {
    AppTheme {
        WebIndexScreen(onNavigateToWeb = {}, onBackClick = {})
    }
}
