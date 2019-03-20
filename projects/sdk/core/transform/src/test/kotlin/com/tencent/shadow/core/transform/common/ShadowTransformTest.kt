package com.tencent.shadow.core.transform.common

import javassist.ClassPool
import javassist.Loader


abstract class ShadowTransformTest {
    companion object {
        const val WRITE_FILE_DIR = "build/test_write_file"
    }

    protected val sLoader: ClassPool = ClassPool.getDefault()
    protected val dLoader: ClassPool = ClassPool(null)
    protected val cLoader: Loader

    init {
        dLoader.appendSystemPath()
        dLoader.insertClassPath(WRITE_FILE_DIR)
        cLoader = Loader(dLoader)
    }

    protected fun make(name: String): Any {
        return cLoader.loadClass(name).getConstructor().newInstance()
    }

    protected operator fun invoke(target: Any, method: String): Int {
        val m = target.javaClass.getMethod(method, *arrayOfNulls(0))
        val res = m.invoke(target, *arrayOfNulls(0))
        return (res as Int).toInt()
    }

}