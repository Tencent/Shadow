package com.tencent.cubershi

import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.util.function.BiConsumer

abstract class MyCustomClassTransform : CustomClassTransform() {
    lateinit var currentFile: File

    override fun transformFile(function: BiConsumer<InputStream, OutputStream>, inputFile: File, outputFile: File) {
        currentFile = outputFile
        super.transformFile(function, inputFile, outputFile)
    }
}