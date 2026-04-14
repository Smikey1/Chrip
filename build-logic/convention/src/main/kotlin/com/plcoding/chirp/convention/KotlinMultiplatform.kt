package com.twugteam.admin.chirp.convention

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * Configures Kotlin Multiplatform for library modules.
 * Note: Android library settings (namespace, compileSdk, minSdk, androidResources) must be
 * configured in each module's build.gradle.kts using kotlin { androidLibrary { ... } }
 */

internal fun Project.configureKotlinMultiplatform() {
    configureAndroidLibraryTarget()
//    configureDesktopTarget()

    extensions.configure<KotlinMultiplatformExtension> {
        listOf(
            iosX64(),
            iosArm64(),
            iosSimulatorArm64()
        ).forEach { iosTarget ->
            iosTarget.binaries.framework {
                baseName = this@configureKotlinMultiplatform.pathToFrameworkName()
            }
        }

        //TODO #1: Check and remove later
        extensions.configure<KotlinMultiplatformAndroidLibraryExtension> {
            minSdk = 26
            compileSdk = 36
            namespace = pathToPackageName()
        }

        applyHierarchyTemplate()

        compilerOptions {
            freeCompilerArgs.add("-Xexpect-actual-classes")
            freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
            freeCompilerArgs.add("-opt-in=kotlin.time.ExperimentalTime")
        }
    }
}