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

class PackageOnlyPluginTest {

    @Test
    fun testCase1PackageOnlyApk() {
        val projectDir = File("src/test/testProjects/case1")

        GradleRunner.create()
                .withProjectDir(projectDir)
                .withPluginClasspath()
                .withArguments("clean")
                .build()

        val result = GradleRunner.create()
                .withProjectDir(projectDir)
                .withPluginClasspath()
                .withArguments(listOf(
                        "-Pdisable_shadow_transform=true",
                        ":packageOnlyApkPlugin"
                ))
                .build()

        val outcome = result.task(":packageOnlyApkPlugin")!!.outcome

        Assert.assertEquals(TaskOutcome.SUCCESS, outcome)

        val jsonFile = File(projectDir, "build/intermediates/generatePluginConfig/onlyApk/config.json")
        val json = JSONParser().parse(jsonFile.bufferedReader()) as JSONObject
        assertJson(json)

        val zipFile = ZipFile(projectDir.absolutePath + "/build/plugin-onlyApk.zip")
        assertFile(zipFile)
    }

    private fun assertFile(zipFile: ZipFile) {
        val zipFileNames = mutableSetOf<String>()
        zipFileNames.add("config.json")
        zipFileNames.add("case1-debug.apk")

        val entries = zipFile.entries()
        Assert.assertEquals(2, zipFile.size())

        for (i in entries) {
            zipFileNames.remove(i.name)
        }
        Assert.assertEquals(0, zipFileNames.size)

    }

    private fun assertJson(json: JSONObject) {
        Assert.assertEquals(4L, json["version"])

        Assert.assertEquals("1234567890", json["UUID"])

        Assert.assertEquals("1.1.5", json["UUID_NickName"])

        val compactVersionArr: JSONArray = json["compact_version"] as JSONArray
        Assert.assertEquals(1L, compactVersionArr[0] as Long)


        val pluginsJson = json["plugins"] as JSONArray
        val pluginJson = pluginsJson[0] as JSONObject
        Assert.assertEquals("demo_main", pluginJson["partKey"])
        Assert.assertEquals("case1-debug.apk", pluginJson["apkName"])
        val dependsOnJson = pluginJson["dependsOn"] as JSONArray
        Assert.assertEquals(2, dependsOnJson.size)
        Assert.assertNotNull(pluginJson["hash"])
    }
}