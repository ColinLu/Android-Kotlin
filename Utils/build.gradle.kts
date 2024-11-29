plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("maven-publish")
}

android {
    namespace = "com.colin.library.android.utils"
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
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }
    buildFeatures {
        viewBinding = true
    }

}

dependencies {
    compileOnly(libs.bundles.androidCommon)
}

//afterEvaluate {
//    publishing {
//        publications {
//            create<MavenPublication>("maven") {
//                groupId = "com.colin.library.android"       // 从 version catalog 获取 groupId
//                artifactId = "Utils"                        // 从 version catalog 获取 artifactId
//                version = "0.3.1"                           // 从 version catalog 获取 version
//                from(components["release"])                 // 发布 release 组件
//            }
//        }
//    }
//}