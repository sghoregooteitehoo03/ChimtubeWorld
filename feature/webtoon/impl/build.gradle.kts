plugins {
    alias(libs.plugins.android.feature.impl)
}

android {
    namespace = "com.sghore.chimtubeworld.feature.webtoon.impl"
}

dependencies {
    implementation(project(":feature:webtoon:api"))
}
