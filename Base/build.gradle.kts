plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
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
    compileOnly(libs.androidx.lifecycle.viewmodel.ktx)
    testCompileOnly(libs.junit)
    androidTestCompileOnly(libs.androidx.junit)
    androidTestCompileOnly(libs.androidx.espresso.core)
}