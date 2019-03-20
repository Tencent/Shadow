package com.tencent.shadow.core.transform.common

import javassist.CtClass

interface TransformStep {
    fun filter(allInputClass: Set<CtClass>): Set<CtClass>

    fun transform(ctClass: CtClass)
}