package com.tencent.shadow.core.loader.classloaders

import android.content.Context
import android.os.Build
import com.tencent.shadow.core.loader.classloaders.multidex.MultiDex
import dalvik.system.BaseDexClassLoader
import java.io.File


/**
 * 用于加载插件的ClassLoader.
 */
open class PluginClassLoader(
        hostAppContext: Context, dexPath: String, optimizedDirectory: File?, private val librarySearchPath: String?, parent: ClassLoader
) : BaseDexClassLoader(dexPath, optimizedDirectory, librarySearchPath, parent) {

    init {
        if (Build.VERSION.SDK_INT <= MultiDex.MAX_SUPPORTED_SDK_VERSION) {
            val pluginLoaderMultiDex = hostAppContext.getSharedPreferences("com.tencent.shadow.multidex", Context.MODE_PRIVATE)
            MultiDex.install(this, dexPath, optimizedDirectory, pluginLoaderMultiDex)
        }
    }

    fun getLibrarySearchPath() = librarySearchPath

}
