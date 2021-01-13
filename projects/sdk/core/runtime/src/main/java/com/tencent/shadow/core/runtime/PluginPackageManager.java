package com.tencent.shadow.core.runtime;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;

import java.util.List;

public interface PluginPackageManager {
    ApplicationInfo getApplicationInfo(String packageName, int flags);

    ActivityInfo getActivityInfo(ComponentName component, int flags);

    PackageInfo getPackageInfo(String packageName, int flags);

    ProviderInfo resolveContentProvider(String name, int flags);

    List<ProviderInfo> queryContentProviders(String processName, int uid, int flags);

    ResolveInfo resolveActivity(Intent intent, int flags);
}
