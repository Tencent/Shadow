package com.tencent.cubershi

import com.android.build.api.transform.TransformInvocation
import javassist.CannotCompileException
import javassist.ClassPool
import javassist.CodeConverter
import javassist.CtClass
import javassist.bytecode.BadBytecode
import javassist.bytecode.ConstPool
import javassist.bytecode.MethodInfo
import javassist.convert.Transformer
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
                val superclass = ctClass.classFile.superclass
                if (superclass != null
                        && superclass == "android.app.Activity") {
                    System.out.println("找到一个" + superclass + ":" + ctClass.name + ",修改它的父类为" + mockActivityClass.name)
                    ctClass.superclass = mockActivityClass
                }

                val codeConverter = MyCodeConverter()

                codeConverter.replaceCallOnClass("android.app.Activity", "com.tencent.cubershi.mock_interface.MockActivity")
                try {
                    ctClass.instrument(codeConverter)
                } catch (ignored: Exception) {
                    ignored.printStackTrace()
                }
                ctClass.toBytecode(DataOutputStream(output))
            }

    override fun transform(invocation: TransformInvocation) {
        System.out.println("CuberPluginLoaderTransform开始")

        super.transform(invocation)

        System.out.println("CuberPluginLoaderTransform结束")
    }

    override fun getName(): String = "CuberPluginLoaderTransform"


    class MyCodeConverter : CodeConverter() {
        fun replaceCallOnClass(className: String, newClassName: String) {
            transformers = TransformAllClassCall(transformers, className, newClassName)
        }

        /**
         * 去掉了rebuildStackMap步骤
         */
        override fun doit(clazz: CtClass, minfo: MethodInfo, cp: ConstPool) {
            var t: Transformer?
            val codeAttr = minfo.getCodeAttribute()
            if (codeAttr == null || transformers == null)
                return
            t = transformers
            while (t != null) {
                t.initialize(cp, clazz, minfo)
                t = t.next
            }

            val iterator = codeAttr.iterator()
            while (iterator.hasNext()) {
                try {
                    var pos = iterator.next()
                    t = transformers
                    while (t != null) {
                        pos = t.transform(clazz, pos, iterator, cp)
                        t = t.next
                    }
                } catch (e: BadBytecode) {
                    throw CannotCompileException(e)
                }

            }

            t = transformers
            while (t != null) {
                t.clean()
                t = t.next
            }
        }
    }
}