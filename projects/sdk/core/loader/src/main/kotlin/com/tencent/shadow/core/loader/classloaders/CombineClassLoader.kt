package com.tencent.shadow.core.loader.classloaders

import android.os.Build

class CombineClassLoader(private val classLoaders: Array<out ClassLoader>, parent: ClassLoader) : ClassLoader(parent) {

    @Throws(ClassNotFoundException::class)
    override fun loadClass(name: String, resolve: Boolean): Class<*> {
        var c: Class<*>? = null
        val classNotFoundException = ClassNotFoundException(name)
        for (classLoader in classLoaders) {
            try {
                c = classLoader.loadClass(name)!!
                break
            } catch (e: ClassNotFoundException) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    classNotFoundException.addSuppressed(e)
                }
            }
        }
        if (c == null) {
            throw classNotFoundException
        }
        return c
    }
}