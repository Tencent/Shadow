package com.tencent.shadow.core.transform.specific

import com.tencent.shadow.core.transform_kit.SpecificTransform
import com.tencent.shadow.core.transform_kit.TransformStep
import javassist.CodeConverter
import javassist.CtClass

class PendingIntentTransform : SpecificTransform() {

    companion object {
        const val AndroidPendingIntentClassname = "android.app.PendingIntent"
        const val ShadowPendingIntentClassname = "com.tencent.shadow.core.runtime.ShadowPendingIntent"
    }

    val codeConverter = CodeConverter()

    override fun setup(allInputClass: Set<CtClass>) {
        val pendingIntentMethod = mClassPool[AndroidPendingIntentClassname].methods!!
        val shadowPendingIntentMethod = mClassPool[ShadowPendingIntentClassname].methods!!

        val method_getPengdingIntent = pendingIntentMethod.filter { it.name == "getService" || it.name == "getActivity" }
        val shadow_method_getPengdingIntent = shadowPendingIntentMethod.filter { it.name == "getService" || it.name == "getActivity" }!!

        for (ctAndroidMethod in method_getPengdingIntent) {
            for (ctShadowMedthod in shadow_method_getPengdingIntent) {
                if (ctShadowMedthod.methodInfo.name == ctAndroidMethod.methodInfo.name && ctAndroidMethod.methodInfo.descriptor == ctShadowMedthod.methodInfo.descriptor) {
                    codeConverter.redirectMethodCall(ctAndroidMethod, ctShadowMedthod)
                }
            }
        }

        newStep(object : TransformStep {
            override fun filter(allInputClass: Set<CtClass>) =
                    allCanRecompileAppClass(allInputClass, listOf(AndroidPendingIntentClassname))

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