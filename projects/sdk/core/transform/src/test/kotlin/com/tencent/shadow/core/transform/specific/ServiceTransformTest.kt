package com.tencent.shadow.core.transform.specific

import com.tencent.shadow.core.transform.common.ShadowTransformTest
import org.junit.Assert
import org.junit.Test

class ServiceTransformTest : ShadowTransformTest() {

    @Test
    fun testApplicationTransform() {
        val targetClass = sLoader["test.TestService"]

        val allInputClass = setOf(targetClass)

        val applicationTransform = ServiceTransform()
        applicationTransform.mClassPool = sLoader
        applicationTransform.setup(allInputClass)

        applicationTransform.list.forEach { transform ->
            transform.filter(allInputClass).forEach {
                transform.transform(it)
            }
        }

        allInputClass.forEach {
            Assert.assertEquals(
                    "Service父类应该都变为了ShadowService",
                    "com.tencent.shadow.runtime.ShadowService",
                    it.classFile.superclass
            )
        }
    }
}