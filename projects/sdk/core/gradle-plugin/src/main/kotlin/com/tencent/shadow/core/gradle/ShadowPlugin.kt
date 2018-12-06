package com.tencent.shadow.core.gradle

import com.android.build.gradle.AppPlugin
import com.tencent.shadow.core.AndroidClassPoolBuilder
import com.tencent.shadow.core.ShadowTransform
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class ShadowPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        System.err.println("ShadowPlugin project.name==" + project.name)

        val currentJar = File(this::class.java.protectionDomain.codeSource.location.toURI())
        val pluginJarSelf = project.files(currentJar)

        val plugin = project.plugins.getPlugin(AppPlugin::class.java)
        val sdkDirectory = plugin.extension.sdkDirectory
        val androidJarPath = "platforms/${plugin.extension.compileSdkVersion}/android.jar"
        val androidJar = File(sdkDirectory, androidJarPath)

        //在这里取到的contextClassLoader包含运行时库(classpath方式引入的)shadow-runtime
        val contextClassLoader = Thread.currentThread().contextClassLoader

        val classPoolBuilder = AndroidClassPoolBuilder(contextClassLoader, androidJar)

        val shadowExtension = project.extensions.create("shadow", ShadowExtension::class.java)
        plugin.extension.registerTransform(ShadowTransform(
                pluginJarSelf,
                classPoolBuilder,
                { shadowExtension.transformConfig.useHostContext }
        ))
    }

    open class ShadowExtension {
        var transformConfig = TransformConfig()
        fun transform(action: Action<in TransformConfig>) {
            action.execute(transformConfig)
        }
    }

    class TransformConfig {
        var useHostContext: Array<String> = emptyArray()
    }
}