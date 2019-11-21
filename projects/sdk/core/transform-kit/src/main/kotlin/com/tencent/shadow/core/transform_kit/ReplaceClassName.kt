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
    private val mNewNames = mutableSetOf<String>()
    /**
     * MutableMap<defClass, MutableMap<method, MutableSet<useClass>>>
     */
    private val errorResult: MutableMap<String, MutableMap<String, MutableSet<String>>> = mutableMapOf()

    fun resetErrorCount() {
        mNewNames.clear()
        errorResult.clear()
    }

    fun replaceClassName(ctClass: CtClass, oldName: String, newName: String) {
        ctClass.replaceClassName(oldName, newName)
        mNewNames.add(newName)
    }

    fun checkAll(classPool: ClassPool, inputClassNames: List<String>): Map<String, Map<String, Set<String>>> {
        inputClassNames.forEach { inputClassName ->
            val inputClass = classPool[inputClassName]
            if (inputClass.refClasses.any { mNewNames.contains(it) }) {
                mNewNames.forEach { newName ->
                    inputClass.checkMethodExist(classPool[newName])
                }
            }
        }
        return errorResult
    }

    /**
     * 检查ctClass对refClassName引用的方法确实都存在
     */
    private fun CtClass.checkMethodExist(refClass: CtClass) {
        val invokeClass = name
        val refClassName = refClass.name
        instrument(object : ExprEditor() {
            override fun edit(m: MethodCall) {
                if (m.className == refClassName) {
                    try {
                        refClass.getMethod(m.methodName, m.signature)
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