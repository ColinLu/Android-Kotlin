package com.colin.android.demo.kotlin.ui.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import com.colin.android.demo.kotlin.ui.components.BaseRefreshScreen
import com.colin.android.demo.kotlin.ui.components.TextListItem
import com.colin.android.demo.kotlin.ui.widget.video.VideoViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import com.colin.android.demo.kotlin.ui.preview.ScreenPreviews
import com.colin.android.demo.kotlin.ui.theme.AppTheme

@OptIn(UnstableApi::class)
@Composable
fun VideoScreen(
    onBackClick: () -> Unit,
    viewModel: VideoViewModel = viewModel()
) {
    val items by viewModel.list.collectAsStateWithLifecycle(initialValue = emptyList())
    val isRefreshing by viewModel.showLoading.collectAsStateWithLifecycle(initialValue = false)
    var selectedVideoUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        BaseRefreshScreen(
            title = "Video",
            items = items,
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.loadData() },
            onNavigationClick = onBackClick,
            navigationIcon = Icons.AutoMirrored.Filled.ArrowBack
        ) { _, url ->
            TextListItem(
                text = url,
                onClick = {
                    selectedVideoUrl = url
                    viewModel.player.setMediaItem(MediaItem.fromUri(url))
                    viewModel.player.prepare()
                    viewModel.player.play()
                }
            )
        }

        if (selectedVideoUrl != null) {
            VideoPlayerDialog(
                player = viewModel.player,
                onClose = {
                    selectedVideoUrl = null
                    viewModel.player.pause()
                }
            )
        }
    }
}

@OptIn(UnstableApi::class)
@ScreenPreviews
@Composable
fun VideoScreenPreview() {
    AppTheme {
        VideoScreen(onBackClick = {})
    }
}
