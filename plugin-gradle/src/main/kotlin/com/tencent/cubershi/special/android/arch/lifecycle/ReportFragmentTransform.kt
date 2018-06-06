package com.tencent.cubershi.special.android.arch.lifecycle

import com.tencent.cubershi.special.SpecialTransform
import javassist.ClassMap
import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod

class ReportFragmentTransform : SpecialTransform {
    companion object {
        const val AndroidActivityClassname = "android.app.Activity"
        const val MockActivityClassname = "com.tencent.cubershi.mock_interface.MockActivity"
    }

    override fun transform(cp: ClassPool, ctClass: CtClass) {
        assert(ctClass.name == "android.arch.lifecycle.ReportFragment")

        val method = ctClass.methods.find {
            it.name == "injectIfNeededIn"
        }

        cp.makeClass("android.app.Fragment")

        val classMap = ClassMap()
        classMap.put(AndroidActivityClassname, MockActivityClassname)
        ctClass.addMethod(CtMethod(method, ctClass, classMap))
        ctClass.removeMethod(method)
    }
}