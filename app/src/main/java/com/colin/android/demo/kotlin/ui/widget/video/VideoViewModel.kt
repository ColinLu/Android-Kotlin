package com.colin.android.demo.kotlin.ui.widget.video

import androidx.annotation.OptIn
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.colin.android.demo.kotlin.R
import com.colin.android.demo.kotlin.app.App
import com.colin.android.demo.kotlin.app.AppViewModel
import com.colin.android.demo.kotlin.app.DELAY_TIME_REFRESH
import com.colin.library.android.widget.video.VideoMediaManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@OptIn(UnstableApi::class)
class VideoViewModel : AppViewModel(), Player.Listener {
    private var _list = MutableSharedFlow<List<String>>()
    val list = _list.asSharedFlow()

    val player: ExoPlayer by lazy {
        VideoMediaManager.getMediaPlayer(App.getInstance()).also {
            it.addListener(this)
        }
    }

    fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            loading(true)
            delay(DELAY_TIME_REFRESH.milliseconds)
            val list = arrayOf(
                "android.resource://${App.getInstance().packageName}/${R.raw.splash}",
                "https://test-streams.mux.dev/x36xhzz/url_6/193039199_mp4_h264_aac_hq_7.m3u8",
                "https://s.wanwuzhinan.top/m3u8/1080p/1.3/100001/6fc6453a46e22186d989a50306c588a8.m3u8",
            ).asList()
            _list.emit(list)
            loading(false)
        }
    }

}
