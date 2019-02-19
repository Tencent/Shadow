package com.tencent.shadow.core.transform

import com.tencent.shadow.core.transform.common.Transform
import com.tencent.shadow.core.transformkit.InputClass
import javassist.ClassPool
import javassist.CtClass

class TransformManager(val mCtClassInputMap: Map<CtClass, InputClass>,
                       val classPool: ClassPool) {
    private val mTransformList: List<Transform> = listOf(
            RemoteViewTransform(),
            DialogTransform()
    )

    init {
        mTransformList.forEach {
            it.mClassPool = classPool
            it.setup()
        }
    }

    fun fireAll() {
        val allInputClass = mCtClassInputMap.keys
        mTransformList.flatMap { it.list }.forEach { transform ->
            transform.filter(allInputClass).forEach {
                transform.transform(it)
            }
        }
    }
}