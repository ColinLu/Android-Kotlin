# Network Module

基于 Retrofit + OkHttp + Coroutines 封装的网络请求模块。

## 核心组件

- **NetworkHelper**: 配置中心，包含 Retrofit 实例、Gson 配置、拦截器管理。
- **HttpExt**: 提供协程扩展方法，简化网络请求发起流程。

## 使用方法

### 1. 初始化配置

在 Application 中设置 BaseUrl：

```kotlin
NetworkHelper.baseUrl = "https://api.example.com/"
// 可选：添加拦截器
NetworkHelper.addInterceptor(loggingInterceptor)
```

### 2. 定义 API 接口

```kotlin
interface ApiService {
    @GET("users/profile")
    suspend fun getUserProfile(): AppResponse<User>
}
```

### 3. 在 ViewModel 中发起请求

使用 `ViewModel.request` 扩展方法，自动绑定 `viewModelScope`：

```kotlin
class MyViewModel : ViewModel() {
    val userData = MutableLiveData<User>()

    fun loadUser() {
        request(
            request = { NetworkHelper.create<ApiService>().getUserProfile() },
            result = { user -> 
                userData.value = user 
            },
            state = { code, msg ->
                // 处理业务错误码
            },
            loading = { show ->
                // 控制 Loading 对话框显示
            }
        )
    }
}
```

### 4. 使用 Flow 发起请求

```kotlin
viewModelScope.launch {
    requestFlow { NetworkHelper.create<ApiService>().getUserProfile() }
        .collect { user ->
            // 处理结果
        }
}
```

## 特性

- **自动重试**: 支持配置重试次数（默认为 3 次）。
- **超时处理**: 支持全局超时配置。
- **状态回调**: 统一处理成功、失败及异常状态。
- **异常捕获**: 内置 `handleFailure` 统一解析网络异常并转换为友好提示。
