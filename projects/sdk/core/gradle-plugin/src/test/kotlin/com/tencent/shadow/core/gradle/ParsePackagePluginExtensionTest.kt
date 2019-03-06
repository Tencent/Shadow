package com.tencent.shadow.core.gradle

import com.tencent.shadow.core.gradle.extensions.PackagePluginExtension
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ParsePackagePluginExtensionTest {
    companion object {
        const val EXTENSION_NAME = "packagePlugin"
    }

    private val project = ProjectBuilder.builder().build()

    @Before
    fun setUp() {
        project.extensions.create(EXTENSION_NAME, PackagePluginExtension::class.java, project)
    }

    @Test
    fun testBasicVersion() {
        val testVersion = 4
        val testUuidNickName = "1.1.5"

        project.extensions.configure(PackagePluginExtension::class.java) { packagePlugin ->
            packagePlugin.apply {
                version = testVersion
                uuidNickName = testUuidNickName
            }
        }

        val extension = project.extensions.getByType(PackagePluginExtension::class.java)

        Assert.assertEquals(testVersion, extension.version)
        Assert.assertEquals(testUuidNickName, extension.uuidNickName)
    }
}