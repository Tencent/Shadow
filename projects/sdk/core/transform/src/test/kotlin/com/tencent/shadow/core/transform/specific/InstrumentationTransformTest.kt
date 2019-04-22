package com.tencent.shadow.core.transform.specific

import com.tencent.shadow.core.transform_kit.AbstractTransformTest
import org.junit.Assert
import org.junit.Test

class InstrumentationTransformTest : AbstractTransformTest() {

    @Test
    fun testInstrumentationTransform() {
        val targetClass = sLoader["test.TestInstrumentation"]

        val allInputClass = setOf(targetClass)

        val applicationTransform = InstrumentationTransform()
        applicationTransform.mClassPool = sLoader
        applicationTransform.setup(allInputClass)

        applicationTransform.list.forEach { transform ->
            transform.filter(allInputClass).forEach {
                transform.transform(it)
            }
        }

        allInputClass.forEach {
            Assert.assertEquals(
                    "Instrumentation父类应该都变为了ShadowInstrumentation",
                    "com.tencent.shadow.runtime.ShadowInstrumentation",
                    it.classFile.superclass
            )
        }
    }
}