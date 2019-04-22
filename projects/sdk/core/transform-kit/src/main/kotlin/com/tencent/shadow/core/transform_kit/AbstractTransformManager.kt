package com.tencent.shadow.core.transform_kit

import javassist.ClassPool
import javassist.CtClass

abstract class AbstractTransformManager(ctClassInputMap: Map<CtClass, InputClass>,
                                        private val classPool: ClassPool
) {
    private val allInputClass = ctClassInputMap.keys

    abstract val mTransformList: List<SpecificTransform>

    fun setupAll() {
        mTransformList.forEach {
            it.mClassPool = classPool
            it.setup(allInputClass)
        }
    }

    fun fireAll() {
        mTransformList.flatMap { it.list }.forEach { transform ->
            transform.filter(allInputClass).forEach {
                transform.transform(it)
            }
        }
    }
}