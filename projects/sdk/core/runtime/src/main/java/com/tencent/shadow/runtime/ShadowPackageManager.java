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

    /**
     * @param classLoader 对应插件所在的classLoader
     * @param packageManager 宿主的packageManager
     * @param packageName 包名
     * @param flags flag
     * @return  如果包名为插件包名则返回插件对应的applicationInfo，否则返回宿主packageManager查询到的applicationInfo
     * @throws PackageManager.NameNotFoundException
     */
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

    /**
     *
     * @param classLoader classLoader 对应插件所在的classLoader
     * @param packageManager 宿主的packageManager
     * @param component 要查询的component
     * @param flags flags
     * @return  从所有插件中查询对应component的ActivityInfo，查询不到则从宿主packageManager中继续查找
     * @throws PackageManager.NameNotFoundException
     */
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
     *
     * @param classLoader classLoader 对应插件所在的classLoader
     * @param packageManager 宿主的packageManager
     * @param packageName 要查询的packageName
     * @param flags flags
     * @return  如果包名为插件包名则包含当前插件的版本信息的PackageInfo
     * @throws PackageManager.NameNotFoundException
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

    /**
     *
     * @param classLoader classLoader 对应插件所在的classLoader
     * @param packageManager 宿主的packageManager
     * @param versionedPackage 要查询的versionedPackage
     * @param flags flags
     * @return  如果包名为插件包名则包含当前插件的版本信息的PackageInfo
     * @throws PackageManager.NameNotFoundException
     */
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
