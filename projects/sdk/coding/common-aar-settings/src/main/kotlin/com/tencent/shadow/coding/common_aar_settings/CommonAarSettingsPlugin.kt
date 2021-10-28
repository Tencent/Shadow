package com.tencent.shadow.coding.common_aar_settings

import com.android.build.gradle.BaseExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.ExtraPropertiesExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

class CommonAarSettingsPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.pluginManager.apply("com.android.library")
        project.pluginManager.apply("com.tencent.shadow.internal.aar-to-jar")

        val android = project.extensions.getByName("android") as BaseExtension

        configVersions(project, android)

        keepOldAGPBehavior(android, project)

        addCommonDependencies(project)
    }

    private fun addCommonDependencies(project: Project) {
        project.dependencies.add("implementation", "com.tencent.shadow.coding:lint")
    }

    private fun keepOldAGPBehavior(
        android: BaseExtension,
        project: Project
    ) {
        // Starting in version 4.2, AGP will use the Java 8 language level by default.
        // To keep the old behavior, specify Java 7 explicitly.
        // https://developer.android.com/studio/releases/gradle-plugin?hl=lt#java-8-default
        android.compileOptions {
            it.sourceCompatibility = JavaVersion.VERSION_1_7
            it.targetCompatibility = JavaVersion.VERSION_1_7
        }

        // For Kotlin projects, compile to Java 6 instead of 7
        project.afterEvaluate {
            val kotlinOptions = (it.extensions.getByName("android") as ExtensionAware).extensions
                .findByType(KotlinJvmOptions::class.java)
            kotlinOptions?.apply {
                jvmTarget = "1.6"
            }
        }

        android.defaultConfig.buildConfigField(
            "String",
            "VERSION_NAME",
            "\"${android.defaultConfig.versionName}\""
        )
    }

    private fun configVersions(
        project: Project,
        android: BaseExtension
    ) {
        val ext = project.extensions.getByName("ext") as ExtraPropertiesExtension

        android.compileSdkVersion(ext["COMPILE_SDK_VERSION"] as Int)

        android.defaultConfig {
            it.minSdk = ext["MIN_SDK_VERSION"] as Int
            it.targetSdk = ext["TARGET_SDK_VERSION"] as Int
            it.versionCode = ext["VERSION_CODE"] as Int
            it.versionName = ext["VERSION_NAME"] as String
            it.testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
        }
    }

}