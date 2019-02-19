package com.tencent.shadow.core.transform.common

import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
import javassist.NotFoundException

abstract class Transform {
    private val _list = mutableListOf<TransformStep>()

    val list: List<TransformStep> = _list

    lateinit var mClassPool: ClassPool

    fun newStep(transform: TransformStep) {
        _list.add(transform)
    }

    abstract fun setup()

    fun CtMethod.copyDescriptorFrom(other: CtMethod) {
        methodInfo.descriptor = other.methodInfo.descriptor
    }

    fun allCanRecompileAppClass(allAppClass: Set<CtClass>, targetClassList: List<String>) =
            allAppClass.filter { ctClass ->
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