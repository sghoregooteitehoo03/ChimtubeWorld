import java.util.Properties

plugins {
    alias(libs.plugins.android.application.convention)
    alias(libs.plugins.android.application.compose)
    alias(libs.plugins.android.hilt)
    alias(libs.plugins.kotlin.serialization.convention)
    alias(libs.plugins.google.services)
}

// 릴리스 서명 정보는 저장소에 두지 않고 로컬 local.properties에서 읽는다.
val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        file.reader(Charsets.UTF_8).use(::load)
    }
}
val releaseStoreFile: String? = localProperties.getProperty("release_store_file")

android {
    defaultConfig {
        applicationId = "com.sghore.chimtubeworld"
        versionCode = 11
        versionName = "1.5.0"
    }

    signingConfigs {
        // 서명 키가 없는 환경에서도 빌드할 수 있도록 키 경로가 있을 때만 release 서명을 만든다.
        if (releaseStoreFile != null) {
            create("release") {
                storeFile = rootProject.file(releaseStoreFile)
                storePassword = localProperties.getProperty("release_store_password")
                keyAlias = localProperties.getProperty("release_key_alias")
                keyPassword = localProperties.getProperty("release_key_password")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.findByName("release")
        }
    }
    buildFeatures {
        buildConfig = false
    }
    namespace = "com.sghore.chimtubeworld"
}

dependencies {
    implementation(project(":feature:youtube:api"))
    implementation(project(":feature:youtube:impl"))
    implementation(project(":feature:bookmark:api"))
    implementation(project(":feature:bookmark:impl"))

    implementation(project(":feature:webtoon:api"))
    implementation(project(":feature:webtoon:impl"))
    implementation(project(":feature:store:api"))
    implementation(project(":feature:store:impl"))
    implementation(project(":feature:cafe:api"))
    implementation(project(":feature:cafe:impl"))
    implementation(project(":feature:chzzk:api"))
    implementation(project(":feature:chzzk:impl"))

    implementation(project(":core:designsystem"))
    implementation(project(":core:ui"))
    implementation(project(":core:navigation"))
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:domain"))
    implementation(project(":core:data"))

    // AndroidX Core
    implementation(libs.androidx.core.ktx)

    // Kotlin Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Navigation
    implementation(libs.androidx.navigation3.ui)

    // UI Utilities
    implementation(libs.androidx.appcompat)
    debugImplementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.constraintlayout.compose)
    debugImplementation(libs.androidx.customview.poolingcontainer)
    implementation(libs.google.material)

}
