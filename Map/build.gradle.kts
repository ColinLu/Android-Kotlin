plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.colin.library.android.map"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

}

dependencies {
    compileOnly(project(":Base"))
    compileOnly(libs.androidx.core.ktx)
    compileOnly(libs.androidx.appcompat)
//    compileOnly(libs.material)
//    compileOnly(libs.androidx.lifecycle.livedata.ktx)
    compileOnly(libs.androidx.lifecycle.viewmodel.ktx)
    //TomTom
    compileOnly(libs.locationProvider)
    compileOnly(libs.locationSimulation)
    compileOnly(libs.locationMapmatched)
    compileOnly(libs.mapsDisplay)
    compileOnly(libs.navigationOnline)
    compileOnly(libs.navigationUi)
    compileOnly(libs.routePlannerOnline)
    compileOnly(libs.routeReplannerOnline)
    // Android dependencies.
    compileOnly(libs.bundles.androidCommon)

    testCompileOnly(libs.junit)
    androidTestCompileOnly(libs.androidx.junit)
    androidTestCompileOnly(libs.androidx.espresso.core)
}