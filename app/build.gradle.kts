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
            jniLibs.srcDirs("libs")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        allWarningsAsErrors = true
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        aidl = true
    }
    lint {
        checkTestSources = false //禁用 UnitTest Lint 检查
        abortOnError = false  // 遇到 lint 错误时不中断构建
        warningsAsErrors = false
        checkReleaseBuilds = false  // 不对 release 构建进行 lint 检查
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar", "*.jar"))))

    implementation(project(":Utils"))
    implementation(project(":Widgets"))
    implementation(project(":Network"))

    implementation(libs.bundles.androidCommon)
    implementation(libs.bundles.androidWidgets)
    implementation(libs.bundles.androidNavigation)
    implementation(libs.bundles.androidLifecycle)
    implementation(libs.bundles.squareup)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
