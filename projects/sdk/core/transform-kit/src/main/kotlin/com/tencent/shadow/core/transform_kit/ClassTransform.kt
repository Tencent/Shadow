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
import com.android.build.api.transform.*
import com.android.build.api.transform.QualifiedContent.ContentType
import com.android.build.api.transform.QualifiedContent.Scope
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import com.google.common.collect.ImmutableList
import com.google.common.io.Files
import org.gradle.api.Project
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * 类转换基类
 *
 * @author cubershi
 */
abstract class ClassTransform(val project: Project) : Transform() {
    val inputSet: MutableSet<TransformInput> = mutableSetOf()

    /**
     * 获取输入文件对应的输出文件路径.即将文件this路径中的inputDir部分替换为outputDir.
     */
    fun File.toOutputFile(inputDir: File, outputDir: File): File {
        return File(outputDir, this.toRelativeString(inputDir))
    }

    fun input(
        inputs: Collection<com.android.build.api.transform.TransformInput>,
        outputProvider: TransformOutputProvider
    ) {
        val logger = project.logger
        if (logger.isInfoEnabled) {
            val sb = StringBuilder()
            sb.appendln()
            inputs.forEach {
                it.directoryInputs.forEach {
                    sb.appendln(it.file.absolutePath)
                }
                it.jarInputs.forEach {
                    sb.appendln(it.file.absolutePath)
                }
            }
            logger.info("ClassTransform input paths:$sb")
        }

        inputs.forEach {
            it.directoryInputs.forEach {
                val inputDir = it.file
                val transformInput = TransformInput(it)
                inputSet.add(transformInput)
                val allFiles = FileUtils.getAllFiles(it.file)
                allFiles.filter {
                    it?.name?.endsWith(SdkConstants.DOT_CLASS) ?: false
                }.forEach {
                    val inputClass = DirInputClass()
                    inputClass.onInputClass(
                        it,
                        it.toOutputFile(inputDir, transformInput.toOutput(outputProvider))
                    )
                    transformInput.addInputClass(inputClass)
                }
            }

            it.jarInputs.forEach {
                val transformInput = TransformInput(it)
                inputSet.add(transformInput)
                ZipInputStream(FileInputStream(it.file)).use { zis ->
                    var entry: ZipEntry?
                    while (true) {
                        entry = zis.nextEntry
                        if (entry == null) break

                        val name = entry.name

                        // 忽略一些实际上不会进入编译classpath的文件
                        if (entry.isDirectory) continue
                        if (!name.endsWith(SdkConstants.DOT_CLASS)) continue
                        if (name.startsWith("META-INF/", true)) continue
                        if (name.endsWith("module-info.class", true)) continue
                        if (name.endsWith("package-info.class", true)) continue

                        // 记录好entry和name的关系，添加再添加成transform的输入
                        val inputClass = JarInputClass()
                        inputClass.onInputClass(zis, name)
                        transformInput.addInputClass(inputClass)
                    }
                }
            }
        }
    }

    fun output(outputProvider: TransformOutputProvider) {
        inputSet.forEach { input ->
            when (input.format) {
                Format.DIRECTORY -> {
                    input.getInputClass().forEach {
                        val dirInputClass = it as DirInputClass
                        dirInputClass.getOutput().forEach {
                            val className = it.first
                            val file = it.second
                            Files.createParentDirs(file)
                            FileOutputStream(file).use {
                                onOutputClass(null, className, it)
                            }
                        }
                    }
                }
                Format.JAR -> {
                    val outputJar = input.toOutput(outputProvider)
                    Files.createParentDirs(outputJar)
                    ZipOutputStream(FileOutputStream(outputJar)).use { zos ->
                        input.getInputClass().forEach {
                            val jarInputClass = it as JarInputClass
                            jarInputClass.getOutput().forEach {
                                val className = it.first
                                val entryName = it.second
                                zos.putNextEntry(ZipEntry(entryName))
                                onOutputClass(entryName, className, zos)
                            }
                        }
                    }
                }
            }
        }
    }

    abstract fun onOutputClass(entryName: String?, className: String, outputStream: OutputStream)

    abstract fun DirInputClass.onInputClass(classFile: File, outputFile: File)

    abstract fun JarInputClass.onInputClass(zipInputStream: ZipInputStream, entryName: String)

    abstract fun onTransform()

    override fun getName(): String = this::class.simpleName!!

    override fun getInputTypes(): MutableSet<ContentType> = TransformManager.CONTENT_CLASS

    override fun isIncremental(): Boolean = false

    override fun getScopes(): MutableSet<in Scope> = TransformManager.SCOPE_FULL_PROJECT

    /**
     * 每一次执行transform前调用。在一次构建中可能有多个Variant，多个Variant会共用同一个
     * Transform对象（就是这个类的对象）。在这里提供一个时机清理transform过程中产生的缓存，
     * 避免对下一次transform产生影响。
     */
    open fun beforeTransform(invocation: TransformInvocation) {
        invocation.outputProvider.deleteAll()
        inputSet.clear()
    }

    open fun afterTransform(invocation: TransformInvocation) {
    }

    final override fun transform(invocation: TransformInvocation) {
        beforeTransform(invocation)
        input(invocation.inputs, invocation.outputProvider)
        onTransform()
        output(invocation.outputProvider)
        afterTransform(invocation)
    }

    override fun getSecondaryFiles(): ImmutableList<SecondaryFile>? {
        val transformJar = File(this::class.java.protectionDomain.codeSource.location.toURI())
        val transformKitJar =
            File(ClassTransform::class.java.protectionDomain.codeSource.location.toURI())

        return ImmutableList.of(
            //将当前类运行所在的jar本身作为转换输入的SecondaryFiles，也就作为了这个transform task的inputs的
            //一部分，这使得当这个Transform程序变化时，构建能检测到这个Transform需要重新执行。这是直接编辑这个
            //Transform源码后，应用了这个Plugin的debug工程能直接生效的关键。
            SecondaryFile.nonIncremental(project.files(transformJar)),
            SecondaryFile.nonIncremental(project.files(transformKitJar))
        )
    }
}
typealias ClassName_OutputFile = Pair<String, File>
typealias ClassName_EntryName = Pair<String, String>

abstract class InputClass() {
    abstract fun renameOutput(oldName: String, newName: String)
}

class DirInputClass() : InputClass() {
    private val outputs = mutableMapOf<String, File>()
    fun getOutput(): Set<ClassName_OutputFile> {
        val mutableSet = mutableSetOf<ClassName_OutputFile>()
        return outputs.mapTo(mutableSet) {
            ClassName_OutputFile(it.key, it.value)
        }
    }

    fun addOutput(className: String, file: File) {
        outputs[className] = file
    }

    fun getOutput(className: String): File {
        return outputs[className]!!
    }

    override fun renameOutput(oldName: String, newName: String) {
        val file = outputs.remove(oldName)!!
        val newFileName = file.name.replace(getSimpleName(oldName), getSimpleName(newName))
        outputs[newName] = File(file.parent, newFileName)
    }

    private fun getSimpleName(name: String): String {
        val i = name.lastIndexOf('.')
        if (i == -1) {
            return name
        } else {
            return name.substring(i + 1)
        }
    }
}

class JarInputClass() : InputClass() {
    private val outputs = mutableMapOf<String, String>()
    fun getOutput(): Set<ClassName_EntryName> {
        val mutableSet = mutableSetOf<ClassName_EntryName>()
        return outputs.mapTo(mutableSet) {
            ClassName_EntryName(it.key, it.value)
        }
    }

    fun addOutput(className: String, entryName: String) {
        outputs[className] = entryName
    }

    fun getOutput(className: String): String {
        return outputs[className]!!
    }

    override fun renameOutput(oldName: String, newName: String) {
        outputs[newName] = newName.replace('.', '/') + ".class"
    }

}


class TransformInput(
    val name: String,
    val contentTypes: Set<ContentType>,
    val scopes: MutableSet<in Scope>,
    val format: Format
) {
    constructor(di: DirectoryInput) : this(
        di.name, di.contentTypes, di.scopes, Format.DIRECTORY
    )

    constructor(ji: JarInput) : this(
        ji.name, ji.contentTypes, ji.scopes, Format.JAR
    )

    private val inputClassSet = mutableSetOf<InputClass>()

    fun addInputClass(inputClass: InputClass) {
        inputClassSet.add(inputClass)
    }

    fun getInputClass() = inputClassSet.toSet()

    fun toOutput(outputProvider: TransformOutputProvider) =
        outputProvider.getContentLocation(
            name,
            contentTypes,
            scopes,
            format
        )
}
