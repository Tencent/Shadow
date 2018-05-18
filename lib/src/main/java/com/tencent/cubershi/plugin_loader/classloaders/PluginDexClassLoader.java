package com.tencent.cubershi.plugin_loader.classloaders;

import dalvik.system.DexClassLoader;

/**
 * 用于加载插件Apk的DexClassLoader.暴露loadClass方法.
 *
 * @author cubershi
 */
class PluginDexClassLoader extends DexClassLoader {
    public PluginDexClassLoader(String dexPath, String optimizedDirectory, String librarySearchPath, ClassLoader parent) {
        super(dexPath, optimizedDirectory, librarySearchPath, parent);
    }

    Class<?> loadClassX(String name, boolean resolve) throws ClassNotFoundException {
        return super.loadClass(name, resolve);
    }
}
