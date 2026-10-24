import org.gradle.api.Plugin
import org.gradle.api.Project
import util.libs

class AndroidFeatureApiConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("sg.android.library")
            pluginManager.apply("sg.kotlin.serialization")

            dependencies.add("api", project(":core:model"))
            dependencies.add("api", project(":core:navigation"))
            dependencies.add("api", libs.findLibrary("kotlinx-serialization-core").get())
        }
    }
}
