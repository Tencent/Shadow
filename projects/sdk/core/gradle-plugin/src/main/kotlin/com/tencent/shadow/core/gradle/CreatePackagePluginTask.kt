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

import com.tencent.shadow.core.gradle.extensions.PackagePluginExtension
import com.tencent.shadow.core.gradle.extensions.PluginBuildType
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.bundling.Zip
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

internal fun createPackagePluginTask(project: Project, buildType: PluginBuildType): Task {
    return project.tasks.create("package${buildType.name.capitalize()}Plugin", Zip::class.java) {
        project.logger.info("PackagePluginTask task run")

        //runtime apk file
        val runtimeApkName: String = buildType.runtimeApkConfig.first
        var runtimeFile: File? = null
        if (runtimeApkName.isNotEmpty()) {
            runtimeFile = ShadowPluginHelper.getRuntimeApkFile(project, buildType, false)
        }


        //loader apk file
        val loaderApkName: String = buildType.loaderApkConfig.first
        var loaderFile: File? = null
        if (loaderApkName.isNotEmpty()) {
            loaderFile = ShadowPluginHelper.getLoaderApkFile(project, buildType, false)
        }


        //config file
        val targetConfigFile =
            File(project.buildDir.absolutePath + "/intermediates/generatePluginConfig/${buildType.name}/config.json")
        targetConfigFile.parentFile.mkdirs()


        //all plugin apks
        val pluginFiles: MutableList<File> = mutableListOf()
        for (i in buildType.pluginApks) {
            pluginFiles.add(ShadowPluginHelper.getPluginFile(project, i, false))
        }


        it.group = "plugin"
        it.description = "打包插件"
        it.outputs.upToDateWhen { false }
        if (runtimeFile != null) {
            pluginFiles.add(runtimeFile)
        }
        if (loaderFile != null) {
            pluginFiles.add(loaderFile)
        }
        it.from(pluginFiles, targetConfigFile)

        val packagePlugin = project.extensions.findByName("packagePlugin")
        val extension = packagePlugin as PackagePluginExtension

        val suffix = if (extension.archiveSuffix.isEmpty()) "" else extension.archiveSuffix
        val prefix = if (extension.archivePrefix.isEmpty()) "plugin" else extension.archivePrefix
        if (suffix.isEmpty()) {
            it.archiveName = "$prefix-${buildType.name}.zip"
        } else {
            it.archiveName = "$prefix-${buildType.name}-$suffix.zip"
        }
        it.destinationDir =
            File(if (extension.destinationDir.isEmpty()) "${project.rootDir}/build" else extension.destinationDir)
    }.dependsOn(createGenerateConfigTask(project, buildType))
}

private fun createGenerateConfigTask(project: Project, buildType: PluginBuildType): Task {
    project.logger.info("GenerateConfigTask task run")
    val packagePlugin = project.extensions.findByName("packagePlugin")
    val extension = packagePlugin as PackagePluginExtension

    //runtime apk build task
    val runtimeApkName = buildType.runtimeApkConfig.first
    var runtimeTask = ""
    if (runtimeApkName.isNotEmpty()) {
        runtimeTask = buildType.runtimeApkConfig.second
        project.logger.info("runtime task = $runtimeTask")
    }


    //loader apk build task
    val loaderApkName = buildType.loaderApkConfig.first
    var loaderTask = ""
    if (loaderApkName.isNotEmpty()) {
        loaderTask = buildType.loaderApkConfig.second
        project.logger.info("loader task = $loaderTask")
    }


    val targetConfigFile =
        File(project.buildDir.absolutePath + "/intermediates/generatePluginConfig/${buildType.name}/config.json")


    val pluginApkTasks: MutableList<String> = mutableListOf()
    for (i in buildType.pluginApks) {
        val task = i.buildTask
        project.logger.info("pluginApkProjects task = $task")
        pluginApkTasks.add(task)
    }

    val task = project.tasks.create("generate${buildType.name.capitalize()}Config") {
        it.group = "plugin"
        it.description = "生成插件配置文件"
        it.outputs.file(targetConfigFile)
        it.outputs.upToDateWhen { false }
    }
        .dependsOn(pluginApkTasks)
        .doLast {

            project.logger.info("generateConfig task begin")
            val json = extension.toJson(project, loaderApkName, runtimeApkName, buildType)

            val bizWriter = BufferedWriter(FileWriter(targetConfigFile))
            bizWriter.write(json.toJSONString())
            bizWriter.newLine()
            bizWriter.flush()
            bizWriter.close()

            project.logger.info("generateConfig task done")
        }
    if (loaderTask.isNotEmpty()) {
        task.dependsOn(loaderTask)
    }
    if (runtimeTask.isNotEmpty()) {
        task.dependsOn(runtimeTask)
    }
    return task
}