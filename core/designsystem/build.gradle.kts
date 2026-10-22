plugins {
    alias(libs.plugins.android.library.convention)
    alias(libs.plugins.android.library.compose)
}

android {
    namespace = "com.sghore.chimtubeworld.core.designsystem"
}

dependencies {
    implementation(libs.androidx.compose.material.icons.extended)
    api(libs.androidx.compose.material3)
}
