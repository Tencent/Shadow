package com.tencent.shadow.core.manifest_parser

import org.junit.Assert
import org.junit.Test
import java.io.File

class AndroidManifestReaderTest {
    @Test
    fun testReadXml() {
        val testFile = File(javaClass.classLoader.getResource("sample-app.xml")!!.toURI())
        val androidManifest = AndroidManifestReader().read(testFile)
        Assert.assertEquals(
            "com.tencent.shadow.sample.host",
            androidManifest[AndroidManifestKeys.`package`]
        )
        Assert.assertEquals(
            "com.tencent.shadow.sample.plugin.app.lib.UseCaseApplication",
            androidManifest[AndroidManifestKeys.name]
        )
        Assert.assertEquals(
            "com.tencent.shadow.test.plugin.androidx_cases.lib.TestComponentFactory",
            androidManifest[AndroidManifestKeys.appComponentFactory]
        )
        Assert.assertEquals(
            "@ref/0x01030006",
            androidManifest[AndroidManifestKeys.theme]
        )
    }
}