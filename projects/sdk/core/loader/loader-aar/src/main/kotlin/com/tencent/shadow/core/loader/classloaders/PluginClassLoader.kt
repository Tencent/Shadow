package com.tencent.shadow.core.loader.classloaders

import android.content.Context
import android.os.Build
import com.tencent.shadow.core.loader.classloaders.multidex.MultiDex
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

    init {
        if (Build.VERSION.SDK_INT <= MultiDex.MAX_SUPPORTED_SDK_VERSION) {
            val pluginLoaderMultiDex = hostAppContext.getSharedPreferences("com.tencent.shadow.multidex", Context.MODE_PRIVATE)
            MultiDex.install(this, dexPath, File(optimizedDirectory), pluginLoaderMultiDex)
        }
    }

    fun getLibrarySearchPath() = librarySearchPath

}
