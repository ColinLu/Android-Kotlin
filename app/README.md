# App Module

项目的主工程模块，整合各功能库实现具体业务需求。

## 技术栈

- **架构**: MVVM (Model-View-ViewModel)
- **数据绑定**: ViewBinding
- **异步**: Kotlin Coroutines
- **导航**: Jetpack Navigation
- **依赖管理**: Version Catalog (`libs.versions.toml`)

## 核心类说明

### 1. AppActivity / AppFragment
业务层基类，利用泛型自动初始化 ViewBinding 和 ViewModel。

```kotlin
class MainActivity : AppActivity<ActivityMainBinding, MainViewModel>() {
    override fun initView(bundle: Bundle?, savedInstanceState: Bundle?) {
        // 直接使用 viewBinding 和 viewModel
        viewBinding.toolbar.title = "Home"
    }
}
```

### 2. App (Application)
完成各组件的初始化：
- `UtilHelper.init`
- `NetworkHelper.baseUrl`
- `QbSdk.initX5Environment` (腾讯 X5 内核)

## 模块依赖关系

- `:AIDL`: 跨进程通信示例。
- `:Network`: 网络请求。
- `:Utils`: 基础工具。
- `:Video`: 视频播放。
- `:Widgets`: UI 组件库。
- `:nfc-focus`: NFC 专注模式核心逻辑。
