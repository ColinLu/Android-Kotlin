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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
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
    publishing {
        singleVariant("release") {
            // 可以在这里添加更多配置选项
            withSourcesJar()
            withJavadocJar()
        }
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