package com.tencent.shadow.core.manifest_parser

import org.junit.Assert
import org.junit.Test
import java.io.File

class PluginManifestGeneratorTest {

    @Test
    fun testCompileCaseAsLittleAsPossible() {
        testCompile("case_as_little_as_possible.xml")
    }

    @Test
    fun testNoAppComponentFactory() {
        testCompile("noAppComponentFactory.xml")
    }

    @Test
    fun testCompileSampleApp() {
        testCompile("sample-app.xml")
    }

    private fun testCompile(case: String) {
        val testFile = File(javaClass.classLoader.getResource(case)!!.toURI())
        val androidManifest = AndroidManifestReader().read(testFile)
        val generator = PluginManifestGenerator()

        val tempBuildDir = File("build", "PluginManifestGeneratorTest")
        val outputDir = File(tempBuildDir, case)
        println("outputDir==$outputDir")
        generator.generate(androidManifest, outputDir, "test")

        val cmd = "javac -cp ../runtime/build/classes/java/main:build/classes/java/test" +
                " ${outputDir.absolutePath}/test/PluginManifest.java"
        val process = Runtime.getRuntime().exec(cmd)
        val ret = process.waitFor()
        Assert.assertEquals(cmd, 0, ret)
    }
}