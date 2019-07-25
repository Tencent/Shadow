package com.tencent.shadow.core.runtime;

import android.content.ComponentName;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.ProviderInfo;

public interface PluginPackageManager {
    ApplicationInfo getApplicationInfo(String packageName, int flags);

    ActivityInfo getActivityInfo(ComponentName component, int flags);

    PackageInfo getPackageInfo(String packageName, int flags);

    ProviderInfo resolveContentProvider(String name, int flags);
}
