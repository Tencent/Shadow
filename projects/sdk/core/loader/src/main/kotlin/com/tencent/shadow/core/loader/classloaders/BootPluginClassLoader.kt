package com.tencent.shadow.core.loader.classloaders

import android.os.Build


/**
 * 用于加载插件的ClassLoader的父classLoader
 *
 * @author owenguo
 */
class BootPluginClassLoader(parent: ClassLoader) : ClassLoader(parent) {

    private val mGrandParent: ClassLoader = parent.parent

    @Throws(ClassNotFoundException::class)
    override fun loadClass(className: String, resolve: Boolean): Class<*> {
        if (className.startsWith("com.tencent.shadow.runtime")
                || className.startsWith("org.apache.commons.logging")//org.apache.commons.logging是非常特殊的的包,由系统放到App的PathClassLoader中.
          || (Build.VERSION.SDK_INT < 28 && className.startsWith("org.apache.http"))) {//Android 9.0以下的系统里面带有http包，走系统的不走本地的
            return super.loadClass(className, resolve)
        } else {
            var clazz: Class<*>? = findLoadedClass(className)

            if (clazz == null) {
                var suppressed: ClassNotFoundException? = null
                try {
                    clazz = findClass(className)!!
                } catch (e: ClassNotFoundException) {
                    suppressed = e
                }

                if (clazz == null) {
                    try {
                        clazz = mGrandParent.loadClass(className)!!
                    } catch (e: ClassNotFoundException) {
                        if (className.startsWith("com.tencent")) {
                            throw suppressed!!
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                e.addSuppressed(suppressed)
                            }
                            throw e
                        }
                    }

                }
            }

            return clazz
        }
    }
}
