package com.tencent.cubershi.special

import javassist.ClassPool
import javassist.CtClass

interface SpecialTransform {
    fun transform(cp: ClassPool, ctClass: CtClass)
}