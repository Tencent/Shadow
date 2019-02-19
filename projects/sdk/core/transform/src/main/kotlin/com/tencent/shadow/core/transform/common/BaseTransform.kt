package com.tencent.shadow.core.transform.common

import com.tencent.shadow.core.transformkit.InputClass
import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod

abstract class BaseTransform(mCtClassInputMap: Map<CtClass, InputClass>,
                             val mClassPool: ClassPool) {

    val mAllAppClasses = mCtClassInputMap.keys

    abstract fun transform(ctClass: CtClass)

    abstract fun filter(): Set<CtClass>

    fun perform() {
        for (ctClass in filter()) {
            transform(ctClass)
        }
    }

    fun CtMethod.copyDescriptorFrom(other: CtMethod) {
        methodInfo.descriptor = other.methodInfo.descriptor
    }
}