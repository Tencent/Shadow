package com.tencent.cubershi

import com.android.build.api.transform.TransformInvocation
import javassist.ClassPool
import javassist.CtClass
import java.io.DataOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.function.BiConsumer

class CuberPluginLoaderTransform : CustomClassTransform() {
    override fun loadTransformFunction(): BiConsumer<InputStream, OutputStream> =
            BiConsumer { input, output ->
                val classPool: ClassPool = ClassPool.getDefault()
                val mockActivityClass: CtClass = classPool.makeClass("com.tencent.cubershi.mock_interface.MockActivity")
                val ctClass: CtClass = classPool.makeClass(input, false)
//                if (ctClass.classFile.name == "android.support.v4.app.SupportActivity") {
//                    ctClass.superclass = mockActivityClass
//                }
//                ctClass.toBytecode(DataOutputStream(output))

                val superclass = ctClass.classFile.superclass
                if (superclass != null
                        && superclass == "android.app.Activity") {
                    System.out.println("找到一个Activity:" + ctClass.name)
                    ctClass.superclass = mockActivityClass
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