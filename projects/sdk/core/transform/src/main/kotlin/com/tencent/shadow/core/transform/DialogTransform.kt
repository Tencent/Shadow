package com.tencent.shadow.core.transform

import com.tencent.shadow.core.transform.common.EachCanRecompileAppClassTransform
import com.tencent.shadow.core.transformkit.InputClass
import javassist.ClassPool
import javassist.CodeConverter
import javassist.CtClass

class DialogTransform(mCtClassInputMap: Map<CtClass, InputClass>,
                      mClassPool: ClassPool)
    : EachCanRecompileAppClassTransform(
        listOf(AndroidDialogClassname),
        mCtClassInputMap,
        mClassPool
) {
    companion object {
        const val AndroidDialogClassname = "android.app.Dialog"
        const val ShadowDialogClassname = "com.tencent.shadow.runtime.ShadowDialog"
    }

    private val codeConverter: CodeConverter

    init {
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
    }

    override fun transform(ctClass: CtClass) {
        try {
            ctClass.replaceClassName(AndroidDialogClassname, ShadowDialogClassname)
            ctClass.instrument(codeConverter)
        } catch (e: Exception) {
            System.err.println("处理" + ctClass.name + "时出错")
            throw e
        }
    }
}