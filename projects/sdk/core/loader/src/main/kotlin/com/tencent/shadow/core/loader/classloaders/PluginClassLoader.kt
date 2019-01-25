package com.tencent.shadow.core.loader.classloaders

import android.content.Context
import android.os.Build
import com.tencent.shadow.core.loader.classloaders.multidex.MultiDex
import dalvik.system.BaseDexClassLoader
import java.io.File


/**
 * 用于加载插件的ClassLoader,插件内部的classLoader树结构如下
 *                       BootClassLoader
 *                              |
 *                      xxxClassLoader
 *                        |        |
 *               PathClassLoader   |
 *                 |               |
 *     PluginClassLoaderA  CombineClassLoader
 *                                 |
 *  PluginClassLoaderB        PluginClassLoaderC
 *
*/
class PluginClassLoader(
        hostAppContext: Context, dexPath: String, optimizedDirectory: File?, private val librarySearchPath: String?, parent: ClassLoader,
        private val hostParentClassLoader: ClassLoader
) : BaseDexClassLoader(dexPath, optimizedDirectory, librarySearchPath, parent) {

    init {
        if (Build.VERSION.SDK_INT <= MultiDex.MAX_SUPPORTED_SDK_VERSION) {
            val pluginLoaderMultiDex = hostAppContext.getSharedPreferences("com.tencent.shadow.multidex", Context.MODE_PRIVATE)
            MultiDex.install(this, dexPath, optimizedDirectory, pluginLoaderMultiDex)
        }
    }

    @Throws(ClassNotFoundException::class)
    override fun loadClass(className: String, resolve: Boolean): Class<*> {
        if (parent is PluginClassLoader || parent is CombineClassLoader //如果parent是一个PluginClassLoader或者CombineClassLoader,说明是有依赖关系的，需要走正常的双亲委派
                || className.startsWith("com.tencent.shadow.runtime")
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
                        clazz = hostParentClassLoader.loadClass(className)!!
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

    fun getLibrarySearchPath() = librarySearchPath

}
