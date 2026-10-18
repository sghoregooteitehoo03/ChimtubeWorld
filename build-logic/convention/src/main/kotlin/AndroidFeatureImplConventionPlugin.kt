import org.gradle.api.Plugin
import org.gradle.api.Project
import util.libs

class AndroidFeatureImplConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("sg.android.library")
            pluginManager.apply("sg.android.library.compose")
            pluginManager.apply("sg.android.hilt")

            dependencies.add("implementation", project(":core:common"))
            dependencies.add("implementation", project(":core:domain"))
            dependencies.add("implementation", project(":core:designsystem"))
            dependencies.add("implementation", project(":core:ui"))
            dependencies.add("implementation", libs.findLibrary("androidx-hilt-navigation-compose").get())
            dependencies.add("testImplementation", project(":core:testing"))
        }
    }
}
