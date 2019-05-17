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
        println("PackagePluginTask task run")

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
        val targetConfigFile = File(project.buildDir.absolutePath + "/intermediates/generatePluginConfig/${buildType.name}/config.json")
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
        val suffix: String? = System.getenv("PluginSuffix")
        if (suffix == null) {
            it.archiveName = "plugin-${buildType.name}.zip"
        } else {
            it.archiveName = "plugin-${buildType.name}-$suffix.zip"
        }
        it.destinationDir = File("${project.rootDir}/build")
    }.dependsOn(createGenerateConfigTask(project, buildType))
}

private fun createGenerateConfigTask(project: Project, buildType: PluginBuildType): Task {
    println("GenerateConfigTask task run")
    val packagePlugin = project.extensions.findByName("packagePlugin")
    val extension = packagePlugin as PackagePluginExtension

    //runtime apk build task
    val runtimeApkName = buildType.runtimeApkConfig.first
    var runtimeTask = ""
    if (runtimeApkName.isNotEmpty()) {
        runtimeTask = buildType.runtimeApkConfig.second
        println("runtime task = $runtimeTask")
    }


    //loader apk build task
    val loaderApkName = buildType.loaderApkConfig.first
    var loaderTask = ""
    if (loaderApkName.isNotEmpty()) {
        loaderTask = buildType.loaderApkConfig.second
        println("loader task = $loaderTask")
    }


    val targetConfigFile = File(project.buildDir.absolutePath + "/intermediates/generatePluginConfig/${buildType.name}/config.json")


    val pluginApkTasks: MutableList<String> = mutableListOf()
    for (i in buildType.pluginApks) {
        val task = i.buildTask
        println("pluginApkProjects task = $task")
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

                println("generateConfig task begin")
                val json = extension.toJson(project, loaderApkName, runtimeApkName, buildType)

                val bizWriter = BufferedWriter(FileWriter(targetConfigFile))
                bizWriter.write(json.toJSONString())
                bizWriter.newLine()
                bizWriter.flush()
                bizWriter.close()

                println("generateConfig task done")
            }
    if (loaderTask.isNotEmpty()) {
        task.dependsOn(loaderTask)
    }
    if (runtimeTask.isNotEmpty()) {
        task.dependsOn(runtimeTask)
    }
    return task
}