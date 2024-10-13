plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("maven-publish")
}

android {
    namespace = "com.colin.library.android.base"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }

}

dependencies {
    compileOnly(project(":Utils"))
    compileOnly(libs.androidx.core.ktx)
    compileOnly(libs.androidx.appcompat)
    compileOnly(libs.androidx.recyclerview)
//    compileOnly(libs.material)
//    compileOnly(libs.androidx.lifecycle.livedata.ktx)
//    compileOnly(libs.androidx.lifecycle.viewmodel.ktx)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = "com.colin.library.android"       // 从 version catalog 获取 groupId
                artifactId = "Base"                        // 从 version catalog 获取 artifactId
                version = "0.3.1"                           // 从 version catalog 获取 version
                from(components["release"])                 // 发布 release 组件
            }
        }
    }
}