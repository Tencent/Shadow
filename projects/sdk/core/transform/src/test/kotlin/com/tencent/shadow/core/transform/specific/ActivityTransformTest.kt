package com.tencent.shadow.core.transform.specific

import com.tencent.shadow.core.transform.common.ShadowTransformTest
import javassist.NotFoundException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ActivityTransformTest : ShadowTransformTest() {

    @Test
    fun testActivityTransform() {
        val targetClass = sLoader["test.TestActivity"]

        val allInputClass = setOf(targetClass)

        val activityTransform = ActivityTransform()
        activityTransform.mClassPool = sLoader
        activityTransform.setup(allInputClass)

        activityTransform.list.forEach { transform ->
            transform.filter(allInputClass).forEach {
                assertTrue(
                        "transform前应该能找到foo方法",
                        try {
                            it.getMethod(
                                    "foo",
                                    "(Landroid/app/Activity;)Landroid/app/Activity;"
                            )
                            true
                        } catch (e: NotFoundException) {
                            false
                        }
                )

                transform.transform(it)
            }
        }

        allInputClass.forEach {
            assertEquals(
                    "Activity父类应该都变为了ShadowActivity",
                    "com.tencent.shadow.runtime.ShadowActivity",
                    it.classFile2.superclass
            )

            assertTrue("原来的foo方法应该找不到了",
                    try {
                        it.getMethod("foo",
                                "(Landroid/app/Activity;)Landroid/app/Activity;")
                        false
                    } catch (e: NotFoundException) {
                        true
                    })

            assertTrue(
                    "应该能找到签名变化了的foo方法",
                    try {
                        it.getMethod(
                                "foo",
                                "(Lcom/tencent/shadow/runtime/ShadowActivity;)" +
                                        "Lcom/tencent/shadow/runtime/ShadowActivity;"
                        )
                        true
                    } catch (e: NotFoundException) {
                        false
                    }
            )
        }

    }
}