package com.tencent.shadow.core.transform.specific

import com.tencent.shadow.core.transform_kit.SpecificTransform
import com.tencent.shadow.core.transform_kit.TransformStep
import javassist.CodeConverter
import javassist.CtClass
import javassist.bytecode.Descriptor

class LayoutInflaterTransform : SpecificTransform() {
    companion object {
        const val ShadowLayoutInflaterClassname =
            "com.tencent.shadow.core.runtime.ShadowLayoutInflater"
        const val AndroidLayoutInflaterClassname = "android.view.LayoutInflater"
        const val LayoutInflaterFactoryClassname = "android.view.LayoutInflater\$Factory"
        const val LayoutInflaterFactory2Classname = "android.view.LayoutInflater\$Factory2"
    }

    override fun setup(allInputClass: Set<CtClass>) {

        val androidLayoutInflaterClass = mClassPool[AndroidLayoutInflaterClassname]
        val shadowLayoutInflaterClass = mClassPool[ShadowLayoutInflaterClassname]
        val layoutInflaterFactoryClass = mClassPool[LayoutInflaterFactoryClassname]
        val layoutInflaterFactory2Class = mClassPool[LayoutInflaterFactory2Classname]

        val getFactoryMethod = androidLayoutInflaterClass.getMethod(
            "getFactory",
            Descriptor.ofMethod(layoutInflaterFactoryClass, null)
        )
        val getFactory2Method = androidLayoutInflaterClass.getMethod(
            "getFactory2",
            Descriptor.ofMethod(layoutInflaterFactory2Class, null)
        )
        val getOriginalFactoryMethod = shadowLayoutInflaterClass.getMethod(
            "getOriginalFactory",
            Descriptor.ofMethod(layoutInflaterFactoryClass, arrayOf(androidLayoutInflaterClass))
        )
        val getOriginalFactory2Method = shadowLayoutInflaterClass.getMethod(
            "getOriginalFactory2",
            Descriptor.ofMethod(layoutInflaterFactory2Class, arrayOf(androidLayoutInflaterClass))
        )

        val codeConverter = CodeConverter()
        codeConverter.redirectMethodCallToStatic(getFactoryMethod, getOriginalFactoryMethod)
        codeConverter.redirectMethodCallToStatic(getFactory2Method, getOriginalFactory2Method)

        newStep(object : TransformStep {
            override fun filter(allInputClass: Set<CtClass>) =
                filterRefClasses(allInputClass, listOf(AndroidLayoutInflaterClassname))

            override fun transform(ctClass: CtClass) {
                try {
                    ctClass.instrument(codeConverter)
                } catch (e: Exception) {
                    System.err.println("处理" + ctClass.name + "时出错")
                    throw e
                }
            }
        })
    }

}