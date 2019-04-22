package com.tencent.shadow.core.transform_kit

import javassist.CtClass

interface TransformStep {
    fun filter(allInputClass: Set<CtClass>): Set<CtClass>

    fun transform(ctClass: CtClass)
}