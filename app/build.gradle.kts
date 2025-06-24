import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
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
        create("sign") {
            storeFile = file("${rootDir.absolutePath}/config/app.jks")
            keyAlias = "colinapp"
            storePassword = "ludapeng31"
            keyPassword = "ludapeng31"
        }
    }

    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("sign")
            isMinifyEnabled = false
        }
        getByName("release") {
            signingConfig = signingConfigs.getByName("sign")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    // 指定AIDL源集目录
    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("libs")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        allWarningsAsErrors = true
        jvmTarget = "21"
    }
    buildFeatures {
        viewBinding = true
        aidl = true
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
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar", "*.jar"))))

    implementation(project(":Utils"))
    implementation(project(":Widgets"))
    implementation(project(":Network"))
//    implementation("com.gitee.colin_lu:Android-Kotlin:v0.0.2")
    implementation(libs.tbssdk)
    implementation(libs.bundles.androidCommon)
    implementation(libs.bundles.androidWidgets)
    implementation(libs.bundles.androidNavigation)
    implementation(libs.bundles.androidLifecycle)
    implementation(libs.bundles.squareup)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
