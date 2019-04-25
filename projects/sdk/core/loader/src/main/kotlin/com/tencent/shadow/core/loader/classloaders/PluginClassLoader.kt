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
        private val specialClassLoader: ClassLoader?, whiteList: Array<String>?
) : BaseDexClassLoader(dexPath, optimizedDirectory, librarySearchPath, parent) {

    private val allWhiteList: Array<String>

    init {
        if (Build.VERSION.SDK_INT <= MultiDex.MAX_SUPPORTED_SDK_VERSION) {
            val pluginLoaderMultiDex = hostAppContext.getSharedPreferences("com.tencent.shadow.multidex", Context.MODE_PRIVATE)
            MultiDex.install(this, dexPath, optimizedDirectory, pluginLoaderMultiDex)
        }
        val defaultWhiteList = arrayOf("com.tencent.shadow.runtime",
                               "org.apache.commons.logging"//org.apache.commons.logging是非常特殊的的包,由系统放到App的PathClassLoader中.
        )
        if (whiteList != null) {
            allWhiteList = defaultWhiteList.plus(whiteList)
        }else {
            allWhiteList = defaultWhiteList
        }
    }

    @Throws(ClassNotFoundException::class)
    override fun loadClass(className: String, resolve: Boolean): Class<*> {
        if (specialClassLoader == null //specialClassLoader 为null 表示该classLoader依赖了其他的插件classLoader，需要遵循双亲委派
                || className.startWith(allWhiteList)
                || (Build.VERSION.SDK_INT < 28 && className.startsWith("org.apache.http"))) {//Android 9.0以下的系统里面带有http包，走系统的不走本地的) {
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
                        clazz = specialClassLoader.loadClass(className)!!
                    } catch (e: ClassNotFoundException) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            e.addSuppressed(suppressed)
                        }
                        throw e
                    }

                }
            }

            return clazz
        }
    }

    fun getLibrarySearchPath() = librarySearchPath


    private fun String.startWith(array: Array<String>): Boolean {
        for (str in array) {
            if (startsWith(str)) {
                return true
            }
        }
        return false
    }

}
