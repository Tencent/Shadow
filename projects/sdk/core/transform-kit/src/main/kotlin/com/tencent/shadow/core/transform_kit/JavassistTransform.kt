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
import org.gradle.api.Project
import java.io.File
import java.io.OutputStream
import java.util.zip.ZipInputStream

open class JavassistTransform(project: Project, val classPoolBuilder: ClassPoolBuilder) :
    ClassTransform(project) {
    /**
     * 所有classPool.makeClass生成的CtClass存起来，
     * 等AbstractTransformManager处理类时直接从这里
     * 拿到所有CtClass作为字节码编辑的输入数据。
     */
    val allInputCtClass = mutableSetOf<CtClass>()
    lateinit var classPool: ClassPool

    override fun onOutputClass(className: String, outputStream: OutputStream) {
        classPool[className].writeOut(outputStream)
    }

    override fun loadDotClassFile(classFile: File): String {
        val ctClass: CtClass = classFile.inputStream().use {
            classPool.makeClass(it)
        }
        allInputCtClass.add(ctClass)
        return ctClass.name
    }

    override fun loadClassFromJar(zipInputStream: ZipInputStream): String {
        val ctClass = classPool.makeClass(zipInputStream)
        allInputCtClass.add(ctClass)
        return ctClass.name
    }

    override fun beforeTransform() {
        super.beforeTransform()
        allInputCtClass.clear()
        classPool = classPoolBuilder.build()
    }


    override fun onTransform() {
        //do nothing.
    }

    fun CtClass.writeOut(output: OutputStream) {
        this.toBytecode(java.io.DataOutputStream(output))
    }

}

interface ClassPoolBuilder {
    fun build(): ClassPool
}