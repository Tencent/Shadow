package com.tencent.shadow.core.gradle.extensions

import com.tencent.shadow.core.gradle.ShadowPluginHelper
import groovy.lang.Closure
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import java.io.File
import java.util.*

open class PackagePluginExtension  {

    var loaderApkProjectPath = ""
    var runtimeApkProjectPath = ""

    var uuid = ""
    var version : Int = 0
    var uuidNickName = ""
    var compactVersion : Array<Int> = emptyArray()

    var buildTypes: NamedDomainObjectContainer<PluginBuildType>

    constructor(project: Project) {
        buildTypes = project.container(PluginBuildType::class.java)
        buildTypes.all {
            it.pluginApks = project.container(PluginApkConfig::class.java)
        }
    }

    fun pluginTypes(closure: Closure<PluginBuildType>) {
        buildTypes.configure(closure)
    }

    fun toJson(
            loaderApkName: String,
            runtimeApkName: String,
            buildType: PluginBuildType,
            projectRootDir: File
    ): JSONObject {
        val json = JSONObject()

        //Json文件中 plugin-loader部分信息
        val pluginLoaderObj = JSONObject()
        pluginLoaderObj["apkName"] = loaderApkName
        val loaderFileParent = buildType.loaderApkConfig.second.replace("assemble", "")
        val loaderFile = File("$projectRootDir" +
                "/$loaderApkProjectPath/build/outputs/apk/$loaderFileParent/$loaderApkName")
        println("loaderFile = $loaderFile")
        println("loaderFile exists ? " + loaderFile.exists())
        pluginLoaderObj["hash"] = ShadowPluginHelper.getFileMD5(loaderFile)
        json["pluginLoader"] = pluginLoaderObj


        //Json文件中 plugin-runtime部分信息
        val runtimeObj = JSONObject()
        runtimeObj["apkName"] = runtimeApkName
        val runtimeFileParent = buildType.runtimeApkConfig.second.replace("assemble", "")
        val runtimeFile = File("$projectRootDir" +
                "/$runtimeApkProjectPath/build/outputs/apk/$runtimeFileParent/$runtimeApkName")
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
            val pluginApk = "$projectRootDir" +
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
        if (version > 0) {
            json["version"] = version
        } else {
            json["version"] = 1
        }


        //uuid UUID_NickName
        if (uuid.isEmpty()) {
            json["UUID"] = UUID.randomUUID().toString().toUpperCase()
        } else {
            json["UUID"] = uuid
        }

        if (uuidNickName.isNotEmpty()) {
            json["UUID_NickName"] = uuidNickName
        } else {
            json["UUID_NickName"] = "1.0"
        }

        if (compactVersion.isNotEmpty()) {
            val jsonArray = JSONArray()
            for (i in compactVersion) {
                jsonArray.add(i)
            }
            json["compact_version"] = jsonArray
        }
        return json
    }
}