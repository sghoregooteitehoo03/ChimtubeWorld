import java.util.Properties

plugins {
    alias(libs.plugins.android.library.convention)
    alias(libs.plugins.android.hilt)
}

val properties = Properties().apply {
    rootProject.file("local.properties").inputStream().use { load(it) }
}

android {
    namespace = "com.sghore.chimtubeworld.core.network"

    defaultConfig {
        buildConfigField("String", "YOUTUBE_API_KEY", properties.getProperty("youtube_api_key"))
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.okhttp)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
}
