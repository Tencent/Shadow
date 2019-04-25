package com.tencent.shadow.core.gradle.extensions

import com.tencent.shadow.core.gradle.ShadowPluginHelper
import groovy.lang.Closure
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import java.io.File
import java.util.*

open class PackagePluginExtension {

    var loaderApkProjectPath = ""
    var runtimeApkProjectPath = ""

    var uuid = ""
    var version: Int = 0
    var uuidNickName = ""
    var compactVersion: Array<Int> = emptyArray()

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
            project: Project,
            loaderApkName: String,
            runtimeApkName: String,
            buildType: PluginBuildType
    ): JSONObject {
        val json = JSONObject()

        if (loaderApkName.isNotEmpty()) {
            //Json文件中 plugin-loader部分信息
            val pluginLoaderObj = JSONObject()
            pluginLoaderObj["apkName"] = loaderApkName
            val loaderFile = ShadowPluginHelper.getLoaderApkFile(project, buildType, true)
            pluginLoaderObj["hash"] = ShadowPluginHelper.getFileMD5(loaderFile)
            json["pluginLoader"] = pluginLoaderObj
        }


        if (runtimeApkName.isNotEmpty()) {
            //Json文件中 plugin-runtime部分信息
            val runtimeObj = JSONObject()
            runtimeObj["apkName"] = runtimeApkName
            val runtimeFile = ShadowPluginHelper.getRuntimeApkFile(project, buildType, true)
            runtimeObj["hash"] = ShadowPluginHelper.getFileMD5(runtimeFile)
            json["runtime"] = runtimeObj
        }


        //Json文件中 plugin部分信息
        val jsonArr = JSONArray()
        for (i in buildType.pluginApks) {
            val pluginObj = JSONObject()
            pluginObj["businessName"] = i.businessName
            pluginObj["partKey"] = i.partKey
            pluginObj["apkName"] = i.apkName
            pluginObj["hash"] = ShadowPluginHelper.getFileMD5(ShadowPluginHelper.getPluginFile(project, i, true))
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
        val uuid = "${project.rootDir}" + "/build/uuid.txt"
        val uuidFile = File(uuid)
        when {
            uuidFile.exists() -> {
                json["UUID"] = uuidFile.readText()
                println("uuid = " + json["UUID"] + " 由文件生成")
            }
            this.uuid.isEmpty() -> {
                json["UUID"] = UUID.randomUUID().toString().toUpperCase()
                println("uuid = " + json["UUID"] + " 随机生成")
            }
            else -> {
                json["UUID"] = this.uuid
                println("uuid = " + json["UUID"] + " 由配置生成")
            }
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