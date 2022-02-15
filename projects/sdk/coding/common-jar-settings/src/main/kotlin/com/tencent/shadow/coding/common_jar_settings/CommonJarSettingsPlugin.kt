package com.tencent.shadow.coding.common_jar_settings;


import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension

@Suppress("unused")
class CommonJarSettingsPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.pluginManager.apply("java-library")
        val java = project.extensions.getByType(JavaPluginExtension::class.java)

        java.sourceCompatibility = JavaVersion.VERSION_1_7
        java.targetCompatibility = JavaVersion.VERSION_1_7

        project.dependencies.add("compileOnly", "com.tencent.shadow.coding:android-jar")

    }
}
