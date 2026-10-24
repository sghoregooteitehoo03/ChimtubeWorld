plugins {
    alias(libs.plugins.android.library.convention)
    alias(libs.plugins.android.library.compose)
}

android {
    namespace = "com.sghore.chimtubeworld.core.ui"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.browser)

    implementation(project(":core:designsystem"))
    implementation(project(":core:model"))

    api(libs.androidx.lifecycle.viewmodel.ktx)
    api(libs.androidx.paging.compose)
    api(libs.kotlinx.coroutines.android)
}
