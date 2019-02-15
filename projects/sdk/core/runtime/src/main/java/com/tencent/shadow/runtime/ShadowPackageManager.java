package com.tencent.shadow.runtime;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.VersionedPackage;
import android.os.Build;
import android.os.Bundle;

import java.util.HashMap;
import java.util.Map;

/**
 * 将插件中的PackageManager部分方法转调到本类上来，以达到修改PackageManager的行为
 */
public class ShadowPackageManager {

    private static Map<ClassLoader, PackageManager> sPluginPackageManagers = new HashMap<>();

    public static void addPluginPackageManager(ClassLoader classLoader, PackageManager packageManager) {
        sPluginPackageManagers.put(classLoader, packageManager);
    }

    public static ApplicationInfo getApplicationInfo(ClassLoader classLoader, PackageManager packageManager, String packageName, int flags) throws PackageManager.NameNotFoundException {
        PackageManager pluginPackageManager = sPluginPackageManagers.get(classLoader);
        if (pluginPackageManager == null) {
            throw new RuntimeException("没有找到classLoader对应的packageManager classLoader:"+classLoader);
        }
        final ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, flags);
        final ApplicationInfo pluginAppInfo = pluginPackageManager.getApplicationInfo(packageName, flags);
        if (pluginAppInfo.metaData != null) {
            if (applicationInfo.metaData == null) {
                applicationInfo.metaData = new Bundle();
            }
            applicationInfo.metaData.putAll(pluginAppInfo.metaData);
        }
        applicationInfo.className = pluginAppInfo.className;
        return applicationInfo;
    }

    public static ActivityInfo getActivityInfo(ClassLoader classLoader, PackageManager packageManager, ComponentName component, int flags) throws PackageManager.NameNotFoundException {
        for (PackageManager pluginPackageManager : sPluginPackageManagers.values()) {
            try {
                return pluginPackageManager.getActivityInfo(component, flags);
            } catch (PackageManager.NameNotFoundException ignored) {
            }
        }
        return packageManager.getActivityInfo(component, flags);
    }

    /**
     * 获取应用的版本信息的时候，如果传入的包名是插件的，由于无法和宿主区分，这里返回插件的，宿主的用宿主的context getPackageManager去取吧
     */
    public static PackageInfo getPackageInfo(ClassLoader classLoader, PackageManager packageManager, String packageName, int flags) throws PackageManager.NameNotFoundException {
        PackageManager pluginPackageManager = sPluginPackageManagers.get(classLoader);
        if (pluginPackageManager == null) {
            throw new RuntimeException("没有找到classLoader对应的packageManager classLoader:"+classLoader);
        }
        PackageInfo hostInfo = packageManager.getPackageInfo(packageName, flags);
        getPluginPackageInfo(packageName, flags, pluginPackageManager, hostInfo);
        return hostInfo;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public static PackageInfo getPackageInfo(ClassLoader classLoader, PackageManager packageManager, VersionedPackage versionedPackage,
                                             int flags) throws PackageManager.NameNotFoundException{
        PackageManager pluginPackageManager = sPluginPackageManagers.get(classLoader);
        if (pluginPackageManager == null) {
            throw new RuntimeException("没有找到classLoader对应的packageManager classLoader:"+classLoader);
        }
        PackageInfo hostInfo = packageManager.getPackageInfo(versionedPackage, flags);
        getPluginPackageInfo(versionedPackage.getPackageName(), flags, pluginPackageManager, hostInfo);
        return hostInfo;
    }

    private static void getPluginPackageInfo(String packageName, int flags, PackageManager pluginPackageManager, PackageInfo hostInfo) throws PackageManager.NameNotFoundException {
        //当packageName为插件时 获取的PackageInfo不为空
        PackageInfo pluginInfo = pluginPackageManager.getPackageInfo(packageName, flags);
        if (pluginInfo != null) {
            hostInfo.versionCode = pluginInfo.versionCode;
            hostInfo.versionName = pluginInfo.versionName;
        }
    }


}
