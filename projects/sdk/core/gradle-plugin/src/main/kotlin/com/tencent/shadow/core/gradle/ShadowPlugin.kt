/*
 * Tencent is pleased to support the open source community by making Tencent Shadow available.
 * Copyright (C) 2019 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tencent.shadow.core.gradle

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.tasks.ProcessMultiApkApplicationManifest
import com.tencent.shadow.core.gradle.extensions.PackagePluginExtension
import com.tencent.shadow.core.manifest_parser.generatePluginManifest
import com.tencent.shadow.core.transform.ShadowTransform
import com.tencent.shadow.core.transform_kit.AndroidClassPoolBuilder
import com.tencent.shadow.core.transform_kit.ClassPoolBuilder
import org.gradle.api.*
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.provider.Property
import java.io.File
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.jvm.isAccessible

class ShadowPlugin : Plugin<Project> {

    private lateinit var androidClassPoolBuilder: ClassPoolBuilder
    private lateinit var contextClassLoader: ClassLoader

    override fun apply(project: Project) {
        val baseExtension = getBaseExtension(project)

        //在这里取到的contextClassLoader包含运行时库(classpath方式引入的)shadow-runtime
        contextClassLoader = Thread.currentThread().contextClassLoader
        val lateInitBuilder = object : ClassPoolBuilder {
            override fun build() = androidClassPoolBuilder.build()
        }

        val shadowExtension = project.extensions.create("shadow", ShadowExtension::class.java)
        if (!project.hasProperty("disable_shadow_transform")) {
            baseExtension.registerTransform(ShadowTransform(
                project,
                lateInitBuilder,
                { shadowExtension.transformConfig.useHostContext }
            ))
        }

        addFlavorForTransform(baseExtension)

        project.extensions.create("packagePlugin", PackagePluginExtension::class.java, project)

        project.afterEvaluate {
            initAndroidClassPoolBuilder(baseExtension, project)

            createPackagePluginTasks(project)

            createGeneratePluginManifestTasks(project)
        }
    }

    private fun createPackagePluginTasks(project: Project) {
        val packagePlugin = project.extensions.findByName("packagePlugin")
        val extension = packagePlugin as PackagePluginExtension
        val buildTypes = extension.buildTypes

        val tasks = mutableListOf<Task>()
        for (i in buildTypes) {
            println("buildTypes = " + i.name)
            val task = createPackagePluginTask(project, i)
            tasks.add(task)
        }
        if (tasks.isNotEmpty()) {
            project.tasks.create("packageAllPlugin") {
                it.group = "plugin"
                it.description = "打包所有插件"
            }.dependsOn(tasks)
        }
    }

    private fun createGeneratePluginManifestTasks(project: Project) {
        val appExtension: AppExtension = project.extensions.getByType(AppExtension::class.java)
        appExtension.applicationVariants.filter { variant ->
            variant.productFlavors.any { flavor ->
                flavor.dimension == ShadowTransform.DimensionName &&
                        flavor.name == ShadowTransform.ApplyShadowTransformFlavorName
            }
        }.forEach { pluginVariant ->
            val output = pluginVariant.outputs.first()
            val processManifestTask = output.processManifestProvider.get()
            val manifestFile = (processManifestTask as ProcessMultiApkApplicationManifest).mainMergedManifest.get().asFile
            val variantName = manifestFile.parentFile.name
            val outputDir = File(project.buildDir, "generated/source/pluginManifest/$variantName")

            // 添加生成PluginManifest.java任务
            val task = project.tasks.register("generate${variantName.capitalize()}PluginManifest") {
                it.dependsOn(processManifestTask)
                it.inputs.file(manifestFile)
                it.outputs.dir(outputDir).withPropertyName("outputDir")
                val packageForR = (project.tasks.getByName("processPluginDebugResources").property("namespace") as Property<String>).get()
                it.doLast {
                    generatePluginManifest(manifestFile,
                            outputDir,
                            "com.tencent.shadow.core.manifest_parser",
                            packageForR)
                }
            }
            project.tasks.getByName("compile${variantName.capitalize()}JavaWithJavac").dependsOn(task)

            // 把PluginManifest.java添加为源码
            appExtension.sourceSets.getByName(variantName).java {
                srcDir(outputDir)
            }
        }
    }

    private fun addFlavorForTransform(baseExtension: BaseExtension) {
        baseExtension.flavorDimensionList.add(ShadowTransform.DimensionName)
        try {
            baseExtension.productFlavors.create(ShadowTransform.NoShadowTransformFlavorName) {
                it.dimension = ShadowTransform.DimensionName
                it.isDefault = true
            }
            baseExtension.productFlavors.create(ShadowTransform.ApplyShadowTransformFlavorName) {
                it.dimension = ShadowTransform.DimensionName
                it.isDefault = false
            }
        } catch (e: InvalidUserDataException) {
            throw Error("请在android{} DSL之前apply plugin: 'com.tencent.shadow.plugin'", e)
        }
    }

    private fun initAndroidClassPoolBuilder(
        baseExtension: BaseExtension,
        project: Project
    ) {
        val sdkDirectory = baseExtension.sdkDirectory
        val compileSdkVersion =
            baseExtension.compileSdkVersion ?: throw IllegalStateException("compileSdkVersion获取失败")
        val androidJarPath = "platforms/${compileSdkVersion}/android.jar"
        val androidJar = File(sdkDirectory, androidJarPath)

        androidClassPoolBuilder = AndroidClassPoolBuilder(project, contextClassLoader, androidJar)
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

    fun getBaseExtension(project: Project): BaseExtension {
        val plugin = project.plugins.getPlugin(AppPlugin::class.java)
        if (com.android.builder.model.Version.ANDROID_GRADLE_PLUGIN_VERSION == "3.0.0") {
            val method = BasePlugin::class.declaredFunctions.first { it.name == "getExtension" }
            method.isAccessible = true
            return method.call(plugin) as BaseExtension
        } else {
            return project.extensions.getByName("android") as BaseExtension
        }
    }

}