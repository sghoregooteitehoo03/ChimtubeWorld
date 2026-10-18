plugins {
    alias(libs.plugins.android.library.convention)
    alias(libs.plugins.android.hilt)
}

android {
    namespace = "com.sghore.chimtubeworld.core.common"
}

dependencies {
    implementation(libs.javax.inject)
    implementation(libs.kotlinx.coroutines.android)
}
