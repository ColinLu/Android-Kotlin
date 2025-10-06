plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.colin.library.android.widget.video"
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
            // 可以在这里添加更多配置选项
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {
    implementation(libs.bundles.androidCommon)
//    implementation(libs.media.ui)
    compileOnly(libs.bundles.media)
    compileOnly(project(":Utils"))
}