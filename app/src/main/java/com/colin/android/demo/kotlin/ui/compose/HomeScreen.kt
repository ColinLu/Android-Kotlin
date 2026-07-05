package com.colin.android.demo.kotlin.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.colin.android.demo.kotlin.R
import com.colin.android.demo.kotlin.ui.components.AppTopBar
import com.colin.android.demo.kotlin.ui.components.TextListItem
import com.colin.android.demo.kotlin.ui.list.ListViewModel
import com.colin.android.demo.kotlin.ui.preview.ScreenPreviews
import com.colin.android.demo.kotlin.ui.theme.AppTheme
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@ScreenPreviews
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    AppTheme {
        HomeScreen(onOpenDrawer = {})
    }
}

@Composable
fun HomeListContent(viewModel: ListViewModel? = null) {
    // Note: In a real app, you'd use the page index to load different data
    val items = remember {
        val array = arrayOf(
            "Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6", "Item 7", "Item 8",
            "Item 9", "Item 10", "Item 11", "Item 12", "Item 13", "Item 14", "Item 15"
        )
        array.toList()
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(items) { item ->
            TextListItem(text = item, onClick = { })
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenDrawer: () -> Unit = {},
    listViewModel: ListViewModel = viewModel()
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.app_name),
                onNavigationClick = onOpenDrawer,
                actions = {
                    IconButton(onClick = { /* Handle language change */ }) {
                        Icon(Icons.Default.Language, contentDescription = "Language", tint = Color.White)
                    }
                }
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        val bannerImages = listOf(
            R.mipmap.banner1, R.mipmap.banner2, R.mipmap.banner3, R.mipmap.banner4, R.mipmap.banner5
        )
        val tabTitles = listOf("Path", "Widgets", "Video")

        val bannerHeight = 200.dp
        val bannerHeightPx = with(LocalDensity.current) { bannerHeight.toPx() }
        var bannerOffsetHeightPx by remember { mutableFloatStateOf(0f) }

        val nestedScrollConnection = remember {
            object : NestedScrollConnection {
                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                    val delta = available.y
                    val newOffset = bannerOffsetHeightPx + delta
                    bannerOffsetHeightPx = newOffset.coerceIn(-bannerHeightPx, 0f)
                    return Offset.Zero
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .nestedScroll(nestedScrollConnection)
        ) {
            // ViewPager content
            val pagerState = rememberPagerState { tabTitles.size }
            val scope = rememberCoroutineScope()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .offset { IntOffset(0, (bannerHeightPx + bannerOffsetHeightPx).roundToInt()) }
            ) {
                TabRow(
                    selectedTabIndex = pagerState.currentPage,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                            text = { Text(title) }
                        )
                    }
                }

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    beyondViewportPageCount = 1
                ) { page ->
                    HomeListContent(listViewModel)
                }
            }

            // Banner (Top)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(bannerHeight)
                    .offset { IntOffset(0, bannerOffsetHeightPx.roundToInt()) }
            ) {
                val bannerPagerState = rememberPagerState { bannerImages.size }
                HorizontalPager(
                    state = bannerPagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    val isPreview = LocalInspectionMode.current
                    if (isPreview) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = androidx.compose.ui.Alignment.Center
                        ) {
                            Text("Banner ${page + 1}")
                        }
                    } else {
                        Image(
                            painter = painterResource(id = bannerImages[page]),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}

