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

package com.tencent.shadow.core.transform_kit

import javassist.CodeConverter
import org.junit.Assert.assertEquals
import org.junit.Test


class RedirectMethodCallToStaticTest : AbstractTransformTest() {

    @Test
    fun redirectMethodCallToStaticMethodCall() {
        val targetClass = sLoader["test.MethodRedirectToStatic"]
        val staticClass = sLoader["test.MethodRedirectToStatic2"]

        val targetMethod = targetClass.getDeclaredMethod("add")
        val staticMethod = staticClass.getDeclaredMethod("add2")
        val conv = CodeConverter()

        conv.redirectMethodCallToStatic(targetMethod, staticMethod)
        targetClass.instrument(conv)
        targetClass.writeFile(WRITE_FILE_DIR)

        val obj = make(targetClass.name)
        assertEquals(30, invoke(obj, "test"))
    }
}