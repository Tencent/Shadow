package com.tencent.shadow.core.transform_kit

import javassist.ClassPool
import javassist.CtClass
import org.apache.commons.io.FileUtils
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.File

class OverrideCheckTest : AbstractTransformTest() {
    private lateinit var inputClasses: Array<CtClass>
    private val overrideCheck = OverrideCheck()
    private lateinit var overrideMap: Map<String, Collection<Method_OriginalDeclaringClass>>
    private lateinit var errorResult: Map<String, List<Method_OriginalDeclaringClass>>

    @Before
    fun setUp() {
        val buildDir = File(WRITE_FILE_DIR)
        if (buildDir.exists()) {
            FileUtils.cleanDirectory(buildDir)
        }

        inputClasses = ClassPool(true).get(
            arrayOf(
                "test.override.Bar",
                "test.override.Foo"
            )
        )
        overrideCheck.prepare(inputClasses.toSet())
        overrideMap = overrideCheck.getOverrideMethods()
        replaceClass(inputClasses)
        errorResult = overrideCheck.check(dLoader, inputClasses.map { it.name })
    }

    private fun replaceClass(inputClasses: Array<CtClass>) {
        inputClasses.forEach {
            it.replaceClassName("test.override.Foo", "test.override.Foo_")
            it.replaceClassName("test.override.Arg", "test.override.NewArg")
            it.replaceClassName("test.override.Super", "test.override.NewSuper")
            it.writeFile(WRITE_FILE_DIR)
        }
    }

    @Test
    fun testFooFindAll() {
        val name = "test.override.Foo"
        findAllOverrideMethods(name)
    }

    @Test
    fun testFooFindError() {
        val name = "test.override.Foo_"
        Assert.assertTrue(errorResult.contains(name))
        val error = errorResult[name]!!
        Assert.assertEquals(1, error.size)
        Assert.assertTrue(
            error.any {
                it.first.name == "s2"
            }
        )
    }

    @Test
    fun testBarFindAll() {
        val name = "test.override.Bar"
        findAllOverrideMethods(name)
    }

    @Test
    fun testBarFindError() {
        val name = "test.override.Bar"
        Assert.assertFalse(errorResult.contains(name))
    }

    private fun findAllOverrideMethods(name: String) {
        val overrideMethods = overrideMap[name]!!
        Assert.assertTrue(
            overrideMethods.any {
                it.first.name == "ss1"
            }
        )

        Assert.assertTrue(
            overrideMethods.any {
                it.first.name == "s1"
            }
        )

        Assert.assertTrue(
            overrideMethods.any {
                it.first.name == "s2"
            }
        )
    }

}