import java.util.Properties

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
        freeCompilerArgs += listOf(
            "-opt-in=kotlin.RequiresOptIn",
            "-Xjvm-default=all-compatibility"
        )
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
        maven {
            val properties = Properties().apply {
                load(project.rootProject.file("local.properties").inputStream())
            }
            url = uri(
                properties.getProperty("gitee.url")
                    ?: "https://gitee.com/ColinTeam/maven/raw/master/repository"
            )
            credentials {
                username = properties.getProperty("gitee.user") ?: ""
                password = properties.getProperty("gitee.pwd") ?: ""
            }
        }
    }
}

//apply(from = "publish_utils.gradle.kts")

