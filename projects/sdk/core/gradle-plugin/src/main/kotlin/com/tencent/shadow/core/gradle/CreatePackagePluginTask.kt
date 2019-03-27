package com.tencent.shadow.core.gradle

import com.tencent.shadow.core.gradle.extensions.PackagePluginExtension
import com.tencent.shadow.core.gradle.extensions.PluginBuildType
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.bundling.Zip
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.util.*

internal fun createPackagePluginTask(project: Project, buildType: PluginBuildType) {
    project.tasks.create("package${buildType.name.capitalize()}Plugin", Zip::class.java) {
        val packagePlugin = project.extensions.findByName("packagePlugin")
        val extension = packagePlugin as PackagePluginExtension

        val pluginApkName: ArrayList<String> = ArrayList()
        for (i in buildType.pluginApks) {
            pluginApkName.add(i.apkName)
        }
        val runtimeApkName: String = buildType.runtimeApkConfig.first
        val loaderApkName: String = buildType.loaderApkConfig.first


        println()
        val targetConfigFile = File(project.buildDir.absolutePath + "/intermediates/generatePluginConfig/${buildType.name}/config.json")
        targetConfigFile.parentFile.mkdirs()

        val runtimeFileParent = buildType.runtimeApkConfig.second.replace("assemble", "").toLowerCase()
        val runtimeFile = File("${project.rootDir}" +
                "/${extension.runtimeApkProjectPath}/build/outputs/apk/$runtimeFileParent/$runtimeApkName")
        println("runtimeFile = $runtimeFile")

        val loaderFileParent = buildType.loaderApkConfig.second.replace("assemble", "").toLowerCase()
        val loaderFile = File("${project.rootDir}" +
                "/${extension.loaderApkProjectPath}/build/outputs/apk/$loaderFileParent/$loaderApkName")
        println("loaderFile = $loaderFile")

        val pluginFiles: MutableList<File> = mutableListOf()
        for (i in buildType.pluginApks) {
            val pluginFileParent = i.buildTask.replace("assemble", "").toLowerCase()
            val pluginApk = "${project.rootDir}" +
                    "/${i.projectPath}/build/outputs/apk/$pluginFileParent/${i.apkName}"
            println("pluginApk = $pluginApk")
            pluginFiles.add(File(pluginApk))
        }


        it.group = "plugin"
        it.description = "打包插件"
        it.from(pluginFiles, runtimeFile, loaderFile, targetConfigFile)
        val suffix: String? = System.getenv("PluginSuffix")
        if (suffix == null) {
            it.archiveName = "plugin-${buildType.name}.zip"
        } else {
            it.archiveName = "plugin-${buildType.name}-$suffix.zip"
        }
        it.destinationDir = File("${project.rootDir}/build")
    }
            .dependsOn(createGenerateConfigTask(project, buildType))
}

private fun createGenerateConfigTask(project: Project, buildType: PluginBuildType): Task {
    val packagePlugin = project.extensions.findByName("packagePlugin")
    val extension = packagePlugin as PackagePluginExtension

    val pluginApkName: ArrayList<String> = ArrayList()
    for (i in buildType.pluginApks) {
        pluginApkName.add(i.apkName)
    }
    val runtimeApkName: String = buildType.runtimeApkConfig.first
    val loaderApkName: String = buildType.loaderApkConfig.first

    for (pluginApk in pluginApkName) {
        println("pluginApk = $pluginApk")
    }

    val targetConfigFile = File(project.buildDir.absolutePath + "/intermediates/generatePluginConfig/${buildType.name}/config.json")
    targetConfigFile.parentFile.mkdirs()
    println("configFile parentFile = " + targetConfigFile.parentFile)

    val pluginApkTasks: MutableList<String> = mutableListOf()
    for (i in buildType.pluginApks) {
        val task = ":" + (i.projectPath.replace("/", ":")
                + ":${i.buildTask}")
        println("pluginApkProjects task = $task")
        pluginApkTasks.add(task)
    }

    val runtimeTask = ":" + (extension.runtimeApkProjectPath.replace("/", ":")
            + ":${buildType.runtimeApkConfig.second}")
    val loaderTask = ":" + (extension.loaderApkProjectPath.replace("/", ":")
            + ":${buildType.loaderApkConfig.second}")
    println("loader task = $loaderTask")
    println("runtime task = $runtimeTask")

    return project.tasks.create("generate${buildType.name.capitalize()}Config") {
        it.group = "plugin"
        it.description = "生成插件配置文件"
        it.outputs.file(targetConfigFile)
    }
            .dependsOn(pluginApkTasks)
            .dependsOn(runtimeTask)
            .dependsOn(loaderTask)
            .doLast {

                println("generateConfig task begin")
                val json = extension.toJson(
                        loaderApkName,
                        runtimeApkName,
                        buildType,
                        project.rootDir
                )

                val bizWriter = BufferedWriter(FileWriter(targetConfigFile))
                bizWriter.write(json.toJSONString())
                bizWriter.newLine()
                bizWriter.flush()
                bizWriter.close()

                println("generateConfig task done")
            }
}