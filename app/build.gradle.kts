import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = libs.versions.applicationID.get()
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = libs.versions.applicationID.get()
        minSdk = libs.versions.minSdk.get().toInt()
        //noinspection OldTargetApi
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = file("${rootDir.absolutePath}/config/app.jks")
            keyAlias = "colinapp"
            storePassword = "ludapeng31"
            keyPassword = "ludapeng31"
        }
    }

    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
        }
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    // 指定AIDL源集目录
    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("${rootDir}/libs")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        viewBinding = true
        compose = true
    }

    android.applicationVariants.all {
        val appName = rootProject.name
        val buildType = this.buildType.name
        outputs.all {
            if (this is com.android.build.gradle.internal.api.ApkVariantOutputImpl) {
                val date = SimpleDateFormat("yy-MM-dd-HH_mm_ss", Locale.CHINA).format(Date())
                val version = android.defaultConfig.versionName ?: "unknown"
                val fileName = "${appName}_${buildType}_${version}_${date}.apk"
                println("build apk:$${fileName}")
                this.outputFileName = fileName
            }
        }
    }

    publishing {
        singleVariant("release") {
            // 可以在这里添加更多配置选项
            withSourcesJar()
            withJavadocJar()
        }
    }
}

kotlin {
    jvmToolchain(libs.versions.versionJava.get().toInt())
    compilerOptions {
        freeCompilerArgs.addAll(
            "-opt-in=kotlin.RequiresOptIn", "-Xjvm-default=all-compatibility"
        )
    }
}

dependencies {
    implementation(
        fileTree(
            mapOf(
                "dir" to "${rootDir}/libs", "include" to listOf("*.aar", "*.jar")
            )
        )
    )

    implementation(project(":Utils"))
    implementation(project(":nfc-focus"))
    implementation(project(":Widgets"))
    implementation(project(":Network"))
    implementation(project(":Video"))
    implementation(libs.tbssdk)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.bundles.androidCommon)
    implementation(libs.bundles.androidWidgets)
    implementation(libs.bundles.androidNavigation)
    implementation(libs.bundles.androidLifecycle)
    implementation(libs.bundles.squareup)
    implementation(libs.squareup.okhttp)
    implementation(libs.repeatmanager)
    implementation(libs.bundles.media)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
