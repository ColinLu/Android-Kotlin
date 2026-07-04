# Utils Module

Android 通用工具类库，提供日志、存储、Toast、常用扩展函数等功能。

## 主要工具类

### 1. Log (日志工具)
支持自动获取调用处的文件名和行号，支持 JSON/XML 格式化。

```kotlin
Log.d("Hello Kotlin")
Log.e("CustomTag", "Error message")
Log.json(jsonString) // 格式化打印 JSON
```

### 2. ToastUtil (提示工具)
内置防抖处理，防止短时间内弹出多个重复 Toast。

```kotlin
ToastUtil.show("操作成功")
ToastUtil.show(R.string.save_success)
```

### 3. SpUtil (存储工具)
基于 SharedPreferences 的轻量级存储，支持多文件缓存和同步/异步提交。

```kotlin
SpUtil.put("is_first_login", false)
val isFirst = SpUtil.getBoolean("is_first_login", true)
```

### 4. 扩展函数 (Extensions)
- **ViewExt**: 
  - `View.onClick`: 防抖点击。
  - `View.visible(Boolean)`: 快速控制可见性。
- **DimensExt**: 
  - `Int.dp()`: 将 px 转换为 dp。
- **FlowExt**: 提供常用的 Flow 操作封装。

## 配置

在使用之前，建议在 Application 中进行初始化：

```kotlin
UtilHelper.init(
    UtilConfig.newBuilder(this, BuildConfig.DEBUG)
        .setLogTag("MyApp")
        .setLogLevel(Log.VERBOSE)
        .build()
)
```
