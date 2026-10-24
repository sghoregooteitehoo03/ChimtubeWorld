plugins {
    `kotlin-dsl`
}

group = "com.sghore.chimtubeworld.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(libs.android.gradle.plugin)
    implementation(libs.hilt.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.kotlin.serialization.gradle.plugin)
    implementation(libs.ksp.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "sg.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidApplicationCompose") {
            id = "sg.android.application.compose"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }
        register("androidLibrary") {
            id = "sg.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = "sg.android.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register("androidHilt") {
            id = "sg.android.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }
        register("androidRoom") {
            id = "sg.android.room"
            implementationClass = "AndroidRoomConventionPlugin"
        }
        register("androidFeatureApi") {
            id = "sg.android.feature.api"
            implementationClass = "AndroidFeatureApiConventionPlugin"
        }
        register("androidFeatureImpl") {
            id = "sg.android.feature.impl"
            implementationClass = "AndroidFeatureImplConventionPlugin"
        }
        register("kotlinSerialization") {
            id = "sg.kotlin.serialization"
            implementationClass = "KotlinSerializationConventionPlugin"
        }
    }
}
