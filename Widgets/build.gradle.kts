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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
    lint {
        disable += "InvalidLintRule"
    }
}

dependencies {
    implementation(libs.bundles.androidCommon)
    compileOnly(libs.androidx.lifecycle.viewmodel.ktx)
    compileOnly(libs.tbssdk)
    compileOnly(libs.gson)
    compileOnly(project(":Utils"))
}

//publishing {
//    publications {
//        register<MavenPublication>("release") {
//            groupId = "com.colin.library.android.kotlin"
//            artifactId = "Widgets"
//            version = "0.3.0"
//
//            afterEvaluate {
//                from(components["release"])
//            }
//        }
//    }
//}

