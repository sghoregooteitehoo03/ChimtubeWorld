plugins {
    alias(libs.plugins.android.library.convention)
}

android {
    namespace = "com.sghore.chimtubeworld.core.domain"
}

dependencies {
    api(project(":core:model"))

    api(libs.androidx.paging.common)
    api(libs.kotlinx.coroutines.core)
}
