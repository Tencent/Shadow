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

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.BaseExtension
import com.tencent.shadow.core.gradle.extensions.PackagePluginExtension
import com.tencent.shadow.core.transform.ShadowTransform
import com.tencent.shadow.core.transform_kit.AndroidClassPoolBuilder
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.BasePlugin
import java.io.File
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.jvm.isAccessible

class ShadowPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        System.err.println("ShadowPlugin project.name==" + project.name)

        val baseExtension = getBaseExtension(project)
        val sdkDirectory = baseExtension.sdkDirectory
        val androidJarPath = "platforms/${baseExtension.compileSdkVersion}/android.jar"
        val androidJar = File(sdkDirectory, androidJarPath)

        //在这里取到的contextClassLoader包含运行时库(classpath方式引入的)shadow-runtime
        val contextClassLoader = Thread.currentThread().contextClassLoader

        val classPoolBuilder = AndroidClassPoolBuilder(project, contextClassLoader, androidJar)

        val shadowExtension = project.extensions.create("shadow", ShadowExtension::class.java)
        if (!project.hasProperty("disable_shadow_transform")) {
            baseExtension.registerTransform(ShadowTransform(
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