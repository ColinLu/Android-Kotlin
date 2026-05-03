# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# ========== 基础配置 ==========
# 优化
-dontshrink
-ignorewarnings
-allowaccessmodification

# ========== Kotlin 相关 ==========
# 保持 Kotlin 元数据
-keepattributes *Annotation*, RuntimeVisibleAnnotations, RuntimeInvisibleAnnotations
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }

# Kotlin 协程
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** { *; }

# ========== Gson 相关 ==========
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# 保持模型类（根据实际包名调整）
-keep class com.colin.android.demo.kotlin.model.** { *; }

# ========== AndroidX 和 Jetpack ==========
-keep class androidx.lifecycle.** { *; }
-keep class androidx.navigation.** { *; }
-keep class androidx.fragment.app.** { *; }

# ViewModel
-keepclassmembers,allowobfuscation class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# LiveData
-keepclassmembers class * extends androidx.lifecycle.LiveData {
    <init>(...);
}

# ========== Retrofit & OkHttp ==========
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes InnerClasses
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**

# ========== TBS 腾讯浏览服务 ==========
-keep class com.tencent.smtt.** { *; }
-dontwarn com.tencent.smtt.**

# ========== AIDL 相关 ==========
# 保持 AIDL 接口
-keep class * extends android.os.Binder { *; }
-keep interface * extends android.os.IInterface { *; }
-keep class com.colin.android.demo.kotlin.service.** { *; }

# ========== 自定义组件 ==========
# 保持自定义 View
-keep class com.colin.library.android.widget.** { *; }
-keep class com.colin.library.android.utils.** { *; }
-keep class com.colin.library.android.network.** { *; }

# 保持 R 类
-keep class **.R$* {
    public static <fields>;
}

# ========== 序列化 ==========
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
