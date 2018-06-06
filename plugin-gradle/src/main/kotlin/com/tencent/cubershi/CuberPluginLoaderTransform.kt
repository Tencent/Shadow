package com.tencent.cubershi

import com.android.build.api.transform.TransformInvocation
import com.tencent.cubershi.special.SpecialTransform
import com.tencent.cubershi.special.android.arch.lifecycle.ReportFragmentTransform
import javassist.ClassPool
import javassist.CtClass
import java.io.DataOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.function.BiConsumer

class CuberPluginLoaderTransform() : CustomClassTransform() {

    companion object {
        const val AndroidApplicationClassname = "android.app.Application"
        const val MockApplicationClassname = "com.tencent.cubershi.mock_interface.MockApplication"
        const val AndroidActivityClassname = "android.app.Activity"
        const val MockActivityClassname = "com.tencent.cubershi.mock_interface.MockActivity"
        val SpecialTransformMap = mapOf<String, SpecialTransform>(
                "android.arch.lifecycle.ReportFragment" to ReportFragmentTransform()
        )
    }

    override fun loadTransformFunction(): BiConsumer<InputStream, OutputStream> =
            BiConsumer { input, output ->
                val classPool: ClassPool = ClassPool.getDefault()
                val ctClass: CtClass = classPool.makeClass(input, false)

                if (SpecialTransformMap.containsKey(ctClass.name)) {
                    SpecialTransformMap[ctClass.name]!!.transform(classPool, ctClass)
                } else {
                    ctClass.replaceClassName(AndroidActivityClassname, MockActivityClassname)
                    ctClass.replaceClassName(AndroidApplicationClassname, MockApplicationClassname)
                }
                ctClass.toBytecode(DataOutputStream(output))
            }

    override fun transform(invocation: TransformInvocation) {
        System.out.println("CuberPluginLoaderTransform开始")

        super.transform(invocation)

        System.out.println("CuberPluginLoaderTransform结束")
    }

    override fun getName(): String = "CuberPluginLoaderTransform"
}