package com.tencent.shadow.core.gradle

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.junit.Assert
import org.junit.Test
import java.io.File
import java.util.zip.ZipFile

/**
 *  测试打包多个工程的插件包 第一个插件包包含loader、runtime、插件1、config.json的插件包  第二个插件包 包含 插件2、config.json
 *  其中插件包1 中的json文件中的uuid 需要和 插件包2 中json文件的uuid 一样
 * ./gradlew -p projects/sdk/core :gradle-plugin:test --tests com.tencent.shadow.core.gradle.PackageMultiPluginTest.testPackageMultiPlugin
 */
class PackageMultiPluginTest {

    @Test
    fun testPackageMultiPlugin() {
        GradleRunner.create()
                .withProjectDir(PROJECT_DIR)
                .withPluginClasspath()
                .withArguments("clean")
                .build()

        val result = GradleRunner.create()
                .withProjectDir(PROJECT_DIR)
                .withPluginClasspath()
                .withArguments(listOf(
                        "-Pdisable_shadow_transform=true",
                        ":PackageMultiPlugin"
                ))
                .build()

        val outcome = result.task(":PackageMultiPlugin")!!.outcome

        Assert.assertEquals(TaskOutcome.SUCCESS, outcome)

        assertJson()

        assertFile()
    }

    private fun assertFile() {
        val zipFile = ZipFile(PROJECT_DIR.absolutePath + "/build/plugin-debug.zip")
        val zipFileNames = mutableSetOf<String>()
        zipFileNames.add("config.json")
        zipFileNames.add("case1-debug.apk")
        zipFileNames.add("loader-debug.apk")
        zipFileNames.add("runtime-debug.apk")

        var entries = zipFile.entries()
        Assert.assertEquals(4, zipFile.size())

        for (i in entries) {
            zipFileNames.remove(i.name)
        }
        Assert.assertEquals(0, zipFileNames.size)

        val case2ZipFile = ZipFile(PROJECT_DIR.absolutePath + "/build/plugin-case2Debug.zip")
        zipFileNames.add("config.json")
        zipFileNames.add("case2-debug.apk")

        entries = case2ZipFile.entries()
        Assert.assertEquals(2, case2ZipFile.size())

        for (i in entries) {
            zipFileNames.remove(i.name)
        }
        Assert.assertEquals(0, zipFileNames.size)
    }

    private fun assertJson() {
        val jsonFile = File(PROJECT_DIR, "build/intermediates/generatePluginConfig/debug/config.json")
        val json = JSONParser().parse(jsonFile.bufferedReader()) as JSONObject
        Assert.assertEquals(4L, json["version"])

        Assert.assertEquals("1.1.5", json["UUID_NickName"])

        val compactVersionArr: JSONArray = json["compact_version"] as JSONArray
        Assert.assertEquals(1L, compactVersionArr[0] as Long)

        val loaderJson = json["pluginLoader"] as JSONObject
        Assert.assertEquals("loader-debug.apk", loaderJson["apkName"])
        Assert.assertNotNull(loaderJson["hash"])

        val runtimeJson = json["runtime"] as JSONObject
        Assert.assertEquals("runtime-debug.apk", runtimeJson["apkName"])
        Assert.assertNotNull(runtimeJson["hash"])

        val pluginsJson = json["plugins"] as JSONArray
        val pluginJson = pluginsJson[0] as JSONObject
        Assert.assertEquals("demo_main", pluginJson["partKey"])
        Assert.assertEquals("case1-debug.apk", pluginJson["apkName"])
        val dependsOnJson = pluginJson["dependsOn"] as JSONArray
        Assert.assertEquals(2, dependsOnJson.size)
        Assert.assertNotNull(pluginJson["hash"])

        val case2JsonFile = File(PROJECT_DIR, "/case2/build/intermediates/generatePluginConfig/case2Debug/config.json")
        val case2Json = JSONParser().parse(case2JsonFile.bufferedReader()) as JSONObject
        Assert.assertEquals(case2Json["UUID"], json["UUID"])
    }

    companion object {
        val PROJECT_DIR = File("src/test/testProjects/case1")
    }
}