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
import javassist.*
import java.util.*

class PackageItemInfoTransform : SpecificTransform() {
    companion object {
        const val AndroidProviderInfo = "android.content.pm.ProviderInfo"
        const val AndroidActivityInfo = "android.content.pm.ActivityInfo"
        const val AndroidApplicationInfo = "android.content.pm.ApplicationInfo"
        const val AndroidServiceInfo = "android.content.pm.ServiceInfo"
        const val AndroidPackageItemInfo = "android.content.pm.PackageItemInfo"
        const val ShadowAndroidPackageItemInfo = "com.tencent.shadow.core.runtime.ShadowPackageItemInfo"
    }

    private fun setup(
            targetClassNames: Array<String>,
            targetMethodName: Array<String>,
            redirectRule: Pair<String, String>
    ) {
        val targetMethods = getTargetMethods(targetClassNames, targetMethodName)
        targetMethods.forEach { targetMethod ->
            newStep(object : TransformStep {
                override fun filter(allInputClass: Set<CtClass>) =
                        filterRefClasses(
                                allInputClass,
                                targetClassNames.asList()
                        ).filter { matchMethodCallInClass(targetMethod, it) }.toSet()

                override fun transform(ctClass: CtClass) {
                    System.out.println(ctClass.name + " matchMethodCallInClass :" + targetMethod.methodInfo.name + "  =================")
                    try {
                        val targetClass = mClassPool[redirectRule.first]
                        val redirectClassName = redirectRule.second
                        val parameterTypes: Array<CtClass> =
                                Array(targetMethod.parameterTypes.size + 1) { index ->
                                    if (index == 0) {
                                        targetClass
                                    } else {
                                        targetMethod.parameterTypes[index - 1]
                                    }
                                }
                        val newMethod = CtNewMethod.make(
                                Modifier.PUBLIC or Modifier.STATIC,
                                targetMethod.returnType,
                                targetMethod.name + "_shadow",
                                parameterTypes,
                                targetMethod.exceptionTypes,
                                null,
                                ctClass
                        )
                        val newBodyBuilder = StringBuilder()
                        newBodyBuilder
                                .append("return ")
                                .append(redirectClassName)
                                .append(".")
                                .append(targetMethod.methodInfo.name)
                                .append("(")
                                .append(ctClass.name)
                                .append(".class.getClassLoader(),")
                        for (i in 1..newMethod.parameterTypes.size) {
                            if (i > 1) {
                                newBodyBuilder.append(',')
                            }
                            newBodyBuilder.append("\$${i}")
                        }
                        newBodyBuilder.append(");")

                        newMethod.setBody(newBodyBuilder.toString())
                        ctClass.addMethod(newMethod)
                        val codeConverter = CodeConverter()
                        codeConverter.redirectMethodCallToStatic(targetMethod, newMethod)
                        ctClass.instrument(codeConverter)
                    } catch (e: Exception) {
                        System.err.println("处理" + ctClass.name + "时出错:" + e)
                        throw e
                    }
                }
            })
        }
    }

    /**
     * 查找目标class对象的目标method
     */
    private fun getTargetMethods(
            targetClassNames: Array<String>,
            targetMethodName: Array<String>
    ): List<CtMethod> {
        val method_targets = ArrayList<CtMethod>()
        for (targetClassName in targetClassNames) {
            val methods = mClassPool[targetClassName].methods
            method_targets.addAll(methods.filter { targetMethodName.contains(it.name) })
        }
        return method_targets
    }

    override fun setup(allInputClass: Set<CtClass>) {
        setup(
                arrayOf(
                        AndroidProviderInfo,
                        AndroidServiceInfo,
                        AndroidApplicationInfo,
                        AndroidActivityInfo
                ),
                arrayOf("loadXmlMetaData"),
                AndroidPackageItemInfo to ShadowAndroidPackageItemInfo
        )
    }
}