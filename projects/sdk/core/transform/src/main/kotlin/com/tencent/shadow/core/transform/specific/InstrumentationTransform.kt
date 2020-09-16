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

import com.tencent.shadow.core.transform_kit.ReplaceClassName
import com.tencent.shadow.core.transform_kit.SpecificTransform
import com.tencent.shadow.core.transform_kit.TransformStep
import javassist.CodeConverter
import javassist.CtClass
import javassist.CtMethod

class InstrumentationTransform : SpecificTransform() {
    companion object {
        const val AndroidInstrumentationClassname = "android.app.Instrumentation"
        const val ShadowInstrumentationClassname = "com.tencent.shadow.core.runtime.ShadowInstrumentation"
    }

    override fun setup(allInputClass: Set<CtClass>) {
        val shadowInstrumentation = mClassPool[ShadowInstrumentationClassname]

        val newShadowApplicationMethods = shadowInstrumentation.getDeclaredMethods("newShadowApplication")

        val newShadowActivityMethod = shadowInstrumentation.getDeclaredMethod("newShadowActivity")

        newStep(object : TransformStep {
            override fun filter(allInputClass: Set<CtClass>) = allInputClass

            override fun transform(ctClass: CtClass) {
                ReplaceClassName.replaceClassName(
                        ctClass,
                        AndroidInstrumentationClassname,
                        ShadowInstrumentationClassname
                )
            }
        })
        newStep(object : TransformStep {
            override fun filter(allInputClass: Set<CtClass>) = allInputClass

            override fun transform(ctClass: CtClass) {
                ctClass.defrost()
                val codeConverter = CodeConverter()
                newShadowApplicationMethods.forEach { codeConverter.redirectMethodCall("newApplication", it) }

                codeConverter.redirectMethodCall("newActivity", newShadowActivityMethod)
                try {
                    ctClass.instrument(codeConverter)
                } catch (e: Exception) {
                    System.err.println("处理" + ctClass.name + "时出错:" + e)
                    throw e
                }
            }
        })
    }
}
