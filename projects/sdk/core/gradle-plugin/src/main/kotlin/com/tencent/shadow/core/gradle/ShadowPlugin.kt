package com.tencent.shadow.core.gradle

import com.android.build.gradle.AppPlugin
import com.tencent.shadow.core.AndroidClassPoolBuilder
import com.tencent.shadow.core.ShadowTransform
import com.tencent.shadow.core.gradle.ShadowPluginHelper.Companion.gitShortRev
import com.tencent.shadow.core.gradle.ShadowPluginHelper.Companion.versionName
import com.tencent.shadow.core.gradle.extensions.PackagePluginExtension
import org.gradle.api.Action
import org.gradle.api.Plugin
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
        plugin.extension.registerTransform(ShadowTransform(
                project,
                classPoolBuilder,
                { shadowExtension.transformConfig.useHostContext }
        ))

        project.extensions.create("packagePlugin", PackagePluginExtension::class.java, project)

        project.afterEvaluate {
            createPackagePluginTask(project, "debug")
            createPackagePluginTask(project, "release")
        }
    }

    private fun createGenerateConfigTask(project: Project, buildType: String): Task {
        val packagePlugin = project.extensions.findByName("packagePlugin")
        val extension = packagePlugin as PackagePluginExtension
        val pluginApkName: Array<String>
        val runtimeApkName: String
        val loaderApkName: String
        if ("debug".equals(buildType, true)) {
            pluginApkName = extension.debug.pluginApkNames
            runtimeApkName = extension.debug.runtimeApkName
            loaderApkName = extension.debug.loaderApkName
        } else {
            pluginApkName = extension.release.pluginApkNames
            runtimeApkName = extension.release.runtimeApkName
            loaderApkName = extension.release.loaderApkName
        }

        if (pluginApkName.size != extension.pluginApkProjectPaths.size) {
            throw IllegalArgumentException("pluginApkProjects 与 pluginApkNames长度不相等")
        }
        for (pluginApk in pluginApkName) {
            println("pluginApk = $pluginApk")
        }

        val targetConfigFile = File(project.buildDir.absolutePath + "/intermediates/generatePluginConfig/$buildType/config.json")
        targetConfigFile.parentFile.mkdirs()
        println("configFile parentFile = " + targetConfigFile.parentFile)

        val pluginApkTasks: MutableList<String> = mutableListOf()
        println("pluginApkProjects = " + extension.pluginApkProjectPaths.size)
        for (i in extension.pluginApkProjectPaths.indices) {
            val task = (extension.pluginApkProjectPaths[i].replace("/", ":")
                    + ":assemble${buildType.capitalize()}")
            println("pluginApkProjects task = $task")
            pluginApkTasks.add(i, task)
        }

        val runtimeTask = (extension.runtimeApkProjectPath.replace("/", ":")
                + ":assemble${buildType.capitalize()}")
        val loaderTask = (extension.loaderApkProjectPath.replace("/", ":")
                + ":assemble${buildType.capitalize()}")
        println("loader task = $loaderTask")
        println("runtime task = $runtimeTask")

        return project.tasks.create("generate${buildType.capitalize()}Config") {
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
                    val loaderFile: String = "${project.rootDir}" +
                            "${extension.loaderApkProjectPath}/build/outputs/apk/$buildType/$loaderApkName"
                    println("loaderFile = $loaderFile")
                    println("loaderFile exists ? " + File(loaderFile).exists())
                    pluginLoaderObj["hash"] = ShadowPluginHelper.bytes2HexStr(loaderFile.toByteArray())
                    json["pluginLoader"] = pluginLoaderObj


                    //Json文件中 plugin-runtime部分信息
                    val runtimeObj = JSONObject()
                    runtimeObj["apkName"] = runtimeApkName
                    val runtimeFile: String = "${project.rootDir}" +
                            "${extension.runtimeApkProjectPath}/build/outputs/apk/$buildType/$runtimeApkName"
                    println("runtimeFile = $runtimeFile")
                    println("runtimeFile exists ? " + File(runtimeFile).exists())
                    runtimeObj["hash"] = ShadowPluginHelper.bytes2HexStr(runtimeFile.toByteArray())
                    json["runtime"] = runtimeObj


                    //Json文件中 plugin部分信息
                    val jsonArr = JSONArray()
                    for (i in extension.pluginApkProjectPaths.indices) {
                        val pluginObj = JSONObject()
                        pluginObj["partKey"] = extension.pluginApkPartKeys[i]
                        pluginObj["apkName"] = pluginApkName[i]
                        val pluginApk = "${project.rootDir}" +
                                "${extension.pluginApkProjectPaths[i]}/build/outputs/apk/$buildType/${pluginApkName[i]}"
                        println("pluginApkPath = $pluginApk")
                        println("pluginApkPath exits ? " + File(pluginApk).exists())
                        pluginObj["hash"] = ShadowPluginHelper.bytes2HexStr(pluginApk.toByteArray())
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
                    val uuid = UUID.randomUUID().toString().toUpperCase()
                    json["uuid"] = uuid

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

    private fun createPackagePluginTask(project: Project, buildType: String) {
        project.tasks.create("package${buildType.capitalize()}Plugin", Zip::class.java) {
            val packagePlugin = project.extensions.findByName("packagePlugin")
            val extension = packagePlugin as PackagePluginExtension
            val pluginApkName: Array<String>
            val runtimeApkName: String
            val loaderApkName: String
            if ("debug".equals(buildType, true)) {
                pluginApkName = extension.debug.pluginApkNames
                runtimeApkName = extension.debug.runtimeApkName
                loaderApkName = extension.debug.loaderApkName
            } else {
                pluginApkName = extension.release.pluginApkNames
                runtimeApkName = extension.release.runtimeApkName
                loaderApkName = extension.release.loaderApkName
            }

            println()
            val targetConfigFile = File(project.buildDir.absolutePath + "/intermediates/generatePluginConfig/$buildType/config.json")
            targetConfigFile.parentFile.mkdirs()

            val runtimeFile = File("${project.rootDir}" +
                    "${extension.runtimeApkProjectPath}/build/outputs/apk/$buildType/$runtimeApkName")
            println("runtimeFile = $runtimeFile")

            val loaderFile = File("${project.rootDir}" +
                    "${extension.loaderApkProjectPath}/build/outputs/apk/$buildType/$loaderApkName")
            println("loaderFile = $loaderFile")

            val pluginFiles: MutableList<File> = mutableListOf()
            for (i in extension.pluginApkProjectPaths.indices) {
                val pluginApk = "${project.rootDir}" +
                        "${extension.pluginApkProjectPaths[i]}/build/outputs/apk/$buildType/${pluginApkName[i]}"
                println("pluginApk = $pluginApk")
                pluginFiles.add(File(pluginApk))
            }


            it.group = "plugin"
            it.description = "打包插件"
            it.from(pluginFiles, runtimeFile, loaderFile, targetConfigFile)

            if (ShadowPluginHelper.isFinalRelease()) {
                it.archiveName = "plugin-${System.getenv("MajorVersion")}.${System.getenv("MinorVersion")}" +
                        ".${System.getenv("FixVersion")}.${System.getenv("BuildNo")}-${gitShortRev()}.zip"
            } else {
                it.archiveName = "plugin-$buildType-${versionName()}.zip"
            }
            println()
            println("archiveName = " + it.archiveName)

            if (ShadowPluginHelper.isCIEnv()) {
                it.destinationDir = File("${project.rootDir}/bin")
            } else {
                it.destinationDir = File("${project.rootDir}/build")
            }
            println("destinationDir = " + it.destinationDir)
        }
                .dependsOn(createGenerateConfigTask(project, buildType))
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