# NFC Focus Module

Android NFC 专注模式管理模块，参考 iOS Brick app 功能设计。通过 NFC 标签触发专注模式，智能屏蔽分心应用，帮助用户保持专注。

## 📦 功能特性

### ✅ 已实现（第一阶段 + 第二阶段）
- **NFC 触发**：通过 NFC 标签一键启动/停止专注模式
- **应用监控**：基于 UsageStatsManager 监控前台应用
- **场景配置**：为不同场景（工作/学习/睡觉）绑定不同规则
- **会话持久化**：服务重启后自动恢复专注状态
- **前台服务**：START_STICKY 模式确保服务不被系统杀死
- **使用统计**：专注时长、分心次数统计
- **权限管理**：使用情况访问权限请求和检查
- **覆盖窗口提醒**：检测到分心时显示 Material Design 3 风格的全屏对话框
- **智能通知**：专注开始/结束通知、分心提醒（三个通知渠道）
- **无障碍拦截**：强制阻止打开分心应用（严格模式）
- **WorkManager 保活**：每 15 分钟检查会话状态，自动恢复
- **开机自启动**：设备重启后自动恢复活跃会话
- **智能提醒策略**：分级提醒（温和 → 标准 → 强制），频率控制
- **UI 完全可定制**：提供接口让集成方自定义覆盖窗口和通知样式
- **深色模式支持**：默认 UI 自动适配深色/浅色模式

## 🚀 快速集成

### 1. 添加依赖

在 `build.gradle.kts` 中添加：

```kotlin
dependencies {
    implementation(project(":nfc-focus"))
    // 或者使用 AAR
    // implementation(files("libs/nfc-focus.aar"))
}
```

### 2. 初始化模块

在 `Application` 类中初始化：

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // 初始化 NFC Focus 模块
        NFCFocus.initialize(this)
    }
}
```

### 3. 请求权限

在使用前请求必要权限：

```kotlin
// 在 Activity 中
lifecycleScope.launch {
    val result = NFCFocus.manager.requestUsageStatsPermission(this@MainActivity)
    if (result.isSuccess) {
        // 权限已授予，可以启动专注模式
    } else {
        // 处理权限拒绝
    }
}
```

## 📖 API 使用指南

### 核心入口

所有功能通过 `NFCFocus.manager` 访问：

```kotlin
val manager = NFCFocus.manager
```

### 1. 创建专注场景

```kotlin
val scene = FocusScene(
    name = "工作模式",
    description = "工作时间专注",
    blockedApps = listOf(
        "com.instagram.android",
        "com.facebook.katana",
        "com.twitter.android"
    ),
    allowListType = AllowListType.BLACKLIST,
    timeRules = TimeRule(
        startTime = "09:00",
        endTime = "18:00",
        repeatDays = listOf(1, 2, 3, 4, 5), // 周一至周五
        durationMinutes = 60
    ),
    exceptionRules = ExceptionRule(
        whitelistApps = listOf("com.whatsapp"),
        emergencyContacts = listOf("+1234567890"),
        allowedTimeSlots = listOf("12:00-13:00")
    ),
    unlockMethod = UnlockMethod.NFC_ONLY,
    enableStartNotification = true,
    enableEndNotification = true,
    enableDistractionAlert = true,
    isActive = true
)

lifecycleScope.launch {
    val result = NFCFocus.manager.createScene(scene)
    if (result.isSuccess) {
        val sceneId = result.getOrNull()!!
        Log.d(TAG, "场景创建成功，ID: $sceneId")
    }
}
```

### 2. 启动专注模式

```kotlin
lifecycleScope.launch {
    // 手动启动
    val result = NFCFocus.manager.startFocus(
        sceneId = sceneId,
        triggeredBy = TriggerType.MANUAL
    )
    
    if (result.isSuccess) {
        val session = result.getOrNull()!!
        Log.d(TAG, "专注模式已启动: ${session.sceneName}")
        Log.d(TAG, "会话 ID: ${session.sessionId}")
    } else {
        Log.e(TAG, "启动失败: ${result.exceptionOrNull()?.message}")
    }
}
```

### 3. 停止专注模式

```kotlin
lifecycleScope.launch {
    val currentSession = NFCFocus.manager.getCurrentSession()
    
    if (currentSession != null) {
        val result = NFCFocus.manager.stopFocus(
            sessionId = currentSession.sessionId,
            completedNormally = true // 正常完成
        )
        
        if (result.isSuccess) {
            Log.d(TAG, "专注模式已停止")
        } else {
            Log.e(TAG, "停止失败: ${result.exceptionOrNull()?.message}")
        }
    }
}
```

### 4. NFC 扫描

```kotlin
// 在 Activity 中
override fun onResume() {
    super.onResume()
    
    lifecycleScope.launch {
        NFCFocus.manager.scanTag(this@MainActivity)
            .collect { result ->
                when (result) {
                    is NfcScanResult.Success -> {
                        Log.d(TAG, "扫描到标签: ${result.tagUid}, 名称: ${result.tagName}")
                        
                        // 自动启动关联的专注场景
                        val sceneId = getSceneIdForTag(result.tagUid)
                        if (sceneId != null) {
                            NFCFocus.manager.startFocus(sceneId, TriggerType.NFC)
                        }
                    }
                    is NfcScanResult.Error -> {
                        Log.e(TAG, "扫描错误: ${result.message}")
                    }
                    is NfcScanResult.Cancelled -> {
                        Log.d(TAG, "扫描已取消")
                    }
                }
            }
    }
}

override fun onPause() {
    super.onPause()
    // 取消 NFC 扫描（Flow 会自动取消）
}
```

### 5. 监听状态变化

```kotlin
NFCFocus.manager.setFocusStateListener(object : FocusStateListener {
    override fun onFocusStarted(session: FocusSession) {
        Log.d(TAG, "专注开始: ${session.sceneName}")
        Log.d(TAG, "会话 ID: ${session.sessionId}")
        Log.d(TAG, "触发方式: ${session.triggeredBy}")
        // 更新 UI
    }
    
    override fun onFocusStopped(session: FocusSession, completedNormally: Boolean) {
        Log.d(TAG, "专注结束: ${if (completedNormally) "正常完成" else "提前退出"}")
        Log.d(TAG, "持续时间: ${session.endTime?.minus(session.startTime)} ms")
        // 更新 UI
    }
    
    override fun onDistractionDetected(packageName: String) {
        Log.w(TAG, "检测到分心应用: $packageName")
        // 显示警告或记录统计
    }
})
```

### 6. 获取统计数据

```kotlin
lifecycleScope.launch {
    // 今日统计
    val todayStats = NFCFocus.manager.getTodayStats()
    Log.d(TAG, "今日专注次数: ${todayStats.size}")
    
    // 本周统计
    val weekStats = NFCFocus.manager.getWeekStats()
    
    // 自定义时间范围
    val startDate = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000 // 7天前
    val endDate = System.currentTimeMillis()
    val customStats = NFCFocus.manager.getUsageStats(startDate, endDate)
    
    // 处理统计数据
    customStats.forEach { stat ->
        Log.d(TAG, "场景: ${stat.sceneName}")
        Log.d(TAG, "  时长: ${stat.duration} 秒")
        Log.d(TAG, "  分心次数: ${stat.distractionCount}")
        Log.d(TAG, "  触发方式: ${stat.triggeredBy}")
    }
}
```

### 7. 管理场景

```kotlin
lifecycleScope.launch {
    // 获取所有场景
    val allScenes = NFCFocus.manager.getAllScenes()
    Log.d(TAG, "总场景数: ${allScenes.size}")
    
    // 获取活跃场景
    val activeScenes = NFCFocus.manager.getActiveScenes()
    
    // 获取指定场景
    val scene = NFCFocus.manager.getScene(sceneId)
    
    // 更新场景
    val updatedScene = scene?.copy(name = "新名称")
    if (updatedScene != null) {
        NFCFocus.manager.updateScene(updatedScene)
    }
    
    // 删除场景
    NFCFocus.manager.deleteScene(sceneId)
}
```

### 8. 写入场景到 NFC 标签

```kotlin
lifecycleScope.launch {
    val result = NFCFocus.manager.writeSceneToTag(
        tagUid = tagUid,
        sceneId = sceneId
    )
    
    if (result.isSuccess) {
        Log.d(TAG, "场景已成功绑定到标签")
    } else {
        Log.e(TAG, "绑定失败: ${result.exceptionOrNull()?.message}")
    }
}
```

### 9. 权限管理

```kotlin
// 检查是否有使用情况统计权限
val hasPermission = NFCFocus.manager.hasUsageStatsPermission()
if (!hasPermission) {
    // 请求权限
    lifecycleScope.launch {
        val result = NFCFocus.manager.requestUsageStatsPermission(activity)
        if (result.isSuccess) {
            Log.d(TAG, "权限已授予")
        }
    }
}

// 检查所有必要权限
val allPermissionsGranted = NFCFocus.manager.hasRequiredPermissions()
```

### 11. UI 自定义（第二阶段新增）

#### 自定义覆盖窗口

```kotlin
// 实现自定义覆盖窗口提供者
class CustomAlertViewProvider : IAlertViewProvider {
    override fun createOverlayView(context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.my_custom_alert, null)
    }
    
    override fun bindData(view: View, alertData: AlertData) {
        // 自定义数据绑定逻辑
        view.findViewById<TextView>(R.id.title).text = "🚫 专注时间！"
        view.findViewById<TextView>(R.id.message).text = alertData.appName
    }
    
    override fun setActionListener(view: View, listener: AlertActionListener) {
        view.findViewById<Button>(R.id.btn_return).setOnClickListener {
            listener.onReturnToFocus()
        }
        view.findViewById<Button>(R.id.btn_exit).setOnClickListener {
            listener.onTemporaryExit(5)
        }
    }
}

// 注册自定义 UI
NFCFocus.manager.setAlertViewProvider(CustomAlertViewProvider())
```

#### 自定义通知

```kotlin
// 实现自定义通知提供者
class CustomNotificationProvider : INotificationProvider {
    override fun createNotificationChannels(context: Context) {
        // 创建自定义通知渠道
    }
    
    override fun showDistractionAlert(context: Context, alertData: AlertData) {
        // 自定义分心提醒通知
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("⚠️ ${alertData.appName}")
            .setContentText("建议返回专注")
            .setStyle(NotificationCompat.BigTextStyle())
            .build()
        
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }
    
    override fun showSessionNotification(context: Context, sessionData: SessionData) {
        // 自定义专注会话通知
    }
    
    override fun cancelAllNotifications(context: Context) {
        // 取消所有通知
    }
}

// 注册自定义通知
NFCFocus.manager.setNotificationProvider(CustomNotificationProvider())
```

### 12. 无障碍服务（第二阶段新增）

```kotlin
// 检查无障碍服务是否启用
val isEnabled = NFCFocus.manager.isAccessibilityServiceEnabled()
if (!isEnabled) {
    // 获取授权引导信息
    val guide = NFCFocus.manager.getAccessibilityAuthGuide()
    if (guide != null) {
        // 显示引导对话框
        showAccessibilityGuideDialog(guide)
    }
    
    // 请求无障碍权限
    lifecycleScope.launch {
        NFCFocus.manager.requestAccessibilityPermission(activity)
    }
}

// 检查悬浮窗权限
val hasOverlayPermission = NFCFocus.manager.hasOverlayPermission()
if (!hasOverlayPermission) {
    lifecycleScope.launch {
        NFCFocus.manager.requestOverlayPermission(activity)
    }
}

### 10. NFC 兼容性检查

```kotlin
// 检查 NFC 支持状态
val supportResult = NFCFocus.manager.checkNfcSupport()
when (supportResult) {
    is NfcSupportResult.Supported -> {
        Log.d(TAG, "NFC 完全支持")
    }
    is NfcSupportResult.SupportedWithWarning -> {
        Log.w(TAG, "NFC 支持但有警告: ${supportResult.message}")
    }
    is NfcSupportResult.Disabled -> {
        Log.w(TAG, "NFC 未启用")
        // 显示开启引导
        val guide = NFCFocus.manager.getNfcEnableGuide()
        showNfcEnableDialog(guide)
    }
    is NfcSupportResult.NoHardware -> {
        Log.e(TAG, "设备不支持 NFC")
    }
}
```

## 🔧 高级功能

### 专注模式类型

```kotlin
enum class FocusMode {
    GENTLE,   // 温和模式：仅监控和提醒，不强制阻止
    BALANCED, // 平衡模式：监控 + 提醒 + 轻度拦截（第二阶段）
    STRICT    // 严格模式：所有策略启用，强制阻止（第三阶段）
}
```

### 触发方式

```kotlin
enum class TriggerType {
    NFC,           // NFC 标签触发
    MANUAL,        // 手动触发
    SCHEDULED,     // 定时触发
    AUTOMATION     // 自动化触发
}
```

### 提醒方式

```kotlin
enum class AlertType {
    OVERLAY_DIALOG,  // 全屏覆盖对话框（第二阶段）
    NOTIFICATION,    // 通知栏提醒
    TOAST,           // Toast 提示
    VIBRATION        // 震动提醒
}
```

## 📱 主工程配置

### AndroidManifest.xml

确保主工程的 `AndroidManifest.xml` 包含以下权限声明：

```xml
<!-- NFC 权限 -->
<uses-permission android:name="android.permission.NFC" />
<uses-feature android:name="android.hardware.nfc" android:required="false" />

<!-- 振动权限（用于提醒） -->
<uses-permission android:name="android.permission.VIBRATE" />

<!-- 通知权限（Android 13+） -->
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />

<!-- 前台服务权限 -->
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_SPECIAL_USE" />

<!-- 使用统计权限（应用监控需要） -->
<uses-permission 
    android:name="android.permission.PACKAGE_USAGE_STATS" 
    tools:ignore="ProtectedPermissions" />

<!-- 悬浮窗权限（第二阶段使用） -->
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />

<!-- 开机自启动权限（第三阶段使用） -->
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
```

### ProGuard 混淆规则

如果使用代码混淆，请在 `proguard-rules.pro` 中添加：

```proguard
# ==================== NFC Focus Module ====================

# 保留所有公共 API
-keep class com.colin.nfc.focus.NFCFocus { *; }
-keep class com.colin.nfc.focus.api.** { *; }
-keep interface com.colin.nfc.focus.api.** { *; }

# 保留数据模型
-keep class com.colin.nfc.focus.data.model.** { *; }

# 保留数据库相关
-keep class com.colin.nfc.focus.data.local.** { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }

# 保留服务
-keep class com.colin.nfc.focus.service.** { *; }

# 保留 blocker 策略
-keep class com.colin.nfc.focus.blocker.** { *; }
-keep interface com.colin.nfc.focus.blocker.AppBlockingStrategy { *; }

# 保留枚举
-keepclassmembers enum com.colin.nfc.focus.** { *; }

# 保留 Gson 序列化字段
-keepclassmembers class com.colin.nfc.focus.** {
    @com.google.gson.annotations.SerializedName <fields>;
}

# 保留 Kotlin 协程相关
-keep class kotlinx.coroutines.** { *; }

# 忽略警告
-dontwarn com.colin.nfc.focus.**
-dontwarn kotlinx.coroutines.**
```

## 🏗️ 构建 AAR

### 方法一：Gradle 命令

```bash
./gradlew :nfc-focus:assembleRelease
```

生成的 AAR 文件位于：
```
nfc-focus/build/outputs/aar/nfc-focus-release.aar
```

### 方法二：发布到本地 Maven

在 `nfc-focus/build.gradle.kts` 中添加发布配置后：

```bash
./gradlew :nfc-focus:publishToMavenLocal
```

然后在主工程中使用：

```kotlin
repositories {
    mavenLocal()
}

dependencies {
    implementation("com.colin:nfc-focus:1.0.0")
}
```

## 📊 架构说明

```
nfc-focus/
├── api/                        # 对外 API 层
│   ├── NFCFocusManager.kt      # 核心接口定义
│   └── NFCFocusManagerImpl.kt  # 接口实现
├── data/                       # 数据层
│   ├── local/                  # 本地数据库
│   │   ├── NfcFocusDatabase.kt
│   │   ├── NfcTagDao.kt
│   │   ├── FocusSceneDao.kt
│   │   ├── UsageStatDao.kt
│   │   ├── FocusSessionDao.kt  # 会话 DAO
│   │   └── Converters.kt
│   └── model/                  # 数据模型
│       ├── NfcTag.kt
│       ├── FocusScene.kt
│       ├── UsageStat.kt
│       └── FocusSessionEntity.kt  # 会话实体
├── nfc/                        # NFC 功能模块
│   ├── NfcManager.kt           # NFC 读写管理器
│   ├── NfcTagHelper.kt         # 标签管理工具
│   └── NfcCompatibilityChecker.kt  # 兼容性检测
├── blocker/                    # 应用屏蔽策略
│   ├── AppBlockingStrategy.kt  # 策略接口
│   ├── AppBlockerManager.kt    # 策略管理器
│   ├── BlockingModels.kt       # 数据模型
│   ├── UsageStatsStrategy.kt   # 监控策略
│   └── AccessibilityStrategy.kt # 无障碍拦截策略（第二阶段）
├── ui/                         # UI 组件（第二阶段）
│   ├── provider/
│   │   ├── IAlertViewProvider.kt      # 覆盖窗口提供者接口
│   │   ├── INotificationProvider.kt   # 通知提供者接口
│   │   ├── DefaultAlertViewProvider.kt    # 默认实现
│   │   └── DefaultNotificationProvider.kt # 默认实现
│   └── model/
│       ├── AlertData.kt        # 提醒数据
│       └── SessionData.kt      # 会话数据
├── service/                    # 后台服务
│   ├── FocusBlockingService.kt # 前台服务
│   └── FocusAccessibilityService.kt # 无障碍服务（第二阶段）
├── worker/                     # WorkManager（第二阶段）
│   ├── FocusKeepAliveWorker.kt # 保活任务
│   └── FocusWorkManager.kt     # Worker 管理器
├── receiver/                   # 广播接收器（第二阶段）
│   └── SessionRecoveryReceiver.kt # 开机自启动
├── manager/                    # 辅助管理器（第二阶段）
│   └── AlertFrequencyManager.kt # 智能提醒管理
└── NFCFocus.kt                 # 单例入口
```

## ⚠️ 注意事项

### 1. 初始化要求
**必须在 Application 中初始化**：在使用任何功能前调用 `NFCFocus.initialize(context)`

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NFCFocus.initialize(this) // ← 必须首先调用
    }
}
```

### 2. 权限要求
部分功能需要特殊权限，需用户手动授权：
- `PACKAGE_USAGE_STATS`：应用监控必需
- `FOREGROUND_SERVICE`：前台服务必需
- `SYSTEM_ALERT_WINDOW`：覆盖窗口（第二阶段）

### 3. 线程安全
所有 suspend 函数应在协程作用域中调用：

```kotlin
lifecycleScope.launch {
    val result = NFCFocus.manager.startFocus(sceneId)
    // 处理结果
}
```

### 4. 生命周期管理
建议在 ViewModel 中持有监听器引用，避免内存泄漏：

```kotlin
class MainViewModel : ViewModel() {
    private val focusListener = object : FocusStateListener {
        override fun onFocusStarted(session: FocusSession) {
            // 更新 LiveData
        }
        // ...
    }
    
    init {
        NFCFocus.manager.setFocusStateListener(focusListener)
    }
    
    override fun onCleared() {
        NFCFocus.manager.setFocusStateListener(null)
    }
}
```

### 5. 服务恢复机制
当前实现的服务恢复机制（第一阶段 + 第二阶段）：
- ✅ START_STICKY：服务被杀后自动重启
- ✅ 数据库持久化：重启后从数据库恢复会话
- ✅ 超时检测：超过 24 小时自动结束会话
- ✅ WorkManager 保活：每 15 分钟检查会话状态
- ✅ 开机自启动：设备重启后自动恢复活跃会话

## 🐛 常见问题

### Q1: 如何调试 NFC 功能？

**A:** 确保设备支持 NFC 并已启用，使用物理 NFC 标签进行测试。模拟器不支持 NFC。

```kotlin
// 检查 NFC 是否可用
val support = NFCFocus.manager.checkNfcSupport()
if (support is NfcSupportResult.Disabled) {
    // 引导用户开启 NFC
    val guide = NFCFocus.manager.getNfcEnableGuide()
    showGuide(guide)
}
```

### Q2: 应用监控不生效？

**A:** 需要授予 `PACKAGE_USAGE_STATS` 权限：

```kotlin
lifecycleScope.launch {
    val result = NFCFocus.manager.requestUsageStatsPermission(activity)
    if (result.isSuccess) {
        Log.d(TAG, "权限已授予")
    }
}
```

### Q3: 服务被系统杀死后如何恢复？

**A:** 当前使用 START_STICKY 模式，服务被杀后会自动重启并从数据库恢复会话。如果需要更强的恢复能力，等待第三阶段的 WorkManager 实现。

### Q4: 如何自定义通知样式？

**A:** 当前版本使用默认通知样式，后续版本将支持自定义。可以在 `FocusBlockingService.createNotification()` 中修改。

### Q5: 国内手机厂商兼容性问题？

**A:** 模块内置了针对华为、小米、OPPO、vivo 等厂商的兼容性检测：

```kotlin
val support = NFCFocus.manager.checkNfcSupport()
when (support) {
    is NfcSupportResult.SupportedWithWarning -> {
        // 显示厂商特定的配置指引
        Log.w(TAG, support.message)
    }
    else -> {}
}
```

## 📝 更新日志

### v1.0.0 (2026-06-26)
**第一阶段完成**
- ✅ 核心 API 框架
- ✅ 数据层实现（Room 数据库）
- ✅ NFC 功能完整实现
- ✅ 应用监控策略（UsageStats）
- ✅ 前台服务 + 会话恢复
- ✅ 权限管理系统
- ✅ 厂商兼容性检测

**待实现**
- ⏳ 覆盖窗口提醒（第二阶段）
- ⏳ 无障碍拦截（第三阶段）
- ⏳ WorkManager 定期检查（第三阶段）

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

## 📄 许可证

MIT License

---

**技术支持**: 如有问题请提交 Issue 或联系开发团队。
