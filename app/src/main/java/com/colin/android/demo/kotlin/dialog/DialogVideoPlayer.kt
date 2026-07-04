package com.colin.android.demo.kotlin.dialog

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.colin.android.demo.kotlin.app.AppDialogFragment
import com.colin.android.demo.kotlin.databinding.DialogVideoPlayerBinding
import com.colin.android.demo.kotlin.ui.widget.video.VideoViewModel

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2026-07-05 08:41
 *
 * Des   :VideoPlayerDialog
 */
class DialogVideoPlayer(val item: MediaItem) : AppDialogFragment<DialogVideoPlayerBinding>(),
    Player.Listener {
    internal val viewModel: VideoViewModel by lazy {
        ViewModelProvider.create(viewModelStore)[VideoViewModel::class.java]
    }
    var isPlaying = false
    override fun initView(bundle: Bundle?, savedInstanceState: Bundle?) {
        viewBinding.playerView.player = viewModel.player.also {
            it.setMediaItem(item)
            it.addListener(this)
            it.playWhenReady = true
            it.prepare()
        }
    }

    override fun initData(bundle: Bundle?, savedInstanceState: Bundle?) {
    }

    override fun onResume() {
        super.onResume()
        viewBinding.playerView.player?.apply {
            this.playWhenReady = isPlaying
            if (this@DialogVideoPlayer.isPlaying) this.prepare()
        }
    }

    override fun onPause() {
        viewBinding.playerView.player?.let {
            isPlaying = false
            it.pause()
        }
        super.onPause()
    }

    override fun onStop() {
        viewBinding.playerView.player?.let {
            isPlaying = false
            it.stop()
        }
        super.onStop()
    }

    override fun onDestroyView() {
        viewModel.player.removeListener(this)
        super.onDestroyView()
    }
}