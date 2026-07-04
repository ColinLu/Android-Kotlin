package com.colin.android.demo.kotlin.ui.widget.video

import android.os.Bundle
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import com.colin.android.demo.kotlin.app.AppFragment
import com.colin.android.demo.kotlin.databinding.FragmentVideoBinding
import com.colin.android.demo.kotlin.ui.slideshow.SlideshowViewModel
import com.colin.library.android.widget.video.VideoMediaManager

class VideoFragment : AppFragment<FragmentVideoBinding, SlideshowViewModel>() {

    @OptIn(UnstableApi::class)
    override fun initView(bundle: Bundle?, savedInstanceState: Bundle?) {
        val player = VideoMediaManager.getMediaPlayer(requireContext())
        viewBinding.video.bind(lifecycle, player)

        // Use a test video URL
        val videoUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
        viewBinding.video.play(MediaItem.fromUri(videoUrl))
    }

    override fun initData(bundle: Bundle?, savedInstanceState: Bundle?) {
    }
}