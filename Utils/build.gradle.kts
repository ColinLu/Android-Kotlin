plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
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
    }
}

dependencies {
    compileOnly(libs.bundles.androidCommon)
    compileOnly(libs.gson)
}

publishing {
    publications {
        create<MavenPublication>("release") {
            groupId = libs.versions.publishGroup.get()
            artifactId = libs.versions.publishUtils.get()
            version = libs.versions.publishVersion.get()
            afterEvaluate {
                from(components["release"])
            }
        }
    }
    publishing.repositories {
        maven {
            url = uri("/Users/Colin/Projects/Maven/Repository")
        }
    }
}

//apply(from = "publish_utils.gradle.kts")

