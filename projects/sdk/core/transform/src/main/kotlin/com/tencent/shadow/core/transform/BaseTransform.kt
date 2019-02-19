package com.tencent.shadow.core.transform

import com.tencent.shadow.core.transformkit.InputClass
import javassist.CtClass

abstract class BaseTransform(mCtClassInputMap: Map<CtClass, InputClass>) {

    val mAllAppClasses = mCtClassInputMap.keys

    abstract fun transform(ctClass: CtClass)

    abstract fun filter(): Set<CtClass>

    fun perform() {
        for (ctClass in filter()) {
            transform(ctClass)
        }
    }

}