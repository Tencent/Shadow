package com.tencent.shadow.dynamic.host;

import android.content.Context;
import android.os.IBinder;

import com.tencent.shadow.runtime.container.DelegateProvider;
import com.tencent.shadow.runtime.container.DelegateProviderHolder;

import java.io.File;

public class PluginLoaderServiceLoader {

    private final static String[] sInterfaces = new String[]{"com.tencent.hydevteam.common.progress",
            "com.tencent.shadow.dynamic.host",
            "com.tencent.hydevteam.pluginframework.pluginloader",
            "com.tencent.hydevteam.pluginframework.plugincontainer",
            "com.tencent.hydevteam.pluginframework.installedplugin",
            "com.tencent.qphone.base.util"};

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
