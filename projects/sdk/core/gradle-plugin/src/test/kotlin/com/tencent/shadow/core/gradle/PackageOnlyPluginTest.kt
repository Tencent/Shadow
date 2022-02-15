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
 *  测试打包只包含、插件1、config.json的插件包
 * ./gradlew -p projects/sdk/core :gradle-plugin:test --tests com.tencent.shadow.core.gradle.PackageOnlyPluginTest.testCase1PackageOnlyApk
 */
class PackageOnlyPluginTest {

    @Test
    fun testCase1PackageOnlyApk() {
        GradleRunner.create()
            .withProjectDir(PLUGIN1_PROJECT_DIR)
            .withPluginClasspath()
            .withArguments("clean")
            .build()

        val result = GradleRunner.create()
            .withProjectDir(PLUGIN1_PROJECT_DIR)
            .withPluginClasspath()
            .withArguments(
                listOf(
                    "-xgeneratePluginDebugPluginManifest",
                    "-Pdisable_shadow_transform=true",
                    ":plugin1:packageOnlyApkPlugin"
                )
            )
            .build()

        val outcome = result.task(":plugin1:packageOnlyApkPlugin")!!.outcome

        Assert.assertEquals(TaskOutcome.SUCCESS, outcome)

        val jsonFile = File(
            PLUGIN1_PROJECT_DIR,
            "build/intermediates/generatePluginConfig/onlyApk/config.json"
        )
        val json = JSONParser().parse(jsonFile.bufferedReader()) as JSONObject
        assertJson(json)

        val zipFile = ZipFile(ROOT_PROJECT_DIR.absolutePath + "/build/plugin-onlyApk.zip")
        assertFile(zipFile)
    }

    private fun assertFile(zipFile: ZipFile) {
        val zipFileNames = mutableSetOf<String>()
        zipFileNames.add("config.json")
        zipFileNames.add("plugin1-plugin-debug.apk")

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
        Assert.assertEquals("plugin1", pluginJson["partKey"])
        Assert.assertEquals("plugin1-plugin-debug.apk", pluginJson["apkName"])
        val dependsOnJson = pluginJson["dependsOn"] as JSONArray
        Assert.assertEquals(2, dependsOnJson.size)
        Assert.assertNotNull(pluginJson["hash"])

        val hostWhiteList = pluginJson["hostWhiteList"] as JSONArray
        Assert.assertEquals(2, hostWhiteList.size)
    }

    companion object {
        val ROOT_PROJECT_DIR = File("src/test/testProjects/case1")
        val PLUGIN1_PROJECT_DIR = File("src/test/testProjects/case1/plugin1")
    }

}