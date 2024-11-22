package com.tencent.shadow.core.runtime;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.VersionedPackage;
import android.os.Build;

import java.util.List;

public interface PluginPackageManager {
    ApplicationInfo getApplicationInfo(String packageName, int flags);

    ActivityInfo getActivityInfo(ComponentName component, int flags);

    ServiceInfo getServiceInfo(ComponentName component, int flags);

    ProviderInfo getProviderInfo(ComponentName component, int flags);

    PackageInfo getPackageInfo(String packageName, int flags);

    PackageInfo getPackageInfo(VersionedPackage versionedPackage, int flags);

    @TargetApi(Build.VERSION_CODES.TIRAMISU)
    PackageInfo getPackageInfo(VersionedPackage versionedPackage, PackageManager.PackageInfoFlags flags);

    @TargetApi(Build.VERSION_CODES.TIRAMISU)
    PackageInfo getPackageInfo(String packageName, PackageManager.PackageInfoFlags flags);

    ProviderInfo resolveContentProvider(String name, int flags);

    List<ProviderInfo> queryContentProviders(String processName, int uid, int flags);

    ResolveInfo resolveActivity(Intent intent, int flags);

    ResolveInfo resolveService(Intent intent, int flags);

    String getArchiveFilePath();
}
