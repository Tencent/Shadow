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

import com.android.SdkConstants
import com.android.utils.FileUtils
import org.gradle.api.Project
import java.io.File
import java.io.FileInputStream
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

/**
 * 类转换基类
 *
 * @author cubershi
 */
abstract class ClassTransform(val project: Project) {

    /**
     * 获取输入文件对应的输出文件路径.即将文件this路径中的inputDir部分替换为outputDir.
     */
    private fun File.toOutputFile(inputDir: File, outputDir: File): File {
        return File(outputDir, this.toRelativeString(inputDir))
    }

    fun input(inputs: Iterable<TransformInput>) {
        val logger = project.logger
        if (logger.isInfoEnabled) {
            val sb = StringBuilder()
            sb.appendln()
            inputs.forEach {
                sb.appendln(it.asFile().absolutePath)
            }
            logger.info("ClassTransform input paths:$sb")
        }

        inputs.forEach { transformInput ->
            when (transformInput.kind) {
                TransformInput.Kind.DIRECTORY -> {
                    val inputDir = transformInput.asFile()
                    val allFiles = FileUtils.getAllFiles(inputDir)
                    allFiles.filter {
                        it?.name?.endsWith(SdkConstants.DOT_CLASS) ?: false
                    }.forEach {
                        val className = loadDotClassFile(it)
                        transformInput.inputClassNames.add(className)
                    }
                }

                TransformInput.Kind.JAR -> {
                    ZipInputStream(FileInputStream(transformInput.asFile())).use { zis ->
                        var entry: ZipEntry?
                        while (true) {
                            entry = zis.nextEntry
                            if (entry == null) break

                            val entryName = entry.name

                            // 忽略一些实际上不会进入编译classpath的文件
                            if (entry.isDirectory) continue
                            if (!entryName.endsWith(SdkConstants.DOT_CLASS)) continue
                            if (entryName.startsWith("META-INF/", true)) continue
                            if (entryName.endsWith("module-info.class", true)) continue
                            if (entryName.endsWith("package-info.class", true)) continue

                            val className = loadClassFromJar(zis)
                            transformInput.inputClassNames.add(className)
                        }
                    }
                }
            }
        }


    }

    abstract fun onOutputClass(className: String, outputStream: OutputStream)

    /**
     * 让子类实现的字节码编辑框架加载.class文件，加载后返回类名
     */
    abstract fun loadDotClassFile(classFile: File): String

    /**
     * 让子类实现的字节码编辑框架加载jar中的class，加载后返回类名
     */
    abstract fun loadClassFromJar(zipInputStream: ZipInputStream): String

    abstract fun onTransform()

    /**
     * 每一次执行transform前调用。在一次构建中可能有多个Variant，多个Variant会共用同一个
     * Transform对象（就是这个类的对象）。在这里提供一个时机清理transform过程中产生的缓存，
     * 避免对下一次transform产生影响。
     */
    open fun beforeTransform() {
    }

    open fun afterTransform() {
    }

}

/**
 * 输入数据的封装
 * 外部Transform框架的适配器DeprecatedTransformWrapper或者GradleTransformWrapper
 * 把它们的输入封装成这个抽象类的子类
 */
abstract class TransformInput {
    /**
     * 一个TransformInput可能是Dir包含多个class文件，也可能是一个jar包包含多个class文件。
     * 这里把它们记下来，等输出的时候要按这个名单输出
     */
    val inputClassNames = mutableSetOf<String>()

    enum class Kind { DIRECTORY, JAR }

    abstract val kind: Kind
    abstract fun asFile(): File
}
