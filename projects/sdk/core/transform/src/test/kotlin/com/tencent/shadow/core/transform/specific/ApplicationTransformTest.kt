package com.tencent.shadow.core.transform.specific

import com.tencent.shadow.core.transform.common.ShadowTransformTest
import org.junit.Assert
import org.junit.Test

class ApplicationTransformTest : ShadowTransformTest() {

    @Test
    fun testApplicationTransform() {
        val applicationTargetClass = sLoader["test.TestApplication"]
        val callbackTargetClass = sLoader["test.TestActivityLifecycleCallbacks"]

        val allInputClass = setOf(applicationTargetClass, callbackTargetClass)

        val applicationTransform = ApplicationTransform()
        applicationTransform.mClassPool = sLoader
        applicationTransform.setup(allInputClass)

        applicationTransform.list.forEach { transform ->
            transform.filter(allInputClass).forEach {
                transform.transform(it)
            }
        }

        allInputClass.forEach {
            if (it.classFile.name == "test.TestApplication") {
                Assert.assertEquals(
                        "Application父类应该都变为了ShadowApplication",
                        "com.tencent.shadow.runtime.ShadowApplication",
                        it.classFile.superclass
                )
            } else if (it.classFile.name == "test.TestActivityLifecycleCallbacks") {
                Assert.assertEquals(
                        "ActivityLifecycleCallbacks接口应该都变为了ShadowActivityLifecycleCallbacks",
                        "com.tencent.shadow.runtime.ShadowActivityLifecycleCallbacks",
                        it.classFile.interfaces[0]
                )
            }
        }
    }
}