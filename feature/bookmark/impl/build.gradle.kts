plugins {
    alias(libs.plugins.android.feature.impl)
}

android {
    namespace = "com.sghore.chimtubeworld.feature.bookmark.impl"
}

dependencies {
    implementation(project(":feature:bookmark:api"))
    implementation(libs.coil.compose)
    implementation(libs.androidx.compose.material.icons.extended)
}
