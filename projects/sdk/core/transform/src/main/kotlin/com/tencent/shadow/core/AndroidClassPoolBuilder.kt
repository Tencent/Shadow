package com.tencent.shadow.core

import com.tencent.shadow.core.transformkit.ClassPoolBuilder
import javassist.ClassPool
import javassist.LoaderClassPath
import java.io.File

class AndroidClassPoolBuilder(
        val contextClassLoader: ClassLoader,
        val androidJar: File
) : ClassPoolBuilder {
    override fun build(): ClassPool {
        //这里使用useDefaultPath:false是因为这里取到的contextClassLoader不包含classpath指定进来的runtime
        //所以在外部先获取一个包含了runtime的contextClassLoader传进来
        val classPool = ClassPool(false)
        classPool.appendClassPath(LoaderClassPath(contextClassLoader))
        classPool.appendClassPath(androidJar.absolutePath)
        return classPool
    }
}