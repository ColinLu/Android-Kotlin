plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("maven-publish")
}

android {
    namespace = "com.colin.library.android.widget"
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
    buildFeatures {
        viewBinding = true
        aidl = true
    }
}

kotlin {
    jvmToolchain(libs.versions.versionJava.get().toInt())
    compilerOptions {
        freeCompilerArgs.addAll(
            "-opt-in=kotlin.RequiresOptIn",
            "-Xjvm-default=all-compatibility"
        )
    }
}

dependencies {
    implementation(libs.bundles.androidCommon)
    compileOnly(libs.androidx.lifecycle.viewmodel.ktx)
    compileOnly(libs.tbssdk)
    compileOnly(libs.gson)
    compileOnly(project(":Utils"))
}

// 应用统一发布配置
apply(from = rootProject.file("publish-config.gradle.kts"))
