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
            // 可以在这里添加更多配置选项
            withSourcesJar()
            withJavadocJar()
        }
    }
    buildFeatures {
        viewBinding = true
        aidl = true
    }
}

dependencies {
    implementation(libs.bundles.androidCommon)
    compileOnly(libs.androidx.lifecycle.viewmodel.ktx)
    compileOnly(libs.tbssdk)
    compileOnly(libs.gson)
    compileOnly(project(":Utils"))
}

publishing {
    publications {
        create<MavenPublication>("release") {
            groupId = libs.versions.publishGroup.get()
            artifactId = libs.versions.publishWidgets.get()
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
