plugins {
    alias(libs.plugins.android.feature.impl)
}

android {
    namespace = "com.sghore.chimtubeworld.feature.store.impl"
}

dependencies {
    implementation(project(":feature:store:api"))
}
