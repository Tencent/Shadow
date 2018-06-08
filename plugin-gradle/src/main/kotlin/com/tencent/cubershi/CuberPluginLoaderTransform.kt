package com.tencent.cubershi

import com.android.SdkConstants
import com.android.build.api.transform.TransformInvocation
import com.android.utils.FileUtils
import com.tencent.cubershi.special.SpecialTransform
import javassist.ClassPool
import javassist.CtClass
import java.io.DataOutputStream
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.util.function.BiConsumer

class CuberPluginLoaderTransform() : CustomClassTransform() {

    companion object {
        val classPool: ClassPool = ClassPool.getDefault()
        const val AndroidApplicationClassname = "android.app.Application"
        const val MockApplicationClassname = "com.tencent.cubershi.mock_interface.MockApplication"
        const val AndroidActivityClassname = "android.app.Activity"
        const val MockActivityClassname = "com.tencent.cubershi.mock_interface.MockActivity"
        const val AndroidServiceClassname = "android.app.Service"
        const val MockServiceClassname = "com.tencent.cubershi.mock_interface.MockService"
        val SpecialTransformMap = mapOf<String, SpecialTransform>(
        )
    }

    override fun loadTransformFunction(): BiConsumer<InputStream, OutputStream> =
            BiConsumer { input, output ->
                val ctClass: CtClass = classPool.makeClass(input, false)

                if (SpecialTransformMap.containsKey(ctClass.name)) {
                    SpecialTransformMap[ctClass.name]!!.transform(classPool, ctClass)
                } else {
                    ctClass.replaceClassName(AndroidActivityClassname, MockActivityClassname)
                    ctClass.replaceClassName(AndroidApplicationClassname, MockApplicationClassname)
                    ctClass.replaceClassName(AndroidServiceClassname, MockServiceClassname)
                }
                ctClass.toBytecode(DataOutputStream(output))
            }

    override fun transform(invocation: TransformInvocation) {
        System.out.println("CuberPluginLoaderTransform开始")

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

    override fun getName(): String = "CuberPluginLoaderTransform"
}