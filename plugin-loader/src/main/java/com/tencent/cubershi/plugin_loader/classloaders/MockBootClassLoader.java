package com.tencent.cubershi.plugin_loader.classloaders;

import dalvik.system.DexClassLoader;

/**
 * 模仿BootClassLoader提供Android组件class的ClassLoader
 *
 * @author cubershi
 */
public class MockBootClassLoader extends DexClassLoader {
    public MockBootClassLoader(String dexPath, String optimizedDirectory, String librarySearchPath, ClassLoader parent) {
        super(dexPath, optimizedDirectory, librarySearchPath, parent);
    }

    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return super.findClass(name);
    }
}
