plugins {
    alias(libs.plugins.android.library.convention)
    alias(libs.plugins.android.hilt)
}

android {
    namespace = "com.sghore.chimtubeworld.core.data"
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))

    implementation(libs.androidx.paging.common)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.jsoup)
}
