package com.tencent.shadow.core.loader.classloaders

import android.os.Build

class CombineClassLoader(private val classLoaders: Array<out ClassLoader>, parent: ClassLoader) : ClassLoader(parent) {

    @Throws(ClassNotFoundException::class)
    override fun loadClass(name: String, resolve: Boolean): Class<*> {
        var c: Class<*>? = findLoadedClass(name)
        val classNotFoundException = ClassNotFoundException(name)
        if (c == null) {
            try {
                c = super.loadClass(name, resolve)
            } catch (e: ClassNotFoundException) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    classNotFoundException.addSuppressed(e)
                }
            }

            if (c == null) {
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
            }
        }
        return c
    }
}