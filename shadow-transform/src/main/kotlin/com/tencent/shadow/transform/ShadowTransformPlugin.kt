package com.tencent.shadow.transform

import com.android.build.gradle.AppPlugin
import javassist.ClassPool
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class ShadowTransformPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        System.err.println("CuberPluginLoaderTransformPlugin project.name==" + project.name)

        val classPool = ClassPool(true)
        val plugin = project.plugins.getPlugin(AppPlugin::class.java)

        val androidJar = File(plugin.extension.sdkDirectory, "platforms/${plugin.extension.compileSdkVersion}/android.jar")
        classPool.appendClassPath(androidJar.absolutePath)

        val keepHostObjectsExtension = project.extensions.create("keepHostObjects", KeepHostObjectsExtension::class.java)
        plugin.extension.registerTransform(ShadowTransform(classPool, keepHostObjectsExtension))
    }

    open class KeepHostObjectsExtension {
        var useHostContext: Array<String> = emptyArray()
    }
}