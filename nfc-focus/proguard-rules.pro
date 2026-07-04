# ==================== NFC Focus Module ProGuard Rules ====================

# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.

# ==================== 保留公共 API ====================

# 保留单例入口
-keep class com.colin.nfc.focus.NFCFocus { *; }

# 保留所有 API 接口和实现
-keep class com.colin.nfc.focus.api.** { *; }
-keep interface com.colin.nfc.focus.api.** { *; }

# ==================== 保留数据模型 ====================

# 保留所有数据模型类
-keep class com.colin.nfc.focus.data.model.** { *; }

# 保留枚举类型
-keepclassmembers enum com.colin.nfc.focus.data.model.** { *; }

# ==================== 保留数据库相关 ====================

# 保留 Room 数据库
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Database class * { *; }

# 保留 DAO 接口
-keep @androidx.room.Dao class * { *; }
-keep interface com.colin.nfc.focus.data.local.** { *; }

# 保留 Entity 实体类
-keep @androidx.room.Entity class * { *; }
-keepclassmembers @androidx.room.Entity class * { *; }

# 保留 TypeConverter
-keep @androidx.room.TypeConverter class * { *; }
-keepclassmembers @androidx.room.TypeConverter class * { *; }

# 忽略 Room 警告
-dontwarn androidx.room.paging.**

# ==================== 保留服务 ====================

# 保留前台服务
-keep class com.colin.nfc.focus.service.FocusBlockingService { *; }
-keep class com.colin.nfc.focus.service.** { *; }

# ==================== 保留应用屏蔽策略 ====================

# 保留策略接口
-keep interface com.colin.nfc.focus.blocker.AppBlockingStrategy { *; }

# 保留策略实现
-keep class com.colin.nfc.focus.blocker.** { *; }

# 保留管理器
-keep class com.colin.nfc.focus.blocker.AppBlockerManager { *; }

# 保留枚举
-keepclassmembers enum com.colin.nfc.focus.blocker.** { *; }

# ==================== 保留 NFC 模块 ====================

# 保留 NFC 相关类
-keep class com.colin.nfc.focus.nfc.** { *; }

# 保留枚举
-keepclassmembers enum com.colin.nfc.focus.nfc.** { *; }

# ==================== 保留 Gson 序列化 ====================

# 如果使用 Gson 进行 JSON 序列化，保留字段名
-keepclassmembers class com.colin.nfc.focus.** {
    @com.google.gson.annotations.SerializedName <fields>;
}

# 保留 Gson 类型适配器
-keep class * implements com.google.gson.TypeAdapter { *; }
-keep class * implements com.google.gson.TypeAdapterFactory { *; }

# ==================== 保留 Kotlin 协程 ====================

# 保留 Kotlin 协程相关
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

# 保留 suspend 函数
-keepclassmembers class * {
    kotlin.coroutines.Continuation <methods>(kotlin.coroutines.Continuation);
}

# ==================== 保留 Android 组件 ====================

# 保留 Activity、Service、Receiver、Provider
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# ==================== 保留 Parcelable ====================

-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# ==================== 保留 Serializable ====================

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ==================== 保留日志（调试用）====================

# 如果需要保留 Log 调用用于调试，取消以下注释
# -assumenosideeffects class android.util.Log {
#     public static *** d(...);
#     public static *** v(...);
#     public static *** i(...);
#     public static *** w(...);
#     public static *** e(...);
# }

# ==================== 优化选项 ====================

# 移除未使用的代码
-shrink

# 优化字节码
-optimize

# 预验证
-preverify

# ==================== 通用规则 ====================

# 忽略第三方库警告
-dontwarn com.google.gson.**
-dontwarn org.jetbrains.annotations.**
-dontwarn androidx.annotation.**

# 保留注解
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses

# 保留行号（便于调试）
-keepattributes SourceFile,LineNumberTable

# ==================== 测试相关 ====================

# 如果是 release 版本，可以移除测试代码
-assumenosideeffects class junit.framework.** { *; }
-assumenosideeffects class junit.runner.** { *; }
-assumenosideeffects class org.junit.** { *; }
-assumenosideeffects class org.hamcrest.** { *; }
