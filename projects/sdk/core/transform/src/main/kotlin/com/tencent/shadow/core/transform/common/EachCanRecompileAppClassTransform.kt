package com.tencent.shadow.core.transform.common

import com.tencent.shadow.core.transformkit.InputClass
import javassist.ClassPool
import javassist.CtClass
import javassist.NotFoundException

/**
 * 对所有可以重新编译的App类进行Transform的基类
 * @param targetClassList 需要编辑的目标类列表
 */
abstract class EachCanRecompileAppClassTransform(private val targetClassList: List<String>,
                                                 mCtClassInputMap: Map<CtClass, InputClass>,
                                                 mClassPool: ClassPool)
    : BaseTransform(mCtClassInputMap, mClassPool) {
    override fun filter(): Set<CtClass> {
        return mAllAppClasses.filter { ctClass ->
            targetClassList.any { targetClass ->
                ctClass.refClasses.contains(targetClass)
            }
        }.filter {
            it.refClasses.all {
                var found: Boolean;
                try {
                    mClassPool[it as String]
                    found = true
                } catch (e: NotFoundException) {
                    found = false
                }
                found
            }
        }.toSet()
    }
}