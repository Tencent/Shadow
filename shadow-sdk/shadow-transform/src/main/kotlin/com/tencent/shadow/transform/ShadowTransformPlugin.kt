package com.tencent.shadow.transform

import com.android.build.gradle.AppPlugin
import com.tencent.shadow.transform.transformkit.ClassPoolBuilder
import javassist.ClassPool
import javassist.LoaderClassPath
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class ShadowTransformPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        System.err.println("ShadowTransformPlugin project.name==" + project.name)

        val plugin = project.plugins.getPlugin(AppPlugin::class.java)
        val sdkDirectory = plugin.extension.sdkDirectory
        val androidJarPath = "platforms/${plugin.extension.compileSdkVersion}/android.jar"
        val androidJar = File(sdkDirectory, androidJarPath)

        //在这里取到的contextClassLoader包含运行时库(classpath方式引入的)shadow-runtime
        val contextClassLoader = Thread.currentThread().contextClassLoader

        val classPoolBuilder = object : ClassPoolBuilder {
            override fun build(): ClassPool {
                val classPool = ClassPool(false)//这里使用useDefaultPath:false是因为这里取到的contextClassLoader不包含shadow-runtime
                classPool.appendClassPath(LoaderClassPath(contextClassLoader))
                classPool.appendClassPath(androidJar.absolutePath)
                classPool["com.tencent.shadow.runtime.ContainerFragment"]
                return classPool
            }
        }

        val keepHostObjectsExtension = project.extensions.create("keepHostObjects", KeepHostObjectsExtension::class.java)
        plugin.extension.registerTransform(ShadowTransform(classPoolBuilder, keepHostObjectsExtension))
    }

    open class KeepHostObjectsExtension {
        var useHostContext: Array<String> = emptyArray()
    }
}