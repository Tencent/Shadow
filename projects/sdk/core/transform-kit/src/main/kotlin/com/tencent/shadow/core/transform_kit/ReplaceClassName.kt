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

import javassist.ClassPool
import javassist.CtClass
import javassist.NotFoundException
import javassist.expr.ExprEditor
import javassist.expr.MethodCall

object ReplaceClassName {
    private val oldToNewRenameMap = mutableMapOf<String, String>()

    /**
     * MutableMap<defClass, MutableMap<method, MutableSet<useClass>>>
     */
    private val errorResult: MutableMap<String, MutableMap<String, MutableSet<String>>> =
        mutableMapOf()

    fun resetErrorCount() {
        oldToNewRenameMap.clear()
        errorResult.clear()
    }

    fun replaceClassName(ctClass: CtClass, oldName: String, newName: String) {
        ctClass.replaceClassName(oldName, newName)
        oldToNewRenameMap[oldName] = newName
    }

    fun checkAll(
        classPool: ClassPool,
        inputClassNames: List<String>
    ): Map<String, Map<String, Set<String>>> {
        inputClassNames.forEach { inputClassName ->
            val inputClass = classPool[inputClassName]
            val oldNames = oldToNewRenameMap.keys
            val newNames = oldToNewRenameMap.values
            if (inputClass.refClasses.any { newNames.contains(it) }) {
                oldNames.forEach { oldName ->
                    val newName = oldToNewRenameMap[oldName]
                    inputClass.checkMethodExist(classPool[oldName], classPool[newName])
                }
            }
        }
        return errorResult
    }

    /**
     * 检查ctClass对refClassName引用的方法确实都存在
     */
    private fun CtClass.checkMethodExist(oldClass: CtClass, newClass: CtClass) {
        val invokeClass = name
        val refClassName = newClass.name
        instrument(object : ExprEditor() {
            override fun edit(m: MethodCall) {
                if (m.className == refClassName) {
                    try {
                        oldClass.getMethod(m.methodName, m.signature)
                    } catch (ignored: NotFoundException) {
                        //替换前旧的类就没有这个方法，就不用管替换后的类是否实现了。
                        return
                    }

                    try {
                        newClass.getMethod(m.methodName, m.signature)
                    } catch (ignored: NotFoundException) {
                        val methodString = "${m.methodName}:${m.signature}"
                        var methodMap = errorResult[refClassName]
                        if (methodMap == null) {
                            methodMap = mutableMapOf()
                            errorResult[refClassName] = methodMap
                        }
                        var useSet = methodMap[methodString]
                        if (useSet == null) {
                            useSet = mutableSetOf()
                            methodMap[methodString] = useSet
                        }
                        useSet.add(invokeClass)
                    }
                }
            }
        })
    }
}