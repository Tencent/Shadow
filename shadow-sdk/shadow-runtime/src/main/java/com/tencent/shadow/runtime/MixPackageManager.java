package com.tencent.shadow.runtime;

import android.content.ComponentName;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

public class MixPackageManager extends PackageManagerWrapper {
    final private PackageManager mPluginPackageManager;

    public MixPackageManager(PackageManager mHostPackageManager, PackageManager mPluginPackageManager) {
        super(mHostPackageManager);
        this.mPluginPackageManager = mPluginPackageManager;
    }

    @Override
    public ApplicationInfo getApplicationInfo(String packageName, int flags) throws NameNotFoundException {
        final ApplicationInfo applicationInfo = super.getApplicationInfo(packageName, flags);
        final ApplicationInfo pluginAppInfo = mPluginPackageManager.getApplicationInfo(packageName, flags);
        if (pluginAppInfo.metaData != null) {
            if (applicationInfo.metaData == null) {
                applicationInfo.metaData = new Bundle();
            }
            applicationInfo.metaData.putAll(pluginAppInfo.metaData);
        }
        return applicationInfo;
    }

    @Override
    public ActivityInfo getActivityInfo(ComponentName component, int flags) throws NameNotFoundException {
        try {
            return mPluginPackageManager.getActivityInfo(component, flags);
        } catch (NameNotFoundException ignored) {
            return super.getActivityInfo(component, flags);
        }
    }

    /**
     * 获取应用的版本信息的时候，如果传入的包名是插件的，由于无法和宿主区分，这里返回插件的，宿主的用宿主的context getPackageManager去取吧
     * @param packageName 包名
     * @param flags flag
     * @return PackageInfo
     * @throws NameNotFoundException
     */
    @Override
    public PackageInfo getPackageInfo(String packageName, int flags) throws NameNotFoundException {
        PackageInfo hostInfo = super.getPackageInfo(packageName, flags);
        PackageInfo pluginInfo = mPluginPackageManager.getPackageInfo(packageName, flags);
        if(pluginInfo != null){
            hostInfo.versionCode = pluginInfo.versionCode;
            hostInfo.versionName = pluginInfo.versionName ;
        }
        return hostInfo;
    }
}
