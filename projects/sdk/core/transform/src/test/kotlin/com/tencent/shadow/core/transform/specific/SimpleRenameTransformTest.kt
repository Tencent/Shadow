package com.tencent.shadow.core.transform.specific

import com.tencent.shadow.core.transform.common.ShadowTransformTest
import javassist.NotFoundException
import org.junit.Assert

abstract class SimpleRenameTransformTest(private val renameTransform: SimpleRenameTransform,
                                         private val allInputClassName: Array<String>, private val methodName: String,
                                         private val newSuperClassName: String, private val methodFromToMap: Map<String, String>)
    : ShadowTransformTest() {

    protected fun doTest() {
        renameTransform.mClassPool = sLoader
        val allInputClass = sLoader[allInputClassName].toMutableSet()
        renameTransform.setup(allInputClass)

        renameTransform.list.forEach { transform ->
            transform.filter(allInputClass).forEach {
                Assert.assertTrue(
                        "transform前应该能找到" + methodName + "方法",
                        try {
                            it.getMethod(methodName, methodFromToMap.entries.first().key)
                            true
                        } catch (e: NotFoundException) {
                            false
                        }
                )

                transform.transform(it)
            }
        }

        allInputClass.forEach {
            Assert.assertEquals("父类应该都变为了新的父类", it.classFile.superclass, newSuperClassName)

            Assert.assertTrue("原来的方法应该找不到了",
                    try {
                        it.getMethod(methodName, methodFromToMap.entries.first().key)
                        false
                    } catch (e: NotFoundException) {
                        true
                    })

            Assert.assertTrue("应该能找到签名变化了的方法",
                    try {
                        it.getMethod(methodName, methodFromToMap.entries.first().value)
                        true
                    } catch (e: NotFoundException) {
                        false
                    }
            )
        }
    }
}