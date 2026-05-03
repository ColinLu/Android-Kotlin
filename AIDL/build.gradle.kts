plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
}

android {
    namespace = "com.colin.android.demo.kotlin.aidl"
    compileSdk { version = release(libs.versions.compileSdk.get().toInt()) }
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
    kotlinOptions {
        jvmTarget = "21"
        freeCompilerArgs += listOf(
            "-opt-in=kotlin.RequiresOptIn",
            "-Xjvm-default=all-compatibility"
        )
    }

    buildFeatures { aidl = true }

    // 确保所有类都被包含
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}


dependencies {
    compileOnly(libs.bundles.androidCommon)
}

//AIDL构建成功的aar包
val copyAar by tasks.registering {
    dependsOn("compileReleaseSources")

    doLast {
        // 定义多个目标目录列表
        val targetDirs = listOf(
            file("${rootDir}/libs"),
            //指定客服端工程的libs目录
            file("/Users/Colin/Projects/Code/DemoAidlClient/app/libs")
        )

        targetDirs.forEach { targetDir ->
            // 添加日志输出
            println("Copying AAR to: ${targetDir.absolutePath}")

            copy {
                from("build/outputs/aar/AIDL-release.aar")
                rename { "aidl.aar" }
                into(targetDir)

                // 处理重复文件策略
                duplicatesStrategy = DuplicatesStrategy.INCLUDE
            }

            println("Successfully copied AAR to: ${targetDir.absolutePath}")
        }
    }
}