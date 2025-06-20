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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    compileOnly(project(":Utils"))
    compileOnly(libs.bundles.androidCommon)
    compileOnly(libs.bundles.squareup)
    compileOnly(libs.androidx.lifecycle.viewmodel.ktx)
}

//publishing {
//    publications {
//        register<MavenPublication>("release") {
//            groupId = "com.colin.library.android.kotlin"
//            artifactId = "Network"
//            version = "0.3.0"
//
//            afterEvaluate {
//                from(components["release"])
//            }
//        }
//    }
//    repositories {
//        maven {
//            name = "publish"
//            url = uri(layout.buildDirectory.dir("publish"))
//        }
//    }
//}

//tasks.register<Zip>("generatePublish") {
//    val publishTask = tasks.named(
//        "publishReleasePublicationToMavenLocalRepository", PublishToMavenRepository::class.java
//    )
//    from(publishTask.map { it.repository.url })
//    into("Utils")
//    archiveFileName.set("Utils.zip")
//}