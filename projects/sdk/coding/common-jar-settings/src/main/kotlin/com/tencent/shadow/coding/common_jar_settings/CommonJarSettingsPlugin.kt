package com.tencent.shadow.coding.common_jar_settings;


import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.compile.JavaCompile

@Suppress("unused")
class CommonJarSettingsPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.pluginManager.apply("java-library")
        val java = project.extensions.getByType(JavaPluginExtension::class.java)

        java.sourceCompatibility = JavaVersion.VERSION_1_7
        java.targetCompatibility = JavaVersion.VERSION_1_7

        val androidJar = project.files(AndroidJar.ANDROID_JAR_PATH)
        // 将android.jar设置为这些jar工程的bootclasspath，以便javac编译时使用的JDK标准库采用android平台的定义
        project.tasks.withType(JavaCompile::class.java) {
            it.options.bootstrapClasspath = androidJar
        }

        // IDE不会自动索引bootstrapClasspath，所以把bootstrapClasspath重复添加到compileOnly中
        project.dependencies.add("compileOnly", androidJar)
    }
}
