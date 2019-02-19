package com.tencent.shadow.core.transform

import com.tencent.shadow.core.transform.common.Transform
import com.tencent.shadow.core.transformkit.InputClass
import javassist.ClassPool
import javassist.CtClass

class TransformManager(val mCtClassInputMap: Map<CtClass, InputClass>,
                       val classPool: ClassPool,
                       useHostContext: () -> Array<String>
) {
    private val allInputClass = mCtClassInputMap.keys

    private val mTransformList: List<Transform> = listOf(
            RemoteViewTransform(),
            FragmentTransform(mCtClassInputMap),
            DialogTransform(),
            WebViewTransform(),
            ContentProviderTransform(),
            KeepHostContextTransform(useHostContext())
    )

    init {
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