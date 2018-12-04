package com.tencent.shadow.transform.transformkit

import com.android.build.api.transform.TransformInvocation
import javassist.ClassPool
import javassist.CtClass
import java.io.File
import java.io.OutputStream
import java.util.zip.ZipInputStream

open class JavassistTransform(val classPoolBuilder: ClassPoolBuilder) : ClassTransform() {
    val mCtClassInputMap = mutableMapOf<CtClass, InputClass>()
    lateinit var classPool: ClassPool

    override fun onOutputClass(className: String, outputStream: OutputStream) {
        classPool[className].writeOut(outputStream)
    }

    override fun DirInputClass.onInputClass(classFile: File, outputFile: File) {
        val ctClass: CtClass = classPool.makeClass(classFile.inputStream())
        addOutput(ctClass.name, outputFile)
        mCtClassInputMap[ctClass] = this
    }

    override fun JarInputClass.onInputClass(zipInputStream: ZipInputStream, entryName: String) {
        val ctClass = classPool.makeClass(zipInputStream)
        addOutput(ctClass.name, entryName)
        mCtClassInputMap[ctClass] = this
    }

    override fun beforeTransform(invocation: TransformInvocation) {
        super.beforeTransform(invocation)
        mCtClassInputMap.clear()
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