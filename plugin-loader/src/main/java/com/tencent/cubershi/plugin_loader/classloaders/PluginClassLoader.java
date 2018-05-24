package com.tencent.cubershi.plugin_loader.classloaders;

import dalvik.system.DexClassLoader;

/**
 * 用于加载插件的ClassLoader.
 *
 * @author cubershi
 */
public class PluginClassLoader extends DexClassLoader {


    private final ClassLoader mMockClassloader;
    private final String[] mMockClassNames;

    public PluginClassLoader(
            String dexPath, String optimizedDirectory, String librarySearchPath, ClassLoader parent,
            ClassLoader mockClassLoader,
            String[] mockClassNames
    ) {
        super(dexPath, optimizedDirectory, librarySearchPath, parent);
        mMockClassloader = mockClassLoader;
        mMockClassNames = mockClassNames;
    }

    @Override
    protected Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException {
        boolean isMockClass = false;
        for (String mockClassName : mMockClassNames) {
            if (className.equals(mockClassName)) {
                isMockClass = true;
                break;
            }
        }

        if (isMockClass) {
            return mMockClassloader.loadClass(className);
        } else {
            return super.loadClass(className, resolve);
        }
    }
}
