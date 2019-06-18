/*
 * Tencent is pleased to support the open source community by making Tencent Shadow available.
 * Copyright (C) 2019 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tencent.shadow.core.transform.specific

import com.tencent.shadow.core.transform_kit.AbstractTransformTest
import javassist.NotFoundException
import org.junit.Assert
import org.junit.Test

class ActivityLifecycleCallbacksTransformTest : AbstractTransformTest() {

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
                        it.getMethod("get", "()Lcom/tencent/shadow/core/runtime/ShadowActivityLifecycleCallbacks;")
                        true
                    } catch (e: NotFoundException) {
                        false
                    }
            )

            Assert.assertEquals(
                    "ActivityLifecycleCallbacks接口应该都变为了ShadowActivityLifecycleCallbacks",
                    "com.tencent.shadow.core.runtime.ShadowActivityLifecycleCallbacks",
                    it.classFile.interfaces[0]
            )
        }
    }
}