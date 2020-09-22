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
import javassist.CtMethod
import javassist.NotFoundException
import javassist.bytecode.CodeAttribute
import javassist.bytecode.MethodInfo
import javassist.bytecode.Opcode

abstract class SpecificTransform {
    private val _list = mutableListOf<TransformStep>()

    val list: List<TransformStep> = _list

    lateinit var mClassPool: ClassPool

    fun newStep(transform: TransformStep) {
        _list.add(transform)
    }

    abstract fun setup(allInputClass: Set<CtClass>)

    fun CtMethod.copyDescriptorFrom(other: CtMethod) {
        methodInfo.descriptor = other.methodInfo.descriptor
    }

    /**
     * 过滤引用了某些类型的类
     */
    fun filterRefClasses(allAppClass: Set<CtClass>, targetClassList: List<String>) =
            allAppClass.filter { ctClass ->
                targetClassList.any { targetClass ->
                    ctClass.refClasses.contains(targetClass)
                }
            }.toSet()

    fun CtClass.isClassOf(className: String): Boolean {
        var tmp: CtClass? = this
        do {
            if (tmp?.name == className) {
                return true
            }
            try {
                tmp = tmp?.superclass
            } catch (e: NotFoundException) {
                return false
            }
        } while (tmp != null)
        return false
    }

    /**
     * 查找目标class是否存在目标method的调用
     */
    fun matchMethodCallInClass(ctMethod: CtMethod, clazz: CtClass): Boolean {
        for (methodInfo in clazz.classFile2.methods) {
            methodInfo as MethodInfo
            val codeAttr: CodeAttribute? = methodInfo.codeAttribute
            val constPool = methodInfo.constPool
            if (codeAttr != null) {
                val iterator = codeAttr.iterator()
                while (iterator.hasNext()) {
                    val pos = iterator.next()
                    val c = iterator.byteAt(pos)
                    if (c == Opcode.INVOKEINTERFACE || c == Opcode.INVOKESPECIAL
                            || c == Opcode.INVOKESTATIC || c == Opcode.INVOKEVIRTUAL) {
                        val index = iterator.u16bitAt(pos + 1)
                        val cname = constPool.eqMember(ctMethod.name, ctMethod.methodInfo2.descriptor, index)
                        val className = ctMethod.declaringClass.name
                        val matched = cname != null && matchClass(ctMethod.name, ctMethod.methodInfo.descriptor, className, cname, clazz.classPool)
                        if (matched) {
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

    private fun matchClass(methodName: String, methodDescriptor: String, classname: String, name: String, pool: ClassPool): Boolean {
        if (classname == name)
            return true

        try {
            val clazz = pool.get(name)
            val declClazz = pool.get(classname)
            if (clazz.subtypeOf(declClazz))
                try {
                    val m = clazz.getMethod(methodName, methodDescriptor)
                    return m.declaringClass.name == classname
                } catch (e: NotFoundException) {
                    // maybe the original method has been removed.
                    return true
                }

        } catch (e: NotFoundException) {
            return false
        }

        return false
    }
}