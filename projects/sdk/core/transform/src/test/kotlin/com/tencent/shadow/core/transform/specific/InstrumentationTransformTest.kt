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
                    "com.tencent.shadow.core.runtime.ShadowInstrumentation",
                    it.classFile.superclass
            )
        }
    }
}