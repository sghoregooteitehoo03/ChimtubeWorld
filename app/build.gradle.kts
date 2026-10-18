import java.util.Properties

plugins {
    alias(libs.plugins.android.application.convention)
    alias(libs.plugins.android.application.compose)
    alias(libs.plugins.android.hilt)
    alias(libs.plugins.google.services)
}

val properties = Properties().apply {
    rootProject.file("local.properties").inputStream().use { load(it) }
}

android {
    defaultConfig {
        applicationId = "com.sghore.chimtubeworld"
        versionCode = 10
        versionName = "1.4.5"

        buildConfigField("String", "YOUTUBE_API_KEY", properties.getProperty("youtube_api_key"))
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    buildFeatures {
        buildConfig = true
    }
    namespace = "com.sghore.chimtubeworld"
}

dependencies {
    // AndroidX Core
    implementation(libs.androidx.core.ktx)

    // Compose
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.material.icons.extended)

    // Hilt
    implementation(libs.androidx.hilt.navigation.compose)

    // Lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Kotlin Coroutines
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.play.services)

    // Navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.ui.ktx)

    // Paging
    implementation(libs.androidx.paging.compose)
    implementation(libs.androidx.paging.runtime.ktx)

    // Network
    implementation(libs.okhttp)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)

    // Room: schemaLocation을 추가하는 convention은 아직 적용하지 않는다.
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)

    // Firebase
    implementation(libs.firebase.firestore.ktx)

    // Image
    implementation(libs.coil.compose)

    // UI Utilities
    implementation(libs.accompanist.insets.ui)
    implementation(libs.androidx.appcompat)
    debugImplementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.constraintlayout.compose)
    debugImplementation(libs.androidx.customview.poolingcontainer)
    implementation(libs.compose.shimmer)
    implementation(libs.google.material)
    implementation(libs.toolbar.compose)

    // Web/Parsing
    implementation(libs.androidx.browser)
    implementation(libs.jsoup)
}
