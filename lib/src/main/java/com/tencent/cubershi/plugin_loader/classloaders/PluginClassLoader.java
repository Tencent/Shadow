package com.tencent.cubershi.plugin_loader.classloaders;

import com.tencent.cubershi.plugin_loader.exceptions.LoadApkException;

import java.io.File;

public class PluginClassLoader extends ClassLoader {
    /**
     * 用于返回自定义的Android组件的ClassLoader
     */
    final private MockBootClassLoader mMockBootClassLoader;
    /**
     * 用于返回插件类的ClassLoader
     */
    final private PluginDexClassLoader mPluginDexClassLoader;

    public PluginClassLoader(ClassLoader parent, MockBootClassLoader mockBootClassLoader, File apk, File odexDir, File libDir) throws LoadApkException {
        super(parent);
        this.mMockBootClassLoader = mockBootClassLoader;
        mPluginDexClassLoader = buildPluginDexClassLoader(parent, apk, odexDir, libDir);
    }

    private PluginDexClassLoader buildPluginDexClassLoader(ClassLoader parent, File apk, File odexDir, File libDir) throws LoadApkException {
        if (odexDir.exists() && !odexDir.isDirectory()) {
            throw new LoadApkException("odexDir目标路径" + odexDir.getAbsolutePath()
                    + "已被其他文件占用");
        } else if (!odexDir.exists()) {
            boolean success = odexDir.mkdir();
            if (!success) {
                throw new LoadApkException("odexDir目标路径" + odexDir.getAbsolutePath()
                        + "创建目录失败");
            }
        }

        if (!libDir.exists()) {
            if (!libDir.mkdirs()) {
                throw new LoadApkException("libDir目标路径" + libDir.getAbsolutePath()
                        + "创建目录失败");
            }
        }

        return new PluginDexClassLoader(apk.getAbsolutePath(), odexDir.getAbsolutePath(), libDir.getAbsolutePath(), parent);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> c = findLoadedClass(name);
        if (c == null) {
            try {
                c = mMockBootClassLoader.findClass(name);
            } catch (ClassNotFoundException e) {
                c = mPluginDexClassLoader.loadClassX(name, resolve);
            }
        }
        return c;
    }
}
