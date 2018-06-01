package com.tencent.cubershi


import com.android.SdkConstants
import com.android.annotations.NonNull
import com.android.build.api.transform.*
import com.android.build.api.transform.QualifiedContent.DefaultContentType.CLASSES
import com.android.build.gradle.internal.pipeline.ExtendedContentType.NATIVE_LIBS
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import com.google.common.collect.ImmutableSet
import com.google.common.io.Files
import java.io.*
import java.util.function.BiConsumer
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * 抄自com.android.build.gradle.internal.transforms.CustomClassTransform
 * 去掉了原CustomClassTransform要求必须从一个jar中load出Service的要求.改为直接由子类提供
 * loadTransformFunction()实现
 */
abstract class CustomClassTransform : Transform() {


    @NonNull
    override fun getInputTypes(): Set<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_CLASS
    }

    @NonNull
    override fun getOutputTypes(): Set<QualifiedContent.ContentType> {
        return ImmutableSet.of(CLASSES, NATIVE_LIBS)
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    override fun isIncremental(): Boolean {
        return true
    }

    @Throws(InterruptedException::class, IOException::class)
    override fun transform(@NonNull invocation: TransformInvocation) {
        val outputProvider = invocation.outputProvider!!

        // Output the resources, we only do this if this is not incremental,
        // as the secondary file is will trigger a full build if modified.
        if (!invocation.isIncremental) {
            outputProvider.deleteAll()
        }

        val function = loadTransformFunction()

        for (ti in invocation.inputs) {
            for (jarInput in ti.jarInputs) {
                val inputJar = jarInput.file
                val outputJar = outputProvider.getContentLocation(
                        jarInput.name,
                        jarInput.contentTypes,
                        jarInput.scopes,
                        Format.JAR)

                if (invocation.isIncremental) {
                    val status: Status = jarInput.status
                    when (status) {
                        Status.NOTCHANGED -> {
                        }
                        Status.ADDED, Status.CHANGED -> transformJar(function, inputJar, outputJar)
                        Status.REMOVED -> FileUtils.delete(outputJar)
                    }
                } else {
                    transformJar(function, inputJar, outputJar)
                }
            }
            for (di in ti.directoryInputs) {
                val inputDir = di.file
                val outputDir = outputProvider.getContentLocation(
                        di.name,
                        di.contentTypes,
                        di.scopes,
                        Format.DIRECTORY)
                if (invocation.isIncremental) {
                    for ((inputFile, value: Status) in di.changedFiles) {
                        val value1: Status = value
                        when (value1) {
                            Status.NOTCHANGED -> {
                            }
                            Status.ADDED, Status.CHANGED -> if (!inputFile.isDirectory && inputFile.name
                                            .endsWith(SdkConstants.DOT_CLASS)) {
                                val out = toOutputFile(outputDir, inputDir, inputFile)
                                transformFile(function, inputFile, out)
                            }
                            Status.REMOVED -> {
                                val outputFile = toOutputFile(outputDir, inputDir, inputFile)
                                FileUtils.deleteIfExists(outputFile)
                            }
                        }
                    }
                } else {
                    for (`in` in FileUtils.getAllFiles(inputDir)) {
                        if (`in`.name.endsWith(SdkConstants.DOT_CLASS)) {
                            val out = toOutputFile(outputDir, inputDir, `in`)
                            transformFile(function, `in`, out)
                        }
                    }
                }
            }
        }

    }

    protected abstract fun loadTransformFunction(): BiConsumer<InputStream, OutputStream>

    @Throws(IOException::class)
    private fun transformJar(
            function: BiConsumer<InputStream, OutputStream>, inputJar: File, outputJar: File) {
        Files.createParentDirs(outputJar)
        FileInputStream(inputJar).use { fis ->
            ZipInputStream(fis).use { zis ->
                FileOutputStream(outputJar).use { fos ->
                    ZipOutputStream(fos).use { zos ->
                        var entry: ZipEntry? = zis.nextEntry
                        while (entry != null) {
                            if (!entry.isDirectory && entry.name.endsWith(SdkConstants.DOT_CLASS)) {
                                zos.putNextEntry(ZipEntry(entry.name))
                                apply(function, zis, zos)
                            } else {
                                // Do not copy resources
                            }
                            entry = zis.nextEntry
                        }
                    }
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun transformFile(
            function: BiConsumer<InputStream, OutputStream>, inputFile: File, outputFile: File) {
        Files.createParentDirs(outputFile)
        FileInputStream(inputFile).use { fis -> FileOutputStream(outputFile).use { fos -> apply(function, fis, fos) } }
    }

    @Throws(IOException::class)
    private fun apply(
            function: BiConsumer<InputStream, OutputStream>, `in`: InputStream, out: OutputStream) {
        try {
            function.accept(`in`, out)
        } catch (e: UncheckedIOException) {
            throw e.cause!!
        }

    }

    companion object {

        @NonNull
        private fun toOutputFile(outputDir: File, inputDir: File, inputFile: File): File {
            return File(outputDir, FileUtils.relativePossiblyNonExistingPath(inputFile, inputDir))
        }
    }
}