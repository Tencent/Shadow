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

import com.tencent.shadow.core.transform_kit.SpecificTransform
import com.tencent.shadow.core.transform_kit.TransformStep
import javassist.CodeConverter
import javassist.CtClass
import javassist.bytecode.Descriptor

/**
 * ActivityOptions有些方法带有Activity类型参数，
 * 由于它是系统类，不会被Transform修改，所以我们需要将插件中调用这些方法的代码修改一下。
 * 转调一下，转换ShadowActivity到PluginContainerActivity，再调用原方法。
 */
class ActivityOptionsSupportTransform : SpecificTransform() {
    companion object {
        const val ActivityOptionsClassname = "android.app.ActivityOptions"
        const val ActivityOptionsSupportClassname =
            "com.tencent.shadow.core.runtime.ActivityOptionsSupport"
        const val ActivityClassname = "android.app.Activity"
        const val ShadowActivityClassname = "com.tencent.shadow.core.runtime.ShadowActivity"
        const val makeSceneTransitionAnimationMethodName = "makeSceneTransitionAnimation"
        fun makeSceneTransitionAnimationMethodSig1(activityClassname: String) =
            "(L${Descriptor.toJvmName(activityClassname)};" +
                    "Landroid/view/View;Ljava/lang/String;)Landroid/app/ActivityOptions;"

        fun makeSceneTransitionAnimationMethodSig2(activityClassname: String) =
            "(L${Descriptor.toJvmName(activityClassname)};" +
                    "[Landroid/util/Pair;)Landroid/app/ActivityOptions;"
    }

    override fun setup(allInputClass: Set<CtClass>) {
        newStep(object : TransformStep {
            val codeConverter = CodeConverter()

            init {
                val activityOptionsClass = mClassPool[ActivityOptionsClassname]
                val activityOptionsSupportClass = mClassPool[ActivityOptionsSupportClassname]

                listOf(
                    ::makeSceneTransitionAnimationMethodSig1,
                    ::makeSceneTransitionAnimationMethodSig2,
                ).forEach { sig ->
                    val originalMethod = activityOptionsClass.getMethod(
                        makeSceneTransitionAnimationMethodName,
                        sig(ActivityClassname)
                    )
                    //appClass中的Activity都已经被改名为ShadowActivity了．所以要把方法签名也先改一下．
                    originalMethod.methodInfo.descriptor =
                        sig(ShadowActivityClassname)

                    val supportMethod = activityOptionsSupportClass.getMethod(
                        makeSceneTransitionAnimationMethodName,
                        sig(ShadowActivityClassname)
                    )
                    codeConverter.redirectMethodCall(originalMethod, supportMethod)
                }
            }

            override fun filter(allInputClass: Set<CtClass>) =
                filterRefClasses(allInputClass, listOf(ActivityOptionsClassname))

            override fun transform(ctClass: CtClass) {
                ctClass.defrost()
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