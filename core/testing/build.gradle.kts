plugins {
    alias(libs.plugins.android.library.convention)
}

android {
    namespace = "com.sghore.chimtubeworld.core.testing"
}

dependencies {
    api(project(":core:domain"))

    api(libs.junit)
    api(libs.kotlinx.coroutines.test)

    testImplementation(libs.androidx.paging.testing)
}
