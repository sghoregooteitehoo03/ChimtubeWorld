plugins {
    alias(libs.plugins.android.feature.impl)
}

android {
    namespace = "com.sghore.chimtubeworld.feature.youtube.impl"
}

dependencies {
    implementation(project(":feature:youtube:api"))
    implementation(project(":feature:bookmark:api"))
    implementation(libs.androidx.paging.compose)
    implementation(libs.compose.shimmer)
    implementation(libs.coil.compose)
    implementation(libs.androidx.compose.material.icons.extended)
}
