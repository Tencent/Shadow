package com.tencent.shadow.core.gradle

import com.tencent.shadow.core.gradle.extensions.PackagePluginExtension
import com.tencent.shadow.core.gradle.extensions.PluginBuildType
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.bundling.Zip
import org.gradle.internal.impldep.com.google.gson.JsonArray
import org.gradle.internal.impldep.org.apache.http.util.TextUtils
import org.json.simple.JSONArray
import org.json.simple.JSONObject
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

        val runtimeFileParent = buildType.runtimeApkConfig.second.replace("assemble", "")
        val runtimeFile = File("${project.rootDir}" +
                "/${extension.runtimeApkProjectPath}/build/outputs/apk/$runtimeFileParent/$runtimeApkName")
        println("runtimeFile = $runtimeFile")

        val loaderFileParent = buildType.loaderApkConfig.second.replace("assemble", "")
        val loaderFile = File("${project.rootDir}" +
                "/${extension.loaderApkProjectPath}/build/outputs/apk/$loaderFileParent/$loaderApkName")
        println("loaderFile = $loaderFile")

        val pluginFiles: MutableList<File> = mutableListOf()
        for (i in buildType.pluginApks) {
            val pluginFileParent = i.buildTask.replace("assemble", "")
            val pluginApk = "${project.rootDir}" +
                    "/${i.projectPath}/build/outputs/apk/$pluginFileParent/${i.apkName}"
            println("pluginApk = $pluginApk")
            pluginFiles.add(File(pluginApk))
        }


        it.group = "plugin"
        it.description = "打包插件"
        it.from(pluginFiles, runtimeFile, loaderFile, targetConfigFile)
        it.archiveName = "plugin-${buildType.name}-local.zip"
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
                val json = JSONObject()

                //Json文件中 plugin-loader部分信息
                val pluginLoaderObj = JSONObject()
                pluginLoaderObj["apkName"] = loaderApkName
                val loaderFileParent = buildType.loaderApkConfig.second.replace("assemble", "")
                val loaderFile = File("${project.rootDir}" +
                        "/${extension.loaderApkProjectPath}/build/outputs/apk/$loaderFileParent/$loaderApkName")
                println("loaderFile = $loaderFile")
                println("loaderFile exists ? " + loaderFile.exists())
                pluginLoaderObj["hash"] = ShadowPluginHelper.getFileMD5(loaderFile)
                json["pluginLoader"] = pluginLoaderObj


                //Json文件中 plugin-runtime部分信息
                val runtimeObj = JSONObject()
                runtimeObj["apkName"] = runtimeApkName
                val runtimeFileParent = buildType.runtimeApkConfig.second.replace("assemble", "")
                val runtimeFile = File("${project.rootDir}" +
                        "/${extension.runtimeApkProjectPath}/build/outputs/apk/$runtimeFileParent/$runtimeApkName")
                println("runtimeFile = $runtimeFile")
                println("runtimeFile exists ? " + runtimeFile.exists())
                runtimeObj["hash"] = ShadowPluginHelper.getFileMD5(runtimeFile)
                json["runtime"] = runtimeObj


                //Json文件中 plugin部分信息
                val jsonArr = JSONArray()
                for (i in buildType.pluginApks) {
                    val pluginObj = JSONObject()
                    pluginObj["partKey"] = i.partKey
                    pluginObj["apkName"] = i.apkName
                    val pluginFileParent = i.buildTask.replace("assemble", "")
                    val pluginApk = "${project.rootDir}" +
                            "/${i.projectPath}/build/outputs/apk/$pluginFileParent/${i.apkName}"
                    println("pluginApkPath = $pluginApk")
                    println("pluginApkPath exits ? " + File(pluginApk).exists())
                    pluginObj["hash"] = ShadowPluginHelper.getFileMD5(File(pluginApk))
                    if (i.dependsOn.isNotEmpty()) {
                        val dependsOnJson = JSONArray()
                        for (k in i.dependsOn) {
                            dependsOnJson.add(k)
                        }
                        pluginObj["dependsOn"] = dependsOnJson
                    }
                    jsonArr.add(pluginObj)
                }
                json["plugins"] = jsonArr


                //Config.json版本号
                if (extension.version > 0) {
                    json["version"] = extension.version
                } else {
                    json["version"] = 1
                }


                //uuid UUID_NickName
                if (TextUtils.isEmpty(extension.uuid)) {
                    json["UUID"] = UUID.randomUUID().toString().toUpperCase()
                } else {
                    json["UUID"] = extension.uuid
                }

                if (!TextUtils.isEmpty(extension.uuidNickName)) {
                    json["UUID_NickName"] = extension.uuidNickName
                } else {
                    json["UUID_NickName"] = "1.0"
                }

                if (extension.compactVersion.isNotEmpty()) {
                    val jsonArray = JsonArray()
                    for (i in extension.compactVersion) {
                        jsonArray.add(i)
                    }
                    json["compact_version"] = jsonArray
                }

                val bizWriter = BufferedWriter(FileWriter(targetConfigFile))
                bizWriter.write(json.toJSONString())
                bizWriter.newLine()
                bizWriter.flush()
                bizWriter.close()

                println("generateConfig task done")
            }
}