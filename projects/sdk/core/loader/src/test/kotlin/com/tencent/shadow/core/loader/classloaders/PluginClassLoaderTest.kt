package com.tencent.shadow.core.loader.classloaders

import org.junit.Assert
import org.junit.Test
import java.util.*

/**
 * @author zby
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2019-09-06
 * @description test String.inPackage(packageNames: Array<String>): Boolean
 * @usage click icon on the left of testString_inPackage()
 */
class PluginClassLoader {
    @Test
    fun testString_inPackage() {
        var packageName = "com.tencent.shadow.core.loader.classloaders.PluginClassLoader"
        Assert.assertTrue(packageName.inPackage(arrayOf(
                "com.tencent.shadow.core.loader.classloaders"
        )))
        packageName = "com.tencent.shadow.core.loader.classloaders.PluginClassLoader"
        Assert.assertFalse(packageName.inPackage(arrayOf(
                ""
        )))
        packageName = "com.tencent.shadow.core.loader.classloaders.PluginClassLoader"
        Assert.assertFalse(packageName.inPackage(arrayOf(
                "om.tencent.shadow"
        )))

        //support "a.b.c.*"
        packageName = "com.tencent.shadow.core.loader.classloaders.PluginClassLoader"
        Assert.assertTrue(packageName.inPackage(arrayOf(
                "com.tencent.shadow.*"
        )))
        //support ".*" everything can be access
        packageName = "com.tencent.shadow.core.loader.classloaders.PluginClassLoader"
        Assert.assertTrue(packageName.inPackage(arrayOf(
                ".*"
        )))
    }

    private fun String.inPackage(packageNames: Array<String>): Boolean {
        println(this + " in " + Arrays.toString(packageNames))
        val packageName = substringBeforeLast('.', "")
        return packageNames.any {
            if (it.endsWith(".*")) {
                val whiteListPackageName = it.substringBeforeLast(".*")
                println("!! [match .*] " + packageName.startsWith(whiteListPackageName))
                return packageName.startsWith(whiteListPackageName)
            }
            println("!! [no match .*] " + (packageName == it))
            packageName == it
        }
    }
}

