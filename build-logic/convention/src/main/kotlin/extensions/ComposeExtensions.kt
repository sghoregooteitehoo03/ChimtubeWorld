import org.gradle.api.Project
import util.libs

internal fun Project.configureComposeDependencies() {
    dependencies.add("implementation", dependencies.platform(libs.findLibrary("androidx-compose-bom").get()))
    dependencies.add("implementation", libs.findLibrary("androidx-activity-compose").get())
    dependencies.add("implementation", libs.findLibrary("androidx-compose-material3").get())
    dependencies.add("implementation", libs.findLibrary("androidx-compose-ui").get())
    dependencies.add("implementation", libs.findLibrary("androidx-compose-ui-graphics").get())
    dependencies.add("implementation", libs.findLibrary("androidx-compose-ui-tooling-preview").get())
    dependencies.add("implementation", libs.findLibrary("androidx-lifecycle-runtime-compose").get())
    dependencies.add("implementation", libs.findLibrary("androidx-lifecycle-viewmodel-compose").get())
    dependencies.add("debugImplementation", libs.findLibrary("androidx-compose-ui-test-manifest").get())
    dependencies.add("debugImplementation", libs.findLibrary("androidx-compose-ui-tooling").get())
    dependencies.add("androidTestImplementation", dependencies.platform(libs.findLibrary("androidx-compose-bom").get()))
    dependencies.add("androidTestImplementation", libs.findLibrary("androidx-compose-ui-test-junit4").get())
}
