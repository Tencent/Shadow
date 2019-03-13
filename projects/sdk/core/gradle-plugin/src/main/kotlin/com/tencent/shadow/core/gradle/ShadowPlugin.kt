package com.tencent.shadow.core.gradle

import com.android.build.gradle.AppPlugin
import com.tencent.shadow.core.AndroidClassPoolBuilder
import com.tencent.shadow.core.ShadowTransform
import com.tencent.shadow.core.gradle.extensions.PackagePluginExtension
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class ShadowPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        System.err.println("ShadowPlugin project.name==" + project.name)

        val plugin = project.plugins.getPlugin(AppPlugin::class.java)
        val sdkDirectory = plugin.extension.sdkDirectory
        val androidJarPath = "platforms/${plugin.extension.compileSdkVersion}/android.jar"
        val androidJar = File(sdkDirectory, androidJarPath)

        //在这里取到的contextClassLoader包含运行时库(classpath方式引入的)shadow-runtime
        val contextClassLoader = Thread.currentThread().contextClassLoader

        val classPoolBuilder = AndroidClassPoolBuilder(contextClassLoader, androidJar)

        val shadowExtension = project.extensions.create("shadow", ShadowExtension::class.java)
        if (!project.hasProperty("disable_shadow_transform")) {
            plugin.extension.registerTransform(ShadowTransform(
                    project,
                    classPoolBuilder,
                    { shadowExtension.transformConfig.useHostContext }
            ))
        }

        project.extensions.create("packagePlugin", PackagePluginExtension::class.java, project)

        project.afterEvaluate {
            val packagePlugin = project.extensions.findByName("packagePlugin")
            val extension = packagePlugin as PackagePluginExtension
            val buildTypes = extension.buildTypes
            for (i in buildTypes) {
                createPackagePluginTask(project, i)
            }
        }
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