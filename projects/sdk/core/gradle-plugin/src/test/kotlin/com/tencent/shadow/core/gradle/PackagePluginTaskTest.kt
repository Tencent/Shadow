package com.tencent.shadow.core.gradle

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome.SUCCESS
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.io.File
import java.util.zip.ZipFile

class PackagePluginTaskTest {

    @Test
    fun testCase1PackageDebugPlugin() {
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
                        ":packageDebugPlugin"
                ))
                .build()

        val outcome = result.task(":packageDebugPlugin")!!.outcome

        assertEquals(SUCCESS, outcome)

        val jsonFile = File(projectDir, "build/intermediates/generatePluginConfig/debug/config.json")
        val json = JSONParser().parse(jsonFile.bufferedReader()) as JSONObject
        assertJson(json)

        val zipFile = ZipFile(projectDir.absolutePath + "/build/plugin-debug.zip")
        assertFile(zipFile)
    }

    private fun assertFile(zipFile: ZipFile) {
        val zipFileNames = mutableSetOf<String>()
        zipFileNames.add("config.json")
        zipFileNames.add("case1-debug.apk")
        zipFileNames.add("loader-debug.apk")
        zipFileNames.add("runtime-debug.apk")

        val entries = zipFile.entries()
        assertEquals(4, zipFile.size())

        for (i in entries) {
            zipFileNames.remove(i.name)
        }
        assertEquals(0, zipFileNames.size)

    }

    private fun assertJson(json: JSONObject) {
        assertEquals(4L, json["version"])

        assertEquals("1234567890", json["UUID"])

        assertEquals("1.1.5", json["UUID_NickName"])

        val compactVersionArr: JSONArray = json["compact_version"] as JSONArray
        assertEquals(1L, compactVersionArr[0] as Long)

        val loaderJson = json["pluginLoader"] as JSONObject
        assertEquals("loader-debug.apk", loaderJson["apkName"])
        assertNotNull(loaderJson["hash"])

        val runtimeJson = json["runtime"] as JSONObject
        assertEquals("runtime-debug.apk", runtimeJson["apkName"])
        assertNotNull(runtimeJson["hash"])

        val pluginsJson = json["plugins"] as JSONArray
        val pluginJson = pluginsJson[0] as JSONObject
        assertEquals("demo_main", pluginJson["partKey"])
        assertEquals("case1-debug.apk", pluginJson["apkName"])
        val dependsOnJson = pluginJson["dependsOn"] as JSONArray
        assertEquals(2, dependsOnJson.size)
        assertNotNull(pluginJson["hash"])
    }
}