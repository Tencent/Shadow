package com.tencent.shadow.core.transformkit

import com.android.build.api.transform.TransformInvocation
import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
import javassist.NotFoundException
import javassist.bytecode.CodeAttribute
import javassist.bytecode.MethodInfo
import javassist.bytecode.Opcode
import org.gradle.api.Project
import java.io.File
import java.io.OutputStream
import java.util.*
import java.util.zip.ZipInputStream

open class JavassistTransform(project: Project, val classPoolBuilder: ClassPoolBuilder) : ClassTransform(project) {
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

    /**
     * 查找目标class对象的目标method
     */
    fun getTargetMethods(targetClassNames: Array<String>, targetMethodName: Array<String>): List<CtMethod> {
        val method_targets = ArrayList<CtMethod>()
        for (targetClassName in targetClassNames) {
            val methods = classPool[targetClassName].methods
            method_targets.addAll(methods.filter { targetMethodName.contains(it.name) })
        }
        return method_targets
    }


    /**
     * 查找目标class是否存在目标method的调用
     */
    fun matchMethodCallInClass(ctMethod: CtMethod, clazz: CtClass): Boolean {
        for (methodInfo in clazz.classFile2.methods) {
            methodInfo as MethodInfo
            val codeAttr: CodeAttribute? = methodInfo.codeAttribute
            val constPool = methodInfo.constPool
            if (codeAttr != null) {
                val iterator = codeAttr.iterator()
                while (iterator.hasNext()) {
                    val pos = iterator.next()
                    val c = iterator.byteAt(pos)
                    if (c == Opcode.INVOKEINTERFACE || c == Opcode.INVOKESPECIAL
                            || c == Opcode.INVOKESTATIC || c == Opcode.INVOKEVIRTUAL) {
                        val index = iterator.u16bitAt(pos + 1)
                        val cname = constPool.eqMember(ctMethod.name, ctMethod.methodInfo2.descriptor, index)
                        val className = ctMethod.declaringClass.name
                        val matched = cname != null && matchClass(ctMethod.name, ctMethod.methodInfo.descriptor, className, cname, clazz.classPool)
                        if (matched) {
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

    private fun matchClass(methodName: String, methodDescriptor: String, classname: String, name: String, pool: ClassPool): Boolean {
        if (classname == name)
            return true

        try {
            val clazz = pool.get(name)
            val declClazz = pool.get(classname)
            if (clazz.subtypeOf(declClazz))
                try {
                    val m = clazz.getMethod(methodName, methodDescriptor)
                    return m.declaringClass.name == classname
                } catch (e: NotFoundException) {
                    // maybe the original method has been removed.
                    return true
                }

        } catch (e: NotFoundException) {
            return false
        }

        return false
    }
}

interface ClassPoolBuilder {
    fun build(): ClassPool
}