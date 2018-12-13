package com.tencent.shadow.dynamic.host;

import android.content.Context;
import android.os.IBinder;

import com.tencent.shadow.runtime.container.DelegateProvider;
import com.tencent.shadow.runtime.container.DelegateProviderHolder;

import java.io.File;

public class PluginLoaderServiceLoader {

    /**
     * 加载{@link PluginLoaderServiceLoader#CLASS_NAME_LOADER}时
     * 需要从宿主PathClassLoader（含双亲委派）中加载的类
     */
    private final static String[] sInterfaces = new String[]{
            //当runtime是动态加载的时候，runtime的ClassLoader是PathClassLoader的parent，
            // 所以不需要写在这个白名单里。但是写在这里不影响，也可以兼容runtime打包在宿主的情况。
            "com.tencent.shadow.runtime.container",
            "com.tencent.shadow.dynamic.host",
    };

    private final static String CLASS_NAME_LOADER = "com.tencent.shadow.dynamic.loader.PluginLoaderService";

    public static IBinder loadPluginLoaderService(Context context, String UUID, String apkPath) {
        File file = new File(apkPath);
        if (!file.exists()) {
            throw new RuntimeException(file.getAbsolutePath() + "文件不存在");
        }
        File odexDir = new File(file.getParent(), "plugin_loader_odex_" + UUID);
        odexDir.mkdirs();
        ApkClassLoader pluginLoaderClassLoader = new ApkClassLoader(apkPath,
                odexDir.getAbsolutePath(), null, PluginLoaderServiceLoader.class.getClassLoader(), sInterfaces);
        try {
            IBinder iBinder = pluginLoaderClassLoader.getInterface(IBinder.class, CLASS_NAME_LOADER, new Class[]{Context.class}, new Object[]{context.getApplicationContext()});
            DelegateProviderHolder.setDelegateProvider((DelegateProvider) iBinder);
            return iBinder;
        } catch (Exception e) {
            throw new RuntimeException(pluginLoaderClassLoader + " 没有找到：" + CLASS_NAME_LOADER, e);
        }
    }
}
