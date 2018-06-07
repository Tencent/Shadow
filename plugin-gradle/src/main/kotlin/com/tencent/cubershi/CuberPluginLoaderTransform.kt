package com.tencent.cubershi

import com.android.SdkConstants
import com.android.build.api.transform.TransformInvocation
import com.android.utils.FileUtils
import com.tencent.cubershi.special.SpecialTransform
import com.tencent.cubershi.special.android.arch.lifecycle.ReportFragmentTransform
import javassist.*
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
        const val AndroidFragmentClassname = "android.app.Fragment"
        const val MockFragmentClassname = "com.tencent.cubershi.mock_interface.PluginFragment"
        const val AndroidServiceClassname = "android.app.Service"
        const val MockServiceClassname = "com.tencent.cubershi.mock_interface.MockService"
        const val PluginFragmentClassname = "com.tencent.cubershi.mock_interface.PluginFragment"
        val SpecialTransformMap = mapOf<String, SpecialTransform>(
                "android.arch.lifecycle.ReportFragment" to ReportFragmentTransform()
        )
        val AndroidFragmentCtClass: CtClass
        val PluginFragmentCtClass: CtClass
        val AndroidFragmentGetActivityCtMethod: CtMethod
        val PluginFragmentGetPluginActivityCtMethod: CtMethod

        init {
            val cp = ClassPool.getDefault()
            AndroidFragmentCtClass = cp[AndroidFragmentClassname]
            PluginFragmentCtClass = cp[PluginFragmentClassname]
            AndroidFragmentGetActivityCtMethod = AndroidFragmentCtClass.methods.find { it.name == "getActivity" }!!
            PluginFragmentGetPluginActivityCtMethod = PluginFragmentCtClass.methods.find { it.name == "getPluginActivity" }!!
        }
    }

    override fun loadTransformFunction(): BiConsumer<InputStream, OutputStream> =
            BiConsumer { input, output ->
                val ctClass: CtClass = classPool.makeClass(input, false)

                ctClass.replaceClassName(AndroidServiceClassname, MockServiceClassname)
                if (SpecialTransformMap.containsKey(ctClass.name)) {
                    SpecialTransformMap[ctClass.name]!!.transform(classPool, ctClass)
                } else {
                    ctClass.replaceClassName(AndroidActivityClassname, MockActivityClassname)
                    ctClass.replaceClassName(AndroidApplicationClassname, MockApplicationClassname)

                    if (ctClass.classFile.superclass == AndroidFragmentClassname) {
                        ctClass.classFile.superclass = MockFragmentClassname
                    }
                    val allRefClassesCanBeFound = ctClass.refClasses.all {
                        var found: Boolean;
                        try {
                            classPool[it as String]
                            found = true
                        } catch (e: NotFoundException) {
                            found = false
                        }
                        found
                    }
                    if (allRefClassesCanBeFound) {
                        redirectFragmentCallGetActivity(ctClass)
                    }
                }
                ctClass.toBytecode(DataOutputStream(output))
            }

    /**
     * 替换所有Fragment.getActivity()为PluginFragment.getPluginActivity()
     */
    fun redirectFragmentCallGetActivity(ctClass: CtClass) {
        val origMethod = AndroidFragmentGetActivityCtMethod
        val substMethod = PluginFragmentGetPluginActivityCtMethod
        origMethod.methodInfo.descriptor = substMethod.methodInfo.descriptor
        val codeConverter = CodeConverter()
        try {
            codeConverter.redirectMethodCall(origMethod, substMethod)
        } catch (e: CannotCompileException) {
            System.err.println("origMethod signature:" + origMethod.methodInfo.descriptor)
            System.err.println("substMethod signature:" + substMethod.methodInfo.descriptor)
            throw e
        }
        ctClass.instrument(codeConverter)
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