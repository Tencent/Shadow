package com.tencent.shadow.core.transform

import com.tencent.shadow.core.transform.common.Transform
import com.tencent.shadow.core.transform.common.TransformStep
import javassist.CodeConverter
import javassist.CtClass

class DialogTransform : Transform() {
    companion object {
        const val AndroidDialogClassname = "android.app.Dialog"
        const val ShadowDialogClassname = "com.tencent.shadow.runtime.ShadowDialog"
    }

    private lateinit var codeConverter: CodeConverter

    override fun setup(allInputClass: Set<CtClass>) {
        val dialogMethods = mClassPool[AndroidDialogClassname].methods!!
        val shadowDialogMethods = mClassPool[ShadowDialogClassname].methods!!
        val method_getOwnerActivity = dialogMethods.find { it.name == "getOwnerActivity" }!!
        val method_setOwnerActivity = dialogMethods.find { it.name == "setOwnerActivity" }!!
        val method_getOwnerPluginActivity = shadowDialogMethods.find { it.name == "getOwnerPluginActivity" }!!
        val method_setOwnerPluginActivity = shadowDialogMethods.find { it.name == "setOwnerPluginActivity" }!!
        //appClass中的Activity都已经被改名为ShadowActivity了．所以要把方法签名也先改一下．
        method_getOwnerActivity.copyDescriptorFrom(method_getOwnerPluginActivity)
        method_setOwnerActivity.copyDescriptorFrom(method_setOwnerPluginActivity)

        codeConverter = CodeConverter()
        codeConverter.redirectMethodCall(method_getOwnerActivity, method_getOwnerPluginActivity)
        codeConverter.redirectMethodCall(method_setOwnerActivity, method_setOwnerPluginActivity)

        newStep(object : TransformStep {
            override fun filter(allInputClass: Set<CtClass>) = allInputClass

            override fun transform(ctClass: CtClass) {
                ctClass.replaceClassName(AndroidDialogClassname, ShadowDialogClassname)
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

}