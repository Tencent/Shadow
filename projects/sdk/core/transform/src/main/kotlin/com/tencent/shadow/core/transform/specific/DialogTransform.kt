package com.tencent.shadow.core.transform.specific

import com.tencent.shadow.core.transform_kit.ReplaceClassName
import com.tencent.shadow.core.transform_kit.SpecificTransform
import com.tencent.shadow.core.transform_kit.TransformStep
import javassist.CodeConverter
import javassist.CtClass
import javassist.CtMethod

class DialogTransform : SpecificTransform() {
    companion object {
        const val AndroidDialogClassname = "android.app.Dialog"
        const val ShadowDialogClassname = "com.tencent.shadow.core.runtime.ShadowDialog"
    }

    private lateinit var codeConverter: CodeConverter

    override fun setup(allInputClass: Set<CtClass>) {
        val dialogMethods = mClassPool[AndroidDialogClassname].methods!!
        val shadowDialogMethods = mClassPool[ShadowDialogClassname].methods!!
        codeConverter = CodeConverter()

        redirectMethodCall(
            dialogMethods.find { it.name == "getOwnerActivity" }!!,
            shadowDialogMethods.find { it.name == "getOwnerPluginActivity" }!!
        )

        redirectMethodCall(
            dialogMethods.find { it.name == "setOwnerActivity" }!!,
            shadowDialogMethods.find { it.name == "setOwnerPluginActivity" }!!
        )

        newStep(object : TransformStep {
            override fun filter(allInputClass: Set<CtClass>) = allInputClass

            override fun transform(ctClass: CtClass) {
                ReplaceClassName.replaceClassName(ctClass, AndroidDialogClassname, ShadowDialogClassname)
            }

        })

        newStep(object : TransformStep {
            override fun filter(allInputClass: Set<CtClass>): Set<CtClass> =
                allCanRecompileAppClass(allInputClass, listOf(AndroidDialogClassname))

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

    private fun redirectMethodCall(
        oldMethod: CtMethod,
        newMethod: CtMethod
    ) {
        oldMethod.copyDescriptorFrom(newMethod)
        //appClass中的Activity都已经被改名为ShadowActivity了．所以要把方法签名也先改一下．
        codeConverter.redirectMethodCall(oldMethod, newMethod)
    }

}
