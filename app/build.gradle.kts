import java.util.Properties

plugins {
    alias(libs.plugins.android.application.convention)
    alias(libs.plugins.android.application.compose)
    alias(libs.plugins.android.hilt)
    alias(libs.plugins.kotlin.serialization.convention)
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
    implementation(project(":feature:webtoon:api"))
    implementation(project(":feature:webtoon:impl"))
    implementation(project(":feature:store:api"))
    implementation(project(":feature:store:impl"))
    implementation(project(":feature:cafe:api"))
    implementation(project(":feature:cafe:impl"))
    implementation(project(":feature:chzzk:api"))
    implementation(project(":feature:chzzk:impl"))

    implementation(project(":core:designsystem"))
    implementation(project(":core:ui"))
    implementation(project(":core:navigation"))
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:domain"))
    implementation(project(":core:data"))

    // AndroidX Core
    implementation(libs.androidx.core.ktx)

    // Compose
    implementation(libs.androidx.compose.material.icons.extended)

    // Hilt
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)

    // Lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Kotlin Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Navigation
    implementation(libs.androidx.navigation3.ui)

    // Paging
    implementation(libs.androidx.paging.compose)

    // Image
    implementation(libs.coil.compose)

    // UI Utilities
    implementation(libs.androidx.appcompat)
    debugImplementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.constraintlayout.compose)
    debugImplementation(libs.androidx.customview.poolingcontainer)
    implementation(libs.compose.shimmer)
    implementation(libs.google.material)

}
