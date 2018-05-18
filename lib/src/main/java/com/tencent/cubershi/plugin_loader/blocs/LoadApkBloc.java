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
     * @param classLoader
     * @param apk         插件apk
     * @return 加载了插件的ClassLoader
     */
    public static ClassLoader load(ClassLoader classLoader, File apk) throws LoadApkException {
        File odexDir = new File(apk.getParent(), apk.getName() + "_odex");
        File libDir = new File(apk.getParent(), apk.getName() + "_lib");

        return new PluginClassLoader(classLoader, new MockBootClassLoader(), apk, odexDir, libDir);
    }

}
