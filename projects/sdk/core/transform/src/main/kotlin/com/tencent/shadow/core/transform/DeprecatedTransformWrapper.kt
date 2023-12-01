@file:Suppress("DEPRECATION")

package com.tencent.shadow.core.transform

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.SecondaryFile
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.api.transform.TransformOutputProvider
import com.android.build.api.variant.VariantInfo
import com.android.build.gradle.internal.pipeline.TransformManager
import com.google.common.collect.ImmutableList
import com.google.common.io.Files
import com.tencent.shadow.core.transform_kit.ClassTransform
import com.tencent.shadow.core.transform_kit.TransformInput
import org.gradle.api.Project
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * 适配ClassTransform到AGP 8之前的Transform API
 * 这是最初开发的、长期使用的比较稳定的实现。因为AGP 8去掉了这个API，所以不得不做两种适配。
 */
class DeprecatedTransformWrapper(
    val project: Project, val classTransform: ClassTransform
) : Transform() {
    override fun getName(): String = "ShadowTransform"

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> =
        TransformManager.CONTENT_CLASS

    override fun isIncremental(): Boolean = false

    override fun isCacheable(): Boolean {
        return true
    }

    override fun applyToVariant(variant: VariantInfo): Boolean {
        return if (variant.isTest) false
        else variant.flavorNames.contains(ShadowTransform.ApplyShadowTransformFlavorName)
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> =
        TransformManager.SCOPE_FULL_PROJECT

    override fun transform(invocation: TransformInvocation) {
        //before Transform clean output
        val outputProvider = invocation.outputProvider
        outputProvider.deleteAll()

        val inputs: List<TransformInput> = invocation.inputs.flatMap { transformInput ->
            transformInput.directoryInputs.map {
                TransformInputImpl(it)
            } + transformInput.jarInputs.map {
                TransformInputImpl(it)
            }
        }

        classTransform.beforeTransform()
        classTransform.input(inputs)
        classTransform.onTransform()
        output(inputs, outputProvider)
        classTransform.afterTransform()
    }

    /**
     * 这个旧版Transform API需要把DIR里的class和jar里的class分别输出到DIR和对应的jar里，
     * 这个行为是API决定的，不是通用的，因此要写在这里。对于ClassTransform来说，唯一可以复用的
     * 就是把class输出到OutputStream中。
     */
    private fun output(inputs: Iterable<TransformInput>, outputProvider: TransformOutputProvider) {
        inputs.forEach { transformInput ->
            transformInput as TransformInputImpl

            val outputLocation = outputProvider.getContentLocation(
                transformInput.name,
                transformInput.contentTypes,
                transformInput.scopes,
                transformInput.format
            )

            when (transformInput.kind) {
                TransformInput.Kind.DIRECTORY -> {
                    transformInput.inputClassNames.forEach { className ->
                        val relativePath = className.replace('.', File.separatorChar) + ".class"
                        val outputClassFile = File(outputLocation, relativePath)
                        Files.createParentDirs(outputClassFile)
                        FileOutputStream(outputClassFile).use {
                            classTransform.onOutputClass(className, it)
                        }
                    }
                }

                TransformInput.Kind.JAR -> {
                    Files.createParentDirs(outputLocation)
                    ZipOutputStream(FileOutputStream(outputLocation)).use { zos ->
                        transformInput.inputClassNames.forEach { className ->
                            val entryName = className.replace('.', '/') + ".class"
                            zos.putNextEntry(ZipEntry(entryName))
                            classTransform.onOutputClass(className, zos)
                        }
                    }
                }
            }
        }
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

    private class TransformInputImpl(
        val file: File,
        val name: String,
        val contentTypes: Set<QualifiedContent.ContentType>,
        val scopes: MutableSet<in QualifiedContent.Scope>,
        val format: Format,
        override val kind: Kind,
    ) : TransformInput() {
        constructor(di: DirectoryInput) : this(
            di.file,
            di.name,
            di.contentTypes,
            di.scopes,
            Format.DIRECTORY,
            Kind.DIRECTORY
        )

        constructor(ji: JarInput) : this(
            ji.file,
            ji.name,
            ji.contentTypes,
            ji.scopes,
            Format.JAR,
            Kind.JAR
        )

        override fun asFile(): File = file

    }
}