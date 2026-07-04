# Widgets Module

通用 UI 组件库，包含常用的自定义 View 和 Activity/Fragment 基类。

## 基类架构

- **BaseActivity / BaseFragment**: 提供统一的生命周期日志、加载状态管理 (`ILoad`)。
- **BaseAdapter / BaseViewHolder**: 简化 RecyclerView 适配器的编写。

## 常用组件

### 1. BannerView (轮播图)
基于 ViewPager2 实现，支持无限循环、自动轮播、自定义指示器。

```xml
<com.colin.library.android.widget.banner.BannerView
    android:id="@+id/banner"
    app:autoPlay="true"
    app:interval="3000"
    app:radius="8dp" />
```

### 2. ClearEditText (带清除按钮的输入框)
自动在有内容时显示清除按钮。

```xml
<com.colin.library.android.widget.edit.ClearEditText
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:hint="请输入内容" />
```

### 3. RoundImageView (圆角图片)
支持圆角、圆形切图，支持描边。

```xml
<com.colin.library.android.widget.image.RoundImageView
    android:layout_width="100dp"
    android:layout_height="100dp"
    app:radius="50dp" /> <!-- 圆形 -->
```

### 4. WheelView (滚轮选择器)
用于日期选择、地区选择等场景，支持 3D 效果。

## 其他特性

- **Skeleton**: 骨架屏加载效果。
- **WebView**: 封装了 `CustomWebView`，支持进程化和预加载。
