package com.tencent.cubershi

import com.android.build.gradle.AppPlugin
import javassist.ClassPool
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class CuberPluginLoaderTransformPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        System.err.println("CuberPluginLoaderTransformPlugin project.name==" + project.name)

        val classPool: ClassPool = ClassPool.getDefault()
        val plugin = project.plugins.getPlugin(AppPlugin::class.java)

        val androidJar = File(plugin.extension.sdkDirectory, "platforms/${plugin.extension.compileSdkVersion}/android.jar")
        classPool.appendClassPath(androidJar.absolutePath)

        plugin.extension.registerTransform(CuberPluginLoaderTransform())
    }
}