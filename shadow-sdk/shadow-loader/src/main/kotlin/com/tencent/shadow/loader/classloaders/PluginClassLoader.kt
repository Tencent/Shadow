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
        context: Context, dexPath: String, optimizedDirectory: String, private val librarySearchPath: String, parent: ClassLoader,
        private val mMockClassloader: ClassLoader,
        private val mMockClassNames: Array<String>
) : DexClassLoader(dexPath, optimizedDirectory, librarySearchPath, parent) {

    init {
        if (Build.VERSION.SDK_INT <= MultiDex.MAX_SUPPORTED_SDK_VERSION) {
            val pluginLoaderMultiDex = context.getSharedPreferences("com.tencent.shadow.multidex", Context.MODE_PRIVATE)
            MultiDex.install(this, dexPath, File(optimizedDirectory), pluginLoaderMultiDex)
        }
    }

    fun getLibrarySearchPath() = librarySearchPath

    @Throws(ClassNotFoundException::class)
    override fun loadClass(className: String, resolve: Boolean): Class<*> {
        var isMockClass = false
        for (mockClassName in mMockClassNames) {
            if (className == mockClassName) {
                isMockClass = true
                break
            }
        }

        return if (isMockClass) {
            mMockClassloader.loadClass(className)
        } else {
            try {
                return super.loadClass(className, resolve)
            } catch (e: ClassNotFoundException) {
                //org.apache.commons.logging是非常特殊的的包,由系统放到App的PathClassLoader中.
                if (className.startsWith("org.apache.commons.logging")) {
                    return mMockClassloader.parent.loadClass(className)
                } else {
                    throw e
                }
            }
        }
    }
}
