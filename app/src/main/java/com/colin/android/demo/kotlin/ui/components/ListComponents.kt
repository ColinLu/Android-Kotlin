package com.colin.android.demo.kotlin.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> BaseRefreshScreen(
    title: String,
    items: List<T>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onNavigationClick: () -> Unit = {},
    navigationIcon: ImageVector = Icons.Default.Menu,
    actions: @Composable RowScope.() -> Unit = {},
    itemContent: @Composable (Int, T) -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = title,
                navigationIcon = navigationIcon,
                onNavigationClick = onNavigationClick,
                actions = actions
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        RefreshableList(
            items = items,
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.padding(padding),
            itemContent = itemContent
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> RefreshableList(
    items: List<T>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    itemContent: @Composable (Int, T) -> Unit
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            itemsIndexed(items) { index, item ->
                itemContent(index, item)
                if (index < items.size - 1) {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }
}

@Composable
fun TextListItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ListItem(
        headlineContent = { Text(text = text) },
        modifier = modifier.clickable { onClick() }
    )
}
