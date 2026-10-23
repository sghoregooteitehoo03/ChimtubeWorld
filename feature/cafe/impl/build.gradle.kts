plugins {
    alias(libs.plugins.android.feature.impl)
}

android {
    namespace = "com.sghore.chimtubeworld.feature.cafe.impl"
}

dependencies {
    implementation(project(":feature:cafe:api"))
    implementation(libs.androidx.paging.compose)
    implementation(libs.toolbar.compose)
    implementation(libs.coil.compose)
}
