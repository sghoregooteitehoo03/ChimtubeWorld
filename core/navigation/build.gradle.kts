plugins {
    alias(libs.plugins.android.library.convention)
    alias(libs.plugins.android.library.compose)
}

android {
    namespace = "com.sghore.chimtubeworld.core.navigation"
}

dependencies {
    api(libs.androidx.navigation3.runtime)

    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
}
