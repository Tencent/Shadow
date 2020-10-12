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

import com.android.build.api.transform.TransformInvocation
import javassist.ClassPool
import javassist.CtClass
import org.gradle.api.Project
import java.io.*
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.system.measureTimeMillis

abstract class AbstractTransform(
        project: Project,
        classPoolBuilder: ClassPoolBuilder
) : JavassistTransform(project, classPoolBuilder) {

    protected abstract val mTransformManager: AbstractTransformManager
    private val mOverrideCheck = OverrideCheck()
    private lateinit var mDebugClassJar: File
    private lateinit var mDebugClassJarZOS: ZipOutputStream


    private fun cleanDebugClassFileDir() {
        val transformTempDir = File(project.buildDir, "transform-temp")
        transformTempDir.mkdirs()
        mDebugClassJar = File.createTempFile("transform-temp", ".jar", transformTempDir)
        mDebugClassJarZOS = ZipOutputStream(FileOutputStream(mDebugClassJar))
    }

    override fun beforeTransform(invocation: TransformInvocation) {
        super.beforeTransform(invocation)
        ReplaceClassName.resetErrorCount()
        cleanDebugClassFileDir()
    }

    override fun onTransform() {
        //Fixme: 这里的OverrideCheck.prepare会对mCtClassInputMap产生影响
        //原本预期是不会产生任何影响的。造成了ApplicationInfoTest失败，测试Activity没有被修改superclass。
//        mOverrideCheck.prepare(mCtClassInputMap.keys.toSet())

        mTransformManager.setupAll()
        mTransformManager.fireAll()
    }

    override fun afterTransform(invocation: TransformInvocation) {
        super.afterTransform(invocation)

        mDebugClassJarZOS.flush()
        mDebugClassJarZOS.close()

        //CtClass在编辑后，其对象中的各种信息，比如superClass并没有更新。
        //所以需要重新创建一个ClassPool，加载转换后的类，用于各种转换后的检查。
        val debugClassPool = classPoolBuilder.build()
        debugClassPool.appendClassPath(mDebugClassJar.absolutePath)
        val inputClassNames = mCtClassInputMap.keys.map { it.name }
        onCheckTransformedClasses(debugClassPool, inputClassNames)
    }

    override fun onOutputClass(className: String, outputStream: OutputStream) {
        classPool[className].debugWriteJar(mDebugClassJarZOS)
        super.onOutputClass(className, outputStream)
    }

    private fun CtClass.debugWriteJar(outputStream: ZipOutputStream) {
        //忽略Kotlin 1.4引入的module-info
        //https://kotlinlang.org/docs/reference/whatsnew14.html#module-info-descriptors-for-stdlib-artifacts
        if (name == "module-info") {
            return
        }

        try {
            val entryName = (name.replace('.', '/') + ".class")
            outputStream.putNextEntry(ZipEntry(entryName))
            val p = stopPruning(true)
            toBytecode(DataOutputStream(outputStream))
            defrost()
            stopPruning(p)
        } catch (e: Exception) {
            outputStream.close()
            throw RuntimeException(e)
        }
    }

    open fun onCheckTransformedClasses(debugClassPool: ClassPool, classNames: List<String>) {
        var delayException: Exception? = null
        val start1 = System.currentTimeMillis()
        try {
            checkReplacedClassHaveRightMethods(debugClassPool, classNames)
        } catch (e: Exception) {
            if (delayException == null) {
                delayException = e
            } else {
                delayException.addSuppressed(e)
            }
        }
        project.logger.info("checkReplacedClassHaveRightMethods完毕，耗时(ms):${System.currentTimeMillis() - start1}")

        val start2 = System.currentTimeMillis()
        try {
            val t2 = measureTimeMillis {
                //                checkOverrideMethods(debugClassPool, classNames)
            }
            System.err.println("t2:$t2")
        } catch (e: Exception) {
            if (delayException == null) {
                delayException = e
            } else {
                delayException.addSuppressed(e)
            }
        }
        project.logger.info("checkOverrideMethods完毕，耗时(ms):${System.currentTimeMillis() - start2}")

        if (delayException != null) {
            throw delayException
        }
    }

    /**
     * 检查转换后的类，其中被替换了的类有实现被调用的方法
     */
    private fun checkReplacedClassHaveRightMethods(debugClassPool: ClassPool, classNames: List<String>) {
        val result = ReplaceClassName.checkAll(debugClassPool, classNames)
        if (result.isNotEmpty()) {
            val tempFile = File.createTempFile("shadow_replace_class_have_right_methods", ".txt", project.buildDir)
            val bw = BufferedWriter(FileWriter(tempFile))

            result.forEach {
                val defClass = it.key
                bw.appendln("Class ${defClass}中缺少方法:")
                val methodMap = it.value
                methodMap.forEach {
                    val methodString = it.key
                    val useClass = it.value

                    bw.appendln("${methodString}被这些类调用了:")
                    useClass.forEach {
                        bw.appendln(it)
                    }
                }
                bw.newLine()
            }
            bw.flush()
            bw.close()
            throw IllegalStateException("存在转换后被调用方法未实现的问题，详见${tempFile.absolutePath}")
        }
    }

    private fun checkOverrideMethods(debugClassPool: ClassPool, classNames: List<String>) {
        val result = mOverrideCheck.check(debugClassPool, classNames)
        if (result.isNotEmpty()) {
            val tempFile = File.createTempFile("shadow_override_check", ".txt", project.buildDir)
            val bw = BufferedWriter(FileWriter(tempFile))
            result.forEach {
                bw.appendln("In Class ${it.key} 这些方法不再Override父类了:")
                it.value.map { "${it.first.name}:${it.first.signature}(转换前定义在${it.second})" }.forEach {
                    bw.appendln(it)
                }
                bw.newLine()
            }
            bw.flush()
            bw.close()
            throw IllegalStateException("存在Override方法转换后不再Override的情况，详见${tempFile.absolutePath}")
        }
    }

}
