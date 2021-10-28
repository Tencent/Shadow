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

package com.tencent.shadow.coding.aar_to_jar_plugin

import com.android.build.gradle.BaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.Copy
import java.util.*

class AarToJarPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.afterEvaluate {
            val android = it.extensions.getByName("android") as BaseExtension
            android.buildTypes.forEach { buildType ->
                val createJarPackageTask = createJarPackageTask(project, buildType.name)
                addJarConfiguration(project, buildType.name, createJarPackageTask)
            }
        }
    }

    private fun createJarPackageTask(project: Project, buildType: String): Task {
        val taskName = "jar${buildType.capitalize(Locale.getDefault())}Package"
        return project.tasks.create(taskName, Copy::class.java) {
            fun buildDirFile(relativePath: String) =
                project.file(project.buildDir.path + relativePath)

            val aarFileName = "${project.name}-${buildType}"
            val aarFile = buildDirFile("/outputs/aar/${aarFileName}.aar")
            val outputDir = buildDirFile("/outputs/jar")

            it.from(project.zipTree(aarFile))
            it.into(outputDir)
            it.include("classes.jar")
            it.rename("classes.jar", "${aarFileName}.jar")
            it.group = "build"
            it.description = "生成jar包"
        }.dependsOn(
            project.getTasksByName(
                "assemble${buildType.capitalize(Locale.getDefault())}",
                false
            ).first()
        )
    }

    /**
     * 添加一个额外的Configuration，用于buildScript中以classpath方式依赖
     */
    private fun addJarConfiguration(
        project: Project,
        buildType: String,
        createJarPackageTask: Task
    ) {
        val configurationName = "jar-${buildType}"
        val jarFile =
            project.file(project.buildDir.path + "/outputs/jar/${project.name}-${buildType}.jar")
        project.configurations.create(configurationName)
        project.artifacts.add(configurationName, jarFile) {
            it.builtBy(createJarPackageTask)
        }
    }
}