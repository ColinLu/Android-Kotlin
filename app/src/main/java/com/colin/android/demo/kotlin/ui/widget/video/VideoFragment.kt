package com.colin.android.demo.kotlin.ui.widget.video

import android.os.Bundle
import androidx.annotation.OptIn
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.colin.android.demo.kotlin.R
import com.colin.android.demo.kotlin.adapter.StringAdapter
import com.colin.android.demo.kotlin.app.AppFragment
import com.colin.android.demo.kotlin.databinding.LayoutRefreshListBinding
import com.colin.android.demo.kotlin.dialog.DialogVideoPlayer
import com.colin.library.android.widget.recycler.SpaceItemDecoration
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
class VideoFragment : AppFragment<LayoutRefreshListBinding, VideoViewModel>(), Player.Listener {
    private val adapter by lazy { StringAdapter() }

    override fun initView(bundle: Bundle?, savedInstanceState: Bundle?) {
        viewBinding.apply {
            refresh.setColorSchemeResources(
                R.color.purple_200, R.color.purple_500, R.color.purple_700
            )
            refresh.setOnRefreshListener { loadData(true) }

            list.apply {
                this.layoutManager = LinearLayoutManager(requireActivity())
                this.adapter = this@VideoFragment.adapter
                this.addItemDecoration(SpaceItemDecoration(space = 5))
            }
            adapter.onItemClickListener = { _, item, position ->
                play(item)
            }
        }
    }

    private fun play(url: String) {
        val item = MediaItem.fromUri(url)
        DialogVideoPlayer(item).show(childFragmentManager, "VideoPlayerDialog")
    }

    override fun initData(bundle: Bundle?, savedInstanceState: Bundle?) {
        lifecycleScope.launch {
            viewModel.list.flowWithLifecycle(lifecycle).collect {
                adapter.submitList(it)
                viewBinding.refresh.isRefreshing = false
            }
        }

    }

    override fun loadData(refresh: Boolean) {
        viewModel.loadData()
    }


}