plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("maven-publish")
}

android {
    namespace = "com.colin.library.android.network"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    lint {
        checkTestSources = false //禁用 UnitTest Lint 检查
        abortOnError = false  // 遇到 lint 错误时不中断构建
        warningsAsErrors = false
        checkReleaseBuilds = false  // 不对 release 构建进行 lint 检查
    }
}

dependencies {
    compileOnly(project(":Utils"))
    compileOnly(libs.bundles.androidCommon)
    compileOnly(libs.bundles.squareup)
    compileOnly(libs.androidx.lifecycle.viewmodel.ktx)
}
