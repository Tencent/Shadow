package com.tencent.shadow.core.transform.specific

import com.tencent.shadow.core.transform.common.ShadowTransformTest
import javassist.NotFoundException
import org.junit.Assert
import org.junit.Test

class ActivityLifecycleCallbacksTransformTest : ShadowTransformTest() {

    @Test
    fun testApplicationTransform() {
        val callbackTargetClass = sLoader["test.TestActivityLifecycleCallbacks"]

        val allInputClass = setOf(callbackTargetClass)

        val applicationTransform = ApplicationTransform()
        applicationTransform.mClassPool = sLoader
        applicationTransform.setup(allInputClass)

        applicationTransform.list.forEach { transform ->
            transform.filter(allInputClass).forEach {
                Assert.assertTrue(
                        "transform前应该能找到" + "get" + "方法",
                        try {
                            it.getMethod("get", "()Landroid/app/Application\$ActivityLifecycleCallbacks;")
                            true
                        } catch (e: NotFoundException) {
                            false
                        }
                )

                transform.transform(it)
            }
        }

        allInputClass.forEach {
            Assert.assertTrue("transform后应该能找不到" + "get" + "方法",
                    try {
                        it.getMethod("get", "()Landroid/app/Application\$ActivityLifecycleCallbacks;")
                        false
                    } catch (e: NotFoundException) {
                        true
                    }
            )

            Assert.assertTrue("transform后应该能找到新的" + "get" + "方法",
                    try {
                        it.getMethod("get", "()Lcom/tencent/shadow/runtime/ShadowActivityLifecycleCallbacks;")
                        true
                    } catch (e: NotFoundException) {
                        false
                    }
            )

            Assert.assertEquals(
                    "ActivityLifecycleCallbacks接口应该都变为了ShadowActivityLifecycleCallbacks",
                    "com.tencent.shadow.runtime.ShadowActivityLifecycleCallbacks",
                    it.classFile.interfaces[0]
            )
        }
    }
}