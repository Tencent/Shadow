package com.tencent.cubershi.plugin_loader.classloaders

import dalvik.system.DexClassLoader

/**
 * 用于加载插件的ClassLoader.
 *
 * @author cubershi
 */
class PluginClassLoader(
        dexPath: String, optimizedDirectory: String, librarySearchPath: String, parent: ClassLoader,
        private val mMockClassloader: ClassLoader,
        private val mMockClassNames: Array<String>
) : DexClassLoader(dexPath, optimizedDirectory, librarySearchPath, parent) {

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
            super.loadClass(className, resolve)
        }
    }
}
