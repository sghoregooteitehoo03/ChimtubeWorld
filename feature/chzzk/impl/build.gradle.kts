plugins {
    alias(libs.plugins.android.feature.impl)
}

android {
    namespace = "com.sghore.chimtubeworld.feature.chzzk.impl"
}

dependencies {
    implementation(project(":feature:chzzk:api"))
    implementation(libs.coil.compose)
}
