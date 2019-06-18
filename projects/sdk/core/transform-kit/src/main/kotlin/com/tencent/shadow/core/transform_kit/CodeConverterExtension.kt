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

import javassist.CannotCompileException
import javassist.CodeConverter
import javassist.CtMethod
import javassist.NotFoundException
import javassist.bytecode.*
import javassist.convert.TransformCall
import javassist.convert.Transformer

class CodeConverterExtension : CodeConverter() {

    /**
     * 将一个方法调用改为静态方法调用，并将原被调用对象作为静态方法的第一个参数
     * @param origMethod 原方法
     * @param substMethod 新方法。必须为静态方法，且该方法签名与原方法一样，仅在第一个参数处多一个参数，类型为原方法的被调用类型。
     */
    @Throws(CannotCompileException::class)
    fun redirectMethodCallToStaticMethodCall(origMethod: CtMethod, substMethod: CtMethod) {

        class Transform(
                next: Transformer?,
                origMethod: CtMethod,
                substMethod: CtMethod
        ) : TransformCall(next, origMethod, substMethod) {
            init {
                methodDescriptor = origMethod.methodInfo2.descriptor
            }

            @Throws(BadBytecode::class)
            override fun match(c: Int, pos: Int, iterator: CodeIterator,
                               typedesc: Int, cp: ConstPool): Int {
                if (newIndex == 0) {
                    val desc = Descriptor.insertParameter(classname, methodDescriptor)
                    val nt = cp.addNameAndTypeInfo(newMethodname, desc)
                    val ci = cp.addClassInfo(newClassname)
                    newIndex = cp.addMethodrefInfo(ci, nt)
                    constPool = cp
                }
                iterator.writeByte(Opcode.INVOKESTATIC, pos)
                iterator.write16bit(newIndex, pos + 1)
                return pos
            }
        }

        try {
            transformers = Transform(transformers, origMethod, substMethod)
        } catch (e: NotFoundException) {
            throw CannotCompileException(e)
        }

    }
}