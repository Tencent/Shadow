package com.tencent.shadow.loader.classloaders

import android.content.Context
import android.os.Build
import com.tencent.shadow.loader.classloaders.multidex.MultiDex
import dalvik.system.DexClassLoader
import java.io.File


/**
 * 用于加载插件的ClassLoader.
 *
 * @author cubershi
 */
class PluginClassLoader(
        hostAppContext: Context, dexPath: String, optimizedDirectory: String, private val librarySearchPath: String, parent: ClassLoader
) : DexClassLoader(dexPath, optimizedDirectory, librarySearchPath, parent) {

    private val mGrandParent: ClassLoader

    init {
        if (Build.VERSION.SDK_INT <= MultiDex.MAX_SUPPORTED_SDK_VERSION) {
            val pluginLoaderMultiDex = hostAppContext.getSharedPreferences("com.tencent.shadow.multidex", Context.MODE_PRIVATE)
            MultiDex.install(this, dexPath, File(optimizedDirectory), pluginLoaderMultiDex)
        }
        mGrandParent = parent.parent
    }

    fun getLibrarySearchPath() = librarySearchPath

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
}
