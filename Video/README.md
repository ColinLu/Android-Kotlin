# Video Module

基于 Media3 (ExoPlayer) 封装的视频播放组件，支持手势控制、生命周期管理和解密播放。

## 核心组件

- **VideoMediaView**: 播放器控件，集成手势交互（亮度、音量、进度）。
- **VideoMediaManager**: 播放器管理中心，负责实例创建和资源加载。

## 使用方法

### 1. 布局集成

```xml
<com.colin.library.android.widget.video.VideoMediaView
    android:id="@+id/videoView"
    android:layout_width="match_parent"
    android:layout_height="250dp" />
```

### 2. 代码绑定

```kotlin
val player = VideoMediaManager.get(context)
videoView.bind(lifecycle, player)

// 播放网络视频
val mediaItem = MediaItem.fromUri("https://example.com/video.mp4")
videoView.play(mediaItem)
```

## 功能特性

- **手势交互**:
  - 屏幕左侧上下滑动：调节亮度。
  - 屏幕右侧上下滑动：调节音量。
  - 屏幕左右滑动：调节播放进度。
  - 双击：切换播放/暂停。
- **生命周期感知**: 自动处理 `onResume` 继续播放，`onPause` 暂停，`onDestroy` 释放资源。
- **解密支持**: 通过自定义 `DataSource` 支持加密视频流的实时解密播放。
