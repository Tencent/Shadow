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

class DialogTransform : SpecificTransform() {
    companion object {
        const val AndroidDialogClassname = "android.app.Dialog"
        const val ShadowDialogClassname = "com.tencent.shadow.core.runtime.ShadowDialog"
    }

    private lateinit var codeConverter: CodeConverter

    val mAllDialogs: MutableList<String> = mutableListOf(ShadowDialogClassname)
    private fun CtClass.isDialog(): Boolean = isClassOf(AndroidDialogClassname)

    override fun setup(allInputClass: Set<CtClass>) {

        //收集哪些当前Transform的App类是Dialog
        newStep(object : TransformStep {
            override fun filter(allInputClass: Set<CtClass>): Set<CtClass> = allInputClass

            override fun transform(ctClass: CtClass) {
                if (ctClass.isDialog()) {
                    mAllDialogs.add(ctClass.name)
                }
            }
        })

        newStep(object : TransformStep {
            override fun filter(allInputClass: Set<CtClass>) = allInputClass

            override fun transform(ctClass: CtClass) {
                ctClass.defrost()
                ReplaceClassName.replaceClassName(ctClass, AndroidDialogClassname, ShadowDialogClassname)
            }

        })

        val dialogMethods = mClassPool[AndroidDialogClassname].methods!!
        val shadowDialogMethods = mClassPool[ShadowDialogClassname].methods!!
        codeConverter = CodeConverter()

        redirectMethodCall(
            dialogMethods.find { it.name == "getOwnerActivity" }!!,
            shadowDialogMethods.find { it.name == "getOwnerPluginActivity" }!!
        )

        redirectMethodCall(
            dialogMethods.find { it.name == "setOwnerActivity" }!!,
            shadowDialogMethods.find { it.name == "setOwnerPluginActivity" }!!
        )

        newStep(object : TransformStep {

            override fun filter(allInputClass: Set<CtClass>): Set<CtClass> =
                    allCanRecompileAppClass(allInputClass, mAllDialogs)

            override fun transform(ctClass: CtClass) {
                try {
                    ctClass.defrost()
                    ctClass.instrument(codeConverter)
                } catch (e: Exception) {
                    System.err.println("处理" + ctClass.name + "时出错")
                    throw e
                }
            }

        })
    }

    private fun redirectMethodCall(
        oldMethod: CtMethod,
        newMethod: CtMethod
    ) {
        oldMethod.copyDescriptorFrom(newMethod)
        //appClass中的Activity都已经被改名为ShadowActivity了．所以要把方法签名也先改一下．
        codeConverter.redirectMethodCall(oldMethod, newMethod)
    }

}
