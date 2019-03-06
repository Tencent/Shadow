package com.tencent.shadow.core.gradle

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome.SUCCESS
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

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
        assertEquals(4L, json["version"])
    }
}