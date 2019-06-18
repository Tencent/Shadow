/*
 * Tencent is pleased to support the open source community by making Tencent Shadow available.
 * Copyright (C) 2019 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tencent.shadow.core.runtime;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.VersionedPackage;
import android.os.Build;
import android.os.Bundle;

/**
 * 将插件中的PackageManager部分方法转调到本类上来，以达到修改PackageManager的行为
 */
public class ShadowPackageManager {

    /**
     * @param classLoader 对应插件所在的classLoader
     * @param packageManager 宿主的packageManager
     * @param packageName 包名
     * @param flags flag
     * @return  如果包名为插件包名则返回插件对应的applicationInfo，否则返回宿主packageManager查询到的applicationInfo
     * @throws PackageManager.NameNotFoundException
     */
    public static ApplicationInfo getApplicationInfo(ClassLoader classLoader, PackageManager packageManager, String packageName, int flags) throws PackageManager.NameNotFoundException {
        PluginPartInfo pluginPartInfo = PluginPartInfoManager.getPluginInfo(classLoader);
        final ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, flags);
        final ApplicationInfo pluginAppInfo = pluginPartInfo.packageManager.getApplicationInfo(packageName, flags);
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
        for (PluginPartInfo pluginPartInfo : PluginPartInfoManager.getAllPluginInfo()) {
            try {
                return pluginPartInfo.packageManager.getActivityInfo(component, flags);
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
        PluginPartInfo pluginPartInfo = PluginPartInfoManager.getPluginInfo(classLoader);
        PackageManager pluginPackageManager = pluginPartInfo.packageManager;
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
        PluginPartInfo pluginPartInfo = PluginPartInfoManager.getPluginInfo(classLoader);
        PackageManager pluginPackageManager = pluginPartInfo.packageManager;
        PackageInfo hostInfo = packageManager.getPackageInfo(versionedPackage, flags);
        getPluginPackageInfo(versionedPackage.getPackageName(), flags, pluginPackageManager, hostInfo);
        return hostInfo;
    }

    private static void getPluginPackageInfo(String packageName, int flags, PackageManager pluginPackageManager, PackageInfo hostInfo) throws PackageManager.NameNotFoundException {
        PackageInfo pluginInfo = pluginPackageManager.getPackageInfo(packageName, flags);
        if (pluginInfo != null) {
            hostInfo.versionCode = pluginInfo.versionCode;
            hostInfo.versionName = pluginInfo.versionName;
        }
    }

    /**
     * @param classLoader    对应插件所在的classLoader
     * @param packageManager 宿主的packageManager
     * @param name           要查询的ProviderInfo
     * @param flags          flags
     * @return 从所有插件中查询对应name的ProviderInfo，查询不到则从宿主packageManager中继续查找
     */
    public static ProviderInfo resolveContentProvider(ClassLoader classLoader, PackageManager packageManager, String name, int flags) {
        for (PluginPartInfo pluginPartInfo : PluginPartInfoManager.getAllPluginInfo()) {
            ProviderInfo providerInfo = pluginPartInfo.packageManager.resolveContentProvider(name, flags);
            if (providerInfo != null) {
                return providerInfo;
            }
        }
        return packageManager.resolveContentProvider(name, flags);
    }


}
