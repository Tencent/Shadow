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

abstract class SimpleRenameTransformTest(private val renameTransform: SimpleRenameTransform,
                                         private val allInputClassName: Array<String>, private val methodName: String,
                                         private val newSuperClassName: String, private val methodFromToMap: Map<String, String>)
    : AbstractTransformTest() {

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