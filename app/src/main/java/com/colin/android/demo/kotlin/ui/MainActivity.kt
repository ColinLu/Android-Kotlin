package com.colin.android.demo.kotlin.ui

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import com.colin.android.demo.kotlin.ui.compose.MainScreen
import com.colin.android.demo.kotlin.ui.theme.AppTheme
import com.colin.library.android.widget.video.VideoMediaManager

@UnstableApi
class MainActivity : ComponentActivity(), Player.Listener {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        VideoMediaManager.connectSessionToken(this)
        setContent {
            AppTheme {
                MainScreen()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.update(true)
    }

    override fun onPause() {
        super.onPause()
        viewModel.update(false)
    }

    override fun onDestroy() {
        VideoMediaManager.release()
        super.onDestroy()
    }

    // TODO: Remove this once fragments are fully migrated to Compose
    fun setMenuVisible(resId: Int, visible: Boolean) {}

}
