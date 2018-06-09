package com.tencent.cubershi

import com.android.SdkConstants
import com.android.build.api.transform.TransformInvocation
import com.android.utils.FileUtils
import com.tencent.cubershi.special.SpecialTransform
import javassist.ClassPool
import javassist.CtClass
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.function.BiConsumer
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class CuberPluginLoaderTransform(val classPool: ClassPool) : MyCustomClassTransform() {

    companion object {
        const val AndroidApplicationClassname = "android.app.Application"
        const val MockApplicationClassname = "com.tencent.cubershi.mock_interface.MockApplication"
        const val AndroidActivityClassname = "android.app.Activity"
        const val MockActivityClassname = "com.tencent.cubershi.mock_interface.MockActivity"
        const val AndroidServiceClassname = "android.app.Service"
        const val MockServiceClassname = "com.tencent.cubershi.mock_interface.MockService"
        const val AndroidFragmentClassname = "android.app.Fragment"
        const val MockFragmentClassname = "com.tencent.cubershi.mock_interface.MockFragment"
        const val AndroidFragmentManagerClassname = "android.app.FragmentManager"
        const val PluginFragmentManagerClassname = "com.tencent.cubershi.mock_interface.PluginFragmentManager"
        val SpecialTransformMap = mapOf<String, SpecialTransform>(
        )
        val FragmentCtClassCache = mutableSetOf<String>()
    }

    val ContainerFragmentCtClass = classPool["com.tencent.cubershi.mock_interface.ContainerFragment"]

    override fun loadTransformFunction(): BiConsumer<InputStream, OutputStream> =
            BiConsumer { input, output ->
                val ctClass: CtClass = classPool.makeClass(input, false)

                val ctClassOriginName = ctClass.name
                if (SpecialTransformMap.containsKey(ctClassOriginName)) {
                    SpecialTransformMap[ctClassOriginName]!!.transform(classPool, ctClass)
                    ctClass.writeOut(output)
                } else {
                    ctClass.replaceClassName(AndroidActivityClassname, MockActivityClassname)
                    ctClass.replaceClassName(AndroidApplicationClassname, MockApplicationClassname)
                    ctClass.replaceClassName(AndroidServiceClassname, MockServiceClassname)
                    ctClass.replaceClassName(AndroidFragmentClassname, MockFragmentClassname)
                    ctClass.replaceClassName(AndroidFragmentManagerClassname, PluginFragmentManagerClassname)
                    renameFragment(ctClass)
                    if (ctClass.isFragment()) {
                        val newContainerFragmentCtClass = classPool.makeClass(ctClassOriginName, ContainerFragmentCtClass)
                        newContainerFragmentCtClass.writeOut(output)
                        when (output) {
                            is FileOutputStream -> {
                                val newPath = currentFile.absolutePath.replace(currentFile.nameWithoutExtension, ctClass.simpleName)
                                FileOutputStream(newPath).use {
                                    ctClass.writeOut(it)
                                }
                            }
                            is ZipOutputStream -> {
                                val newEntryPath = ctClass.name.replace(".", "/") + ".class"
                                output.putNextEntry(ZipEntry(newEntryPath))
                                ctClass.writeOut(output)
                            }
                        }
                        return@BiConsumer
                    }
                    ctClass.writeOut(output)
                }
            }

    private fun CtClass.writeOut(output: OutputStream) {
        this.toBytecode(java.io.DataOutputStream(output))
    }

    override fun transform(invocation: TransformInvocation) {
        System.out.println("CuberPluginLoaderTransform开始")

        val ctClass = classPool.getOrNull("android.arch.lifecycle.ReportFragment_")
        System.err.println("ctClass==null:" + (ctClass == null))

        loadAppCtClass(invocation)

        super.transform(invocation)

        System.out.println("CuberPluginLoaderTransform结束")
    }

    private fun loadAppCtClass(invocation: TransformInvocation) {
        for (ti in invocation.inputs) {
            for (jarInput in ti.jarInputs) {
                val inputJar = jarInput.file
                loadJar(inputJar)
            }
            for (di in ti.directoryInputs) {
                val inputDir = di.file
                for (`in` in FileUtils.getAllFiles(inputDir)) {
                    if (`in`.name.endsWith(SdkConstants.DOT_CLASS)) {
                        loadClassFile(`in`)
                    }
                }
            }
        }
    }

    private fun loadJar(jarFile: File) {
        classPool.appendClassPath(jarFile.absolutePath)
    }

    private fun loadClassFile(classFile: File) {
        classPool.makeClass(classFile.inputStream(), false)
    }

    private fun renameFragment(ctClass: CtClass) {
        ctClass.refClasses.forEach {
            val refClassName: String = it as String
            if (refClassName == MockFragmentClassname) {
                return@forEach
            }
            if (refClassName in FragmentCtClassCache) {
                ctClass.replaceFragmentName(refClassName)
            } else if (classPool.getOrNull(refClassName) != null
                    && classPool.getOrNull(refClassName).isFragment()) {
                FragmentCtClassCache.add(refClassName)
                ctClass.replaceFragmentName(refClassName)
            }
        }
    }

    private fun CtClass.replaceFragmentName(fragmentClassName: String) {
        this.replaceClassName(fragmentClassName, fragmentClassName + "_")
    }

    private fun CtClass.isFragment(): Boolean {
        var tmp: CtClass? = this
        do {
            if (tmp?.name == MockFragmentClassname) {
                return true
            }
            tmp = tmp?.superclass
        } while (tmp != null)
        return false
    }

    override fun getName(): String = "CuberPluginLoaderTransform"
}