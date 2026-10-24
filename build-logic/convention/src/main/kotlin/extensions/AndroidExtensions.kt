import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import util.findVersionInt
import util.libs

private const val TEST_INSTRUMENTATION_RUNNER = "androidx.test.runner.AndroidJUnitRunner"

internal fun CommonExtension.configureAndroidDefaults(project: Project) {
    val libs = project.libs

    compileSdk {
        version = release(libs.findVersionInt("compileSdk")) {
            minorApiLevel = libs.findVersionInt("compileSdkMinor")
        }
    }

    defaultConfig.apply {
        minSdk = libs.findVersionInt("minSdk")
        testInstrumentationRunner = TEST_INSTRUMENTATION_RUNNER
    }

    compileOptions.apply {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

internal fun ApplicationExtension.configureAndroidApplicationDefaults(project: Project) {
    defaultConfig.apply {
        targetSdk = project.libs.findVersionInt("targetSdk")
    }
}

internal fun Project.configureAndroidTestDependencies() {
    dependencies.add("testImplementation", libs.findLibrary("junit").get())
    dependencies.add("testImplementation", libs.findLibrary("kotlinx-coroutines-test").get())
    dependencies.add("androidTestImplementation", libs.findLibrary("androidx-junit").get())
    dependencies.add("androidTestImplementation", libs.findLibrary("androidx-espresso-core").get())
}
