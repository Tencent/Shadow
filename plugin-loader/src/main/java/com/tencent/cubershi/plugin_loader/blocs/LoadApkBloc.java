package com.tencent.cubershi.plugin_loader.blocs;

import com.tencent.cubershi.plugin_loader.classloaders.MockBootClassLoader;
import com.tencent.cubershi.plugin_loader.classloaders.PluginClassLoader;
import com.tencent.cubershi.plugin_loader.exceptions.LoadApkException;

import java.io.File;

/**
 * 加载插件到ClassLoader中
 *
 * @author cubershi
 */
public class LoadApkBloc {
    /**
     * 加载插件到ClassLoader中.
     *
     * @param parent 父ClassLoader
     * @param apk    插件apk
     * @return 加载了插件的ClassLoader
     */
    public static PluginClassLoader loadPlugin(ClassLoader parent, File apk, MockBootClassLoader mockBootClassLoader) throws LoadApkException {
        File odexDir = new File(apk.getParent(), apk.getName() + "_odex");
        File libDir = new File(apk.getParent(), apk.getName() + "_lib");
        prepareDirs(odexDir, libDir);
        return new PluginClassLoader(parent, mockBootClassLoader, apk, odexDir, libDir);
    }

    /**
     * 加载MockAndroid到ClassLoader中.
     *
     * @param parent 父ClassLoader
     * @param apk    插件apk
     * @return 加载了插件的ClassLoader
     */
    public static MockBootClassLoader loadMockAndroid(ClassLoader parent, File apk) throws LoadApkException {
        File odexDir = new File(apk.getParent(), apk.getName() + "_odex");
        File libDir = new File(apk.getParent(), apk.getName() + "_lib");
        prepareDirs(odexDir, libDir);
        return new MockBootClassLoader(apk.getAbsolutePath(), odexDir.getAbsolutePath(), libDir.getAbsolutePath(), parent);
    }

    private static void prepareDirs(File odexDir, File libDir) throws LoadApkException {
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
    }
}
