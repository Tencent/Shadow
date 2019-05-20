package com.tencent.shadow.core.runtime;

import android.content.ComponentName;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;


/**
 * 将插件中的PackageManager部分方法转调到本类上来，以达到修改PackageManager的行为
 */
public class ShadowPackageManager {


    public static ApplicationInfo getApplicationInfo(ClassLoader classLoader, PackageManager packageManager, String packageName, int flags) {
        return null;
    }


    public static ActivityInfo getActivityInfo(ClassLoader classLoader, PackageManager packageManager, ComponentName component, int flags) {
        return null;
    }


    public static PackageInfo getPackageInfo(ClassLoader classLoader, PackageManager packageManager, String packageName, int flags) {
        return null;
    }


    public static ProviderInfo resolveContentProvider(ClassLoader classLoader, PackageManager packageManager, String name, int flags) {

        return null;
    }


}
