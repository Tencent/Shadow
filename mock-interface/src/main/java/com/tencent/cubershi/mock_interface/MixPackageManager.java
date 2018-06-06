package com.tencent.cubershi.mock_interface;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ChangedPackages;
import android.content.pm.FeatureInfo;
import android.content.pm.InstrumentationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.SharedLibraryInfo;
import android.content.pm.VersionedPackage;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.UserHandle;

import java.util.List;

public class MixPackageManager extends PackageManager {
    final private PackageManager mHostPackageManager;
    final private PackageManager mPluginPackageManager;

    public MixPackageManager(PackageManager mHostPackageManager, PackageManager mPluginPackageManager) {
        this.mHostPackageManager = mHostPackageManager;
        this.mPluginPackageManager = mPluginPackageManager;
    }

    @Override
    public PackageInfo getPackageInfo(String packageName, int flags) throws NameNotFoundException {
        return mHostPackageManager.getPackageInfo(packageName, flags);
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public PackageInfo getPackageInfo(VersionedPackage versionedPackage, int flags) throws NameNotFoundException {
        return mHostPackageManager.getPackageInfo(versionedPackage, flags);
    }

    @Override
    public String[] currentToCanonicalPackageNames(String[] names) {
        return mHostPackageManager.currentToCanonicalPackageNames(names);
    }

    @Override
    public String[] canonicalToCurrentPackageNames(String[] names) {
        return mHostPackageManager.canonicalToCurrentPackageNames(names);
    }

    @Override
    public Intent getLaunchIntentForPackage(String packageName) {
        return mHostPackageManager.getLaunchIntentForPackage(packageName);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public Intent getLeanbackLaunchIntentForPackage(String packageName) {
        return mHostPackageManager.getLeanbackLaunchIntentForPackage(packageName);
    }

    @Override
    public int[] getPackageGids(String packageName) throws NameNotFoundException {
        return mHostPackageManager.getPackageGids(packageName);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public int[] getPackageGids(String packageName, int flags) throws NameNotFoundException {
        return mHostPackageManager.getPackageGids(packageName, flags);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public int getPackageUid(String packageName, int flags) throws NameNotFoundException {
        return mHostPackageManager.getPackageUid(packageName, flags);
    }

    @Override
    public PermissionInfo getPermissionInfo(String name, int flags) throws NameNotFoundException {
        return mHostPackageManager.getPermissionInfo(name, flags);
    }

    @Override
    public List<PermissionInfo> queryPermissionsByGroup(String group, int flags) throws NameNotFoundException {
        return mHostPackageManager.queryPermissionsByGroup(group, flags);
    }

    @Override
    public PermissionGroupInfo getPermissionGroupInfo(String name, int flags) throws NameNotFoundException {
        return mHostPackageManager.getPermissionGroupInfo(name, flags);
    }

    @Override
    public List<PermissionGroupInfo> getAllPermissionGroups(int flags) {
        return mHostPackageManager.getAllPermissionGroups(flags);
    }

    @Override
    public ApplicationInfo getApplicationInfo(String packageName, int flags) throws NameNotFoundException {
        final ApplicationInfo applicationInfo = mHostPackageManager.getApplicationInfo(packageName, flags);
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
        return mHostPackageManager.getActivityInfo(component, flags);
    }

    @Override
    public ActivityInfo getReceiverInfo(ComponentName component, int flags) throws NameNotFoundException {
        return mHostPackageManager.getReceiverInfo(component, flags);
    }

    @Override
    public ServiceInfo getServiceInfo(ComponentName component, int flags) throws NameNotFoundException {
        return mHostPackageManager.getServiceInfo(component, flags);
    }

    @Override
    public ProviderInfo getProviderInfo(ComponentName component, int flags) throws NameNotFoundException {
        return mHostPackageManager.getProviderInfo(component, flags);
    }

    @Override
    public List<PackageInfo> getInstalledPackages(int flags) {
        return mHostPackageManager.getInstalledPackages(flags);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public List<PackageInfo> getPackagesHoldingPermissions(String[] permissions, int flags) {
        return mHostPackageManager.getPackagesHoldingPermissions(permissions, flags);
    }

    @Override
    public int checkPermission(String permName, String pkgName) {
        return mHostPackageManager.checkPermission(permName, pkgName);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public boolean isPermissionRevokedByPolicy(String permName, String pkgName) {
        return mHostPackageManager.isPermissionRevokedByPolicy(permName, pkgName);
    }

    @Override
    public boolean addPermission(PermissionInfo info) {
        return mHostPackageManager.addPermission(info);
    }

    @Override
    public boolean addPermissionAsync(PermissionInfo info) {
        return mHostPackageManager.addPermissionAsync(info);
    }

    @Override
    public void removePermission(String name) {
        mHostPackageManager.removePermission(name);
    }

    @Override
    public int checkSignatures(String pkg1, String pkg2) {
        return mHostPackageManager.checkSignatures(pkg1, pkg2);
    }

    @Override
    public int checkSignatures(int uid1, int uid2) {
        return mHostPackageManager.checkSignatures(uid1, uid2);
    }

    @Override
    public String[] getPackagesForUid(int uid) {
        return mHostPackageManager.getPackagesForUid(uid);
    }

    @Override
    public String getNameForUid(int uid) {
        return mHostPackageManager.getNameForUid(uid);
    }

    @Override
    public List<ApplicationInfo> getInstalledApplications(int flags) {
        return mHostPackageManager.getInstalledApplications(flags);
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public boolean isInstantApp() {
        return mHostPackageManager.isInstantApp();
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public boolean isInstantApp(String packageName) {
        return mHostPackageManager.isInstantApp(packageName);
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public int getInstantAppCookieMaxBytes() {
        return mHostPackageManager.getInstantAppCookieMaxBytes();
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public byte[] getInstantAppCookie() {
        return mHostPackageManager.getInstantAppCookie();
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public void clearInstantAppCookie() {
        mHostPackageManager.clearInstantAppCookie();
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public void updateInstantAppCookie(byte[] cookie) {
        mHostPackageManager.updateInstantAppCookie(cookie);
    }

    @Override
    public String[] getSystemSharedLibraryNames() {
        return mHostPackageManager.getSystemSharedLibraryNames();
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public List<SharedLibraryInfo> getSharedLibraries(int flags) {
        return mHostPackageManager.getSharedLibraries(flags);
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public ChangedPackages getChangedPackages(int sequenceNumber) {
        return mHostPackageManager.getChangedPackages(sequenceNumber);
    }

    @Override
    public FeatureInfo[] getSystemAvailableFeatures() {
        return mHostPackageManager.getSystemAvailableFeatures();
    }

    @Override
    public boolean hasSystemFeature(String name) {
        return mHostPackageManager.hasSystemFeature(name);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public boolean hasSystemFeature(String name, int version) {
        return mHostPackageManager.hasSystemFeature(name, version);
    }

    @Override
    public ResolveInfo resolveActivity(Intent intent, int flags) {
        return mHostPackageManager.resolveActivity(intent, flags);
    }

    @Override
    public List<ResolveInfo> queryIntentActivities(Intent intent, int flags) {
        return mHostPackageManager.queryIntentActivities(intent, flags);
    }

    @Override
    public List<ResolveInfo> queryIntentActivityOptions(ComponentName caller, Intent[] specifics, Intent intent, int flags) {
        return mHostPackageManager.queryIntentActivityOptions(caller, specifics, intent, flags);
    }

    @Override
    public List<ResolveInfo> queryBroadcastReceivers(Intent intent, int flags) {
        return mHostPackageManager.queryBroadcastReceivers(intent, flags);
    }

    @Override
    public ResolveInfo resolveService(Intent intent, int flags) {
        return mHostPackageManager.resolveService(intent, flags);
    }

    @Override
    public List<ResolveInfo> queryIntentServices(Intent intent, int flags) {
        return mHostPackageManager.queryIntentServices(intent, flags);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public List<ResolveInfo> queryIntentContentProviders(Intent intent, int flags) {
        return mHostPackageManager.queryIntentContentProviders(intent, flags);
    }

    @Override
    public ProviderInfo resolveContentProvider(String name, int flags) {
        return mHostPackageManager.resolveContentProvider(name, flags);
    }

    @Override
    public List<ProviderInfo> queryContentProviders(String processName, int uid, int flags) {
        return mHostPackageManager.queryContentProviders(processName, uid, flags);
    }

    @Override
    public InstrumentationInfo getInstrumentationInfo(ComponentName className, int flags) throws NameNotFoundException {
        return mHostPackageManager.getInstrumentationInfo(className, flags);
    }

    @Override
    public List<InstrumentationInfo> queryInstrumentation(String targetPackage, int flags) {
        return mHostPackageManager.queryInstrumentation(targetPackage, flags);
    }

    @Override
    public Drawable getDrawable(String packageName, int resid, ApplicationInfo appInfo) {
        return mHostPackageManager.getDrawable(packageName, resid, appInfo);
    }

    @Override
    public Drawable getActivityIcon(ComponentName activityName) throws NameNotFoundException {
        return mHostPackageManager.getActivityIcon(activityName);
    }

    @Override
    public Drawable getActivityIcon(Intent intent) throws NameNotFoundException {
        return mHostPackageManager.getActivityIcon(intent);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public Drawable getActivityBanner(ComponentName activityName) throws NameNotFoundException {
        return mHostPackageManager.getActivityBanner(activityName);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public Drawable getActivityBanner(Intent intent) throws NameNotFoundException {
        return mHostPackageManager.getActivityBanner(intent);
    }

    @Override
    public Drawable getDefaultActivityIcon() {
        return mHostPackageManager.getDefaultActivityIcon();
    }

    @Override
    public Drawable getApplicationIcon(ApplicationInfo info) {
        return mHostPackageManager.getApplicationIcon(info);
    }

    @Override
    public Drawable getApplicationIcon(String packageName) throws NameNotFoundException {
        return mHostPackageManager.getApplicationIcon(packageName);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public Drawable getApplicationBanner(ApplicationInfo info) {
        return mHostPackageManager.getApplicationBanner(info);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public Drawable getApplicationBanner(String packageName) throws NameNotFoundException {
        return mHostPackageManager.getApplicationBanner(packageName);
    }

    @Override
    public Drawable getActivityLogo(ComponentName activityName) throws NameNotFoundException {
        return mHostPackageManager.getActivityLogo(activityName);
    }

    @Override
    public Drawable getActivityLogo(Intent intent) throws NameNotFoundException {
        return mHostPackageManager.getActivityLogo(intent);
    }

    @Override
    public Drawable getApplicationLogo(ApplicationInfo info) {
        return mHostPackageManager.getApplicationLogo(info);
    }

    @Override
    public Drawable getApplicationLogo(String packageName) throws NameNotFoundException {
        return mHostPackageManager.getApplicationLogo(packageName);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public Drawable getUserBadgedIcon(Drawable icon, UserHandle user) {
        return mHostPackageManager.getUserBadgedIcon(icon, user);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public Drawable getUserBadgedDrawableForDensity(Drawable drawable, UserHandle user, Rect badgeLocation, int badgeDensity) {
        return mHostPackageManager.getUserBadgedDrawableForDensity(drawable, user, badgeLocation, badgeDensity);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public CharSequence getUserBadgedLabel(CharSequence label, UserHandle user) {
        return mHostPackageManager.getUserBadgedLabel(label, user);
    }

    @Override
    public CharSequence getText(String packageName, int resid, ApplicationInfo appInfo) {
        return mHostPackageManager.getText(packageName, resid, appInfo);
    }

    @Override
    public XmlResourceParser getXml(String packageName, int resid, ApplicationInfo appInfo) {
        return mHostPackageManager.getXml(packageName, resid, appInfo);
    }

    @Override
    public CharSequence getApplicationLabel(ApplicationInfo info) {
        return mHostPackageManager.getApplicationLabel(info);
    }

    @Override
    public Resources getResourcesForActivity(ComponentName activityName) throws NameNotFoundException {
        return mHostPackageManager.getResourcesForActivity(activityName);
    }

    @Override
    public Resources getResourcesForApplication(ApplicationInfo app) throws NameNotFoundException {
        return mHostPackageManager.getResourcesForApplication(app);
    }

    @Override
    public Resources getResourcesForApplication(String appPackageName) throws NameNotFoundException {
        return mHostPackageManager.getResourcesForApplication(appPackageName);
    }

    @Override
    public void verifyPendingInstall(int id, int verificationCode) {
        mHostPackageManager.verifyPendingInstall(id, verificationCode);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void extendVerificationTimeout(int id, int verificationCodeAtTimeout, long millisecondsToDelay) {
        mHostPackageManager.extendVerificationTimeout(id, verificationCodeAtTimeout, millisecondsToDelay);
    }

    @Override
    public void setInstallerPackageName(String targetPackage, String installerPackageName) {
        mHostPackageManager.setInstallerPackageName(targetPackage, installerPackageName);
    }

    @Override
    public String getInstallerPackageName(String packageName) {
        return mHostPackageManager.getInstallerPackageName(packageName);
    }

    @Override
    public void addPackageToPreferred(String packageName) {
        mHostPackageManager.addPackageToPreferred(packageName);
    }

    @Override
    public void removePackageFromPreferred(String packageName) {
        mHostPackageManager.removePackageFromPreferred(packageName);
    }

    @Override
    public List<PackageInfo> getPreferredPackages(int flags) {
        return mHostPackageManager.getPreferredPackages(flags);
    }

    @Override
    public void addPreferredActivity(IntentFilter filter, int match, ComponentName[] set, ComponentName activity) {
        mHostPackageManager.addPreferredActivity(filter, match, set, activity);
    }

    @Override
    public void clearPackagePreferredActivities(String packageName) {
        mHostPackageManager.clearPackagePreferredActivities(packageName);
    }

    @Override
    public int getPreferredActivities(List<IntentFilter> outFilters, List<ComponentName> outActivities, String packageName) {
        return mHostPackageManager.getPreferredActivities(outFilters, outActivities, packageName);
    }

    @Override
    public void setComponentEnabledSetting(ComponentName componentName, int newState, int flags) {
        mHostPackageManager.setComponentEnabledSetting(componentName, newState, flags);
    }

    @Override
    public int getComponentEnabledSetting(ComponentName componentName) {
        return mHostPackageManager.getComponentEnabledSetting(componentName);
    }

    @Override
    public void setApplicationEnabledSetting(String packageName, int newState, int flags) {
        mHostPackageManager.setApplicationEnabledSetting(packageName, newState, flags);
    }

    @Override
    public int getApplicationEnabledSetting(String packageName) {
        return mHostPackageManager.getApplicationEnabledSetting(packageName);
    }

    @Override
    public boolean isSafeMode() {
        return mHostPackageManager.isSafeMode();
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public void setApplicationCategoryHint(String packageName, int categoryHint) {
        mHostPackageManager.setApplicationCategoryHint(packageName, categoryHint);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public PackageInstaller getPackageInstaller() {
        return mHostPackageManager.getPackageInstaller();
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public boolean canRequestPackageInstalls() {
        return mHostPackageManager.canRequestPackageInstalls();
    }
}
