package com.tencent.shadow.core.transform

import com.tencent.shadow.core.transform_kit.ClassTransform
import com.tencent.shadow.core.transform_kit.TransformInput
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry
import javax.inject.Inject

/**
 * 适配AGP 7.2引入的新的Transform API，AGP没给这个API特别命名，但我们知道它是Gradle直接提供的Transform
 * 接口。与之对应的是DeprecatedTransformWrapper适配旧的Transform API接口。
 */
abstract class GradleTransformWrapper @Inject constructor(@Internal val classTransform: ClassTransform) :
    DefaultTask() {
    // This property will be set to all Jar files available in scope
    @get:InputFiles
    abstract val allJars: ListProperty<RegularFile>

    // Gradle will set this property with all class directories that available in scope
    @get:InputFiles
    abstract val allDirectories: ListProperty<Directory>

    // Task will put all classes from directories and jars after optional modification into single jar
    @get:OutputFile
    abstract val output: RegularFileProperty

    @TaskAction
    fun taskAction() {
        val inputs: List<TransformInput> = allDirectories.get().map {
            TransformInputImpl(it.asFile, TransformInput.Kind.DIRECTORY)
        } + allJars.get().map {
            TransformInputImpl(it.asFile, TransformInput.Kind.JAR)
        }

        classTransform.beforeTransform()
        classTransform.input(inputs)
        classTransform.onTransform()
        output(inputs)
        classTransform.afterTransform()
    }

    private fun output(inputs: Iterable<TransformInput>) {
        val jarOutput = JarOutputStream(
            BufferedOutputStream(FileOutputStream(output.get().asFile))
        )
        jarOutput.use {
            val outputClassNames = inputs.flatMap {
                it.inputClassNames
            }

            outputClassNames.forEach { className ->
                val entryName = className.replace('.', '/') + ".class"
                jarOutput.putNextEntry(ZipEntry(entryName))
                classTransform.onOutputClass(className, jarOutput)
            }
        }
    }

    private class TransformInputImpl(
        val file: File,
        override val kind: Kind
    ) : TransformInput() {

        override fun asFile(): File = file

    }
}

