package com.tencent.cubershi.plugin_loader.classloaders;

import java.io.File;

import dalvik.system.DexClassLoader;

/**
 * 用于加载插件apk的ClassLoader
 *
 * @author cubershi
 */
public class PluginClassLoader extends DexClassLoader {
    /**
     * 用于返回自定义的Android组件的ClassLoader
     */
    final private MockBootClassLoader mMockBootClassLoader;

    public PluginClassLoader(ClassLoader parent, MockBootClassLoader mockBootClassLoader, File apk, File odexDir, File libDir) {
        super(apk.getAbsolutePath(), odexDir.getAbsolutePath(), libDir.getAbsolutePath(), parent);
        this.mMockBootClassLoader = mockBootClassLoader;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> c = findLoadedClass(name);
        if (c == null) {
            try {
                c = mMockBootClassLoader.findClass(name);
            } catch (ClassNotFoundException e) {
                c = super.loadClass(name, resolve);
            }
        }
        return c;
    }
}
