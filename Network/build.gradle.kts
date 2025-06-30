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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    compileOnly(project(":Utils"))
    compileOnly(libs.bundles.androidCommon)
    compileOnly(libs.bundles.squareup)
    compileOnly(libs.androidx.lifecycle.viewmodel.ktx)
}
publishing {
    publications {
        create<MavenPublication>("release") {
            groupId = libs.versions.publishGroup.get()
            artifactId = libs.versions.publishNetwork.get()
            version = libs.versions.versionName.get()
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