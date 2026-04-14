import com.twugteam.admin.chirp.convention.applyHierarchyTemplate
import com.twugteam.admin.chirp.convention.configureAndroidLibraryTarget
import com.twugteam.admin.chirp.convention.configureIosTargets
import com.twugteam.admin.chirp.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class CmpApplicationConventionPlugin: Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.kotlin.multiplatform.library")
                apply("org.jetbrains.kotlin.multiplatform")
                apply("org.jetbrains.compose")
                apply("org.jetbrains.kotlin.plugin.compose")
                apply("org.jetbrains.kotlin.plugin.serialization")
            }

            configureAndroidLibraryTarget()
            configureIosTargets()
//            configureDesktopTarget()


            extensions.configure<KotlinMultiplatformExtension> {
                applyHierarchyTemplate()
            }

            dependencies {
                // Core Compose dependencies
                "commonMainImplementation"(libs.findLibrary("jetbrains-compose-runtime").get())
                "commonMainImplementation"(libs.findLibrary("jetbrains-compose-foundation").get())
                "commonMainImplementation"(libs.findLibrary("jetbrains-compose-material3").get())
                "commonMainImplementation"(libs.findLibrary("jetbrains-compose-ui").get())

                // From CMP 1.10.0+: Resources and preview tooling are now separate modules
                "commonMainImplementation"(libs.findLibrary("jetbrains-compose-resources").get())
                "commonMainImplementation"(libs.findLibrary("jetbrains-compose-ui-tooling-preview").get())

                // For Single-variant model: use androidMainImplementation instead of debugImplementation
                "androidMainImplementation"(libs.findLibrary("jetbrains-compose-ui-tooling").get())
            }
        }
    }
}