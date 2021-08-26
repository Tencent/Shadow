package com.tencent.shadow.core.loader.delegates;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ChangedPackages;
import android.content.pm.FeatureInfo;
import android.content.pm.InstallSourceInfo;
import android.content.pm.InstrumentationInfo;
import android.content.pm.ModuleInfo;
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
import android.os.Bundle;
import android.os.UserHandle;

import java.util.List;
import java.util.Set;

@SuppressLint("NewApi")
class PackageManagerWrapper extends PackageManager {
    final private PackageManager proxy;

    PackageManagerWrapper(PackageManager proxy) {
        this.proxy = proxy;
    }

    @Override
    public PackageInfo getPackageInfo(String packageName, int flags) throws NameNotFoundException {
        return proxy.getPackageInfo(packageName, flags);
    }

    @Override
    public PackageInfo getPackageInfo(VersionedPackage versionedPackage, int flags) throws NameNotFoundException {
        return proxy.getPackageInfo(versionedPackage, flags);
    }

    @Override
    public String[] currentToCanonicalPackageNames(String[] packageNames) {
        return proxy.currentToCanonicalPackageNames(packageNames);
    }

    @Override
    public String[] canonicalToCurrentPackageNames(String[] packageNames) {
        return proxy.canonicalToCurrentPackageNames(packageNames);
    }

    @Override
    public Intent getLaunchIntentForPackage(String packageName) {
        return proxy.getLaunchIntentForPackage(packageName);
    }

    @Override
    public Intent getLeanbackLaunchIntentForPackage(String packageName) {
        return proxy.getLeanbackLaunchIntentForPackage(packageName);
    }

    @Override
    public int[] getPackageGids(String packageName) throws NameNotFoundException {
        return proxy.getPackageGids(packageName);
    }

    @Override
    public int[] getPackageGids(String packageName, int flags) throws NameNotFoundException {
        return proxy.getPackageGids(packageName, flags);
    }

    @Override
    public int getPackageUid(String packageName, int flags) throws NameNotFoundException {
        return proxy.getPackageUid(packageName, flags);
    }

    @Override
    public PermissionInfo getPermissionInfo(String permName, int flags) throws NameNotFoundException {
        return proxy.getPermissionInfo(permName, flags);
    }

    @Override
    public List<PermissionInfo> queryPermissionsByGroup(String permissionGroup, int flags) throws NameNotFoundException {
        return proxy.queryPermissionsByGroup(permissionGroup, flags);
    }

    @Override
    public PermissionGroupInfo getPermissionGroupInfo(String permName, int flags) throws NameNotFoundException {
        return proxy.getPermissionGroupInfo(permName, flags);
    }

    @Override
    public List<PermissionGroupInfo> getAllPermissionGroups(int flags) {
        return proxy.getAllPermissionGroups(flags);
    }

    @Override
    public ApplicationInfo getApplicationInfo(String packageName, int flags) throws NameNotFoundException {
        return proxy.getApplicationInfo(packageName, flags);
    }

    @Override
    public ActivityInfo getActivityInfo(ComponentName component, int flags) throws NameNotFoundException {
        return proxy.getActivityInfo(component, flags);
    }

    @Override
    public ActivityInfo getReceiverInfo(ComponentName component, int flags) throws NameNotFoundException {
        return proxy.getReceiverInfo(component, flags);
    }

    @Override
    public ServiceInfo getServiceInfo(ComponentName component, int flags) throws NameNotFoundException {
        return proxy.getServiceInfo(component, flags);
    }

    @Override
    public ProviderInfo getProviderInfo(ComponentName component, int flags) throws NameNotFoundException {
        return proxy.getProviderInfo(component, flags);
    }

    @Override
    public ModuleInfo getModuleInfo(String packageName, int flags) throws NameNotFoundException {
        return proxy.getModuleInfo(packageName, flags);
    }

    @Override
    public List<ModuleInfo> getInstalledModules(int flags) {
        return proxy.getInstalledModules(flags);
    }

    @Override
    public List<PackageInfo> getInstalledPackages(int flags) {
        return proxy.getInstalledPackages(flags);
    }

    @Override
    public List<PackageInfo> getPackagesHoldingPermissions(String[] permissions, int flags) {
        return proxy.getPackagesHoldingPermissions(permissions, flags);
    }

    @Override
    public int checkPermission(String permName, String packageName) {
        return proxy.checkPermission(permName, packageName);
    }

    @Override
    public boolean isPermissionRevokedByPolicy(String permName, String packageName) {
        return proxy.isPermissionRevokedByPolicy(permName, packageName);
    }

    @Override
    public boolean addPermission(PermissionInfo info) {
        return proxy.addPermission(info);
    }

    @Override
    public boolean addPermissionAsync(PermissionInfo info) {
        return proxy.addPermissionAsync(info);
    }

    @Override
    public void removePermission(String permName) {
        proxy.removePermission(permName);
    }

    @Override
    public Set<String> getWhitelistedRestrictedPermissions(String packageName, int whitelistFlag) {
        return proxy.getWhitelistedRestrictedPermissions(packageName, whitelistFlag);
    }

    @Override
    public boolean addWhitelistedRestrictedPermission(String packageName, String permName, int whitelistFlags) {
        return proxy.addWhitelistedRestrictedPermission(packageName, permName, whitelistFlags);
    }

    @Override
    public boolean removeWhitelistedRestrictedPermission(String packageName, String permName, int whitelistFlags) {
        return proxy.removeWhitelistedRestrictedPermission(packageName, permName, whitelistFlags);
    }

    @Override
    public boolean setAutoRevokeWhitelisted(String packageName, boolean whitelisted) {
        return proxy.setAutoRevokeWhitelisted(packageName, whitelisted);
    }

    @Override
    public boolean isAutoRevokeWhitelisted(String packageName) {
        return proxy.isAutoRevokeWhitelisted(packageName);
    }

    @Override
    public CharSequence getBackgroundPermissionOptionLabel() {
        return proxy.getBackgroundPermissionOptionLabel();
    }

    @Override
    public int checkSignatures(String packageName1, String packageName2) {
        return proxy.checkSignatures(packageName1, packageName2);
    }

    @Override
    public int checkSignatures(int uid1, int uid2) {
        return proxy.checkSignatures(uid1, uid2);
    }

    @Override
    public String[] getPackagesForUid(int uid) {
        return proxy.getPackagesForUid(uid);
    }

    @Override
    public String getNameForUid(int uid) {
        return proxy.getNameForUid(uid);
    }

    @Override
    public List<ApplicationInfo> getInstalledApplications(int flags) {
        return proxy.getInstalledApplications(flags);
    }

    @Override
    public boolean isInstantApp() {
        return proxy.isInstantApp();
    }

    @Override
    public boolean isInstantApp(String packageName) {
        return proxy.isInstantApp(packageName);
    }

    @Override
    public int getInstantAppCookieMaxBytes() {
        return proxy.getInstantAppCookieMaxBytes();
    }

    @Override
    public byte[] getInstantAppCookie() {
        return proxy.getInstantAppCookie();
    }

    @Override
    public void clearInstantAppCookie() {
        proxy.clearInstantAppCookie();
    }

    @Override
    public void updateInstantAppCookie(byte[] cookie) {
        proxy.updateInstantAppCookie(cookie);
    }

    @Override
    public String[] getSystemSharedLibraryNames() {
        return proxy.getSystemSharedLibraryNames();
    }

    @Override
    public List<SharedLibraryInfo> getSharedLibraries(int flags) {
        return proxy.getSharedLibraries(flags);
    }

    @Override
    public ChangedPackages getChangedPackages(int sequenceNumber) {
        return proxy.getChangedPackages(sequenceNumber);
    }

    @Override
    public FeatureInfo[] getSystemAvailableFeatures() {
        return proxy.getSystemAvailableFeatures();
    }

    @Override
    public boolean hasSystemFeature(String featureName) {
        return proxy.hasSystemFeature(featureName);
    }

    @Override
    public boolean hasSystemFeature(String featureName, int version) {
        return proxy.hasSystemFeature(featureName, version);
    }

    @Override
    public ResolveInfo resolveActivity(Intent intent, int flags) {
        return proxy.resolveActivity(intent, flags);
    }

    @Override
    public List<ResolveInfo> queryIntentActivities(Intent intent, int flags) {
        return proxy.queryIntentActivities(intent, flags);
    }

    @Override
    public List<ResolveInfo> queryIntentActivityOptions(ComponentName caller, Intent[] specifics, Intent intent, int flags) {
        return proxy.queryIntentActivityOptions(caller, specifics, intent, flags);
    }

    @Override
    public List<ResolveInfo> queryBroadcastReceivers(Intent intent, int flags) {
        return proxy.queryBroadcastReceivers(intent, flags);
    }

    @Override
    public ResolveInfo resolveService(Intent intent, int flags) {
        return proxy.resolveService(intent, flags);
    }

    @Override
    public List<ResolveInfo> queryIntentServices(Intent intent, int flags) {
        return proxy.queryIntentServices(intent, flags);
    }

    @Override
    public List<ResolveInfo> queryIntentContentProviders(Intent intent, int flags) {
        return proxy.queryIntentContentProviders(intent, flags);
    }

    @Override
    public ProviderInfo resolveContentProvider(String authority, int flags) {
        return proxy.resolveContentProvider(authority, flags);
    }

    @Override
    public List<ProviderInfo> queryContentProviders(String processName, int uid, int flags) {
        return proxy.queryContentProviders(processName, uid, flags);
    }

    @Override
    public InstrumentationInfo getInstrumentationInfo(ComponentName className, int flags) throws NameNotFoundException {
        return proxy.getInstrumentationInfo(className, flags);
    }

    @Override
    public List<InstrumentationInfo> queryInstrumentation(String targetPackage, int flags) {
        return proxy.queryInstrumentation(targetPackage, flags);
    }

    @Override
    public Drawable getDrawable(String packageName, int resid, ApplicationInfo appInfo) {
        return proxy.getDrawable(packageName, resid, appInfo);
    }

    @Override
    public Drawable getActivityIcon(ComponentName activityName) throws NameNotFoundException {
        return proxy.getActivityIcon(activityName);
    }

    @Override
    public Drawable getActivityIcon(Intent intent) throws NameNotFoundException {
        return proxy.getActivityIcon(intent);
    }

    @Override
    public Drawable getActivityBanner(ComponentName activityName) throws NameNotFoundException {
        return proxy.getActivityBanner(activityName);
    }

    @Override
    public Drawable getActivityBanner(Intent intent) throws NameNotFoundException {
        return proxy.getActivityBanner(intent);
    }

    @Override
    public Drawable getDefaultActivityIcon() {
        return proxy.getDefaultActivityIcon();
    }

    @Override
    public Drawable getApplicationIcon(ApplicationInfo info) {
        return proxy.getApplicationIcon(info);
    }

    @Override
    public Drawable getApplicationIcon(String packageName) throws NameNotFoundException {
        return proxy.getApplicationIcon(packageName);
    }

    @Override
    public Drawable getApplicationBanner(ApplicationInfo info) {
        return proxy.getApplicationBanner(info);
    }

    @Override
    public Drawable getApplicationBanner(String packageName) throws NameNotFoundException {
        return proxy.getApplicationBanner(packageName);
    }

    @Override
    public Drawable getActivityLogo(ComponentName activityName) throws NameNotFoundException {
        return proxy.getActivityLogo(activityName);
    }

    @Override
    public Drawable getActivityLogo(Intent intent) throws NameNotFoundException {
        return proxy.getActivityLogo(intent);
    }

    @Override
    public Drawable getApplicationLogo(ApplicationInfo info) {
        return proxy.getApplicationLogo(info);
    }

    @Override
    public Drawable getApplicationLogo(String packageName) throws NameNotFoundException {
        return proxy.getApplicationLogo(packageName);
    }

    @Override
    public Drawable getUserBadgedIcon(Drawable drawable, UserHandle user) {
        return proxy.getUserBadgedIcon(drawable, user);
    }

    @Override
    public Drawable getUserBadgedDrawableForDensity(Drawable drawable, UserHandle user, Rect badgeLocation, int badgeDensity) {
        return proxy.getUserBadgedDrawableForDensity(drawable, user, badgeLocation, badgeDensity);
    }

    @Override
    public CharSequence getUserBadgedLabel(CharSequence label, UserHandle user) {
        return proxy.getUserBadgedLabel(label, user);
    }

    @Override
    public CharSequence getText(String packageName, int resid, ApplicationInfo appInfo) {
        return proxy.getText(packageName, resid, appInfo);
    }

    @Override
    public XmlResourceParser getXml(String packageName, int resid, ApplicationInfo appInfo) {
        return proxy.getXml(packageName, resid, appInfo);
    }

    @Override
    public CharSequence getApplicationLabel(ApplicationInfo info) {
        return proxy.getApplicationLabel(info);
    }

    @Override
    public Resources getResourcesForActivity(ComponentName activityName) throws NameNotFoundException {
        return proxy.getResourcesForActivity(activityName);
    }

    @Override
    public Resources getResourcesForApplication(ApplicationInfo app) throws NameNotFoundException {
        return proxy.getResourcesForApplication(app);
    }

    @Override
    public Resources getResourcesForApplication(String packageName) throws NameNotFoundException {
        return proxy.getResourcesForApplication(packageName);
    }

    @Override
    public PackageInfo getPackageArchiveInfo(String archiveFilePath, int flags) {
        return proxy.getPackageArchiveInfo(archiveFilePath, flags);
    }

    @Override
    public void verifyPendingInstall(int id, int verificationCode) {
        proxy.verifyPendingInstall(id, verificationCode);
    }

    @Override
    public void extendVerificationTimeout(int id, int verificationCodeAtTimeout, long millisecondsToDelay) {
        proxy.extendVerificationTimeout(id, verificationCodeAtTimeout, millisecondsToDelay);
    }

    @Override
    public void setInstallerPackageName(String targetPackage, String installerPackageName) {
        proxy.setInstallerPackageName(targetPackage, installerPackageName);
    }

    @Override
    @Deprecated
    public String getInstallerPackageName(String packageName) {
        return proxy.getInstallerPackageName(packageName);
    }

    @Override
    public InstallSourceInfo getInstallSourceInfo(String packageName) throws NameNotFoundException {
        return proxy.getInstallSourceInfo(packageName);
    }

    @Override
    @Deprecated
    public void addPackageToPreferred(String packageName) {
        proxy.addPackageToPreferred(packageName);
    }

    @Override
    @Deprecated
    public void removePackageFromPreferred(String packageName) {
        proxy.removePackageFromPreferred(packageName);
    }

    @Override
    @Deprecated
    public List<PackageInfo> getPreferredPackages(int flags) {
        return proxy.getPreferredPackages(flags);
    }

    @Override
    @Deprecated
    public void addPreferredActivity(IntentFilter filter, int match, ComponentName[] set, ComponentName activity) {
        proxy.addPreferredActivity(filter, match, set, activity);
    }

    @Override
    @Deprecated
    public void clearPackagePreferredActivities(String packageName) {
        proxy.clearPackagePreferredActivities(packageName);
    }

    @Override
    @Deprecated
    public int getPreferredActivities(List<IntentFilter> outFilters, List<ComponentName> outActivities, String packageName) {
        return proxy.getPreferredActivities(outFilters, outActivities, packageName);
    }

    @Override
    public void setComponentEnabledSetting(ComponentName componentName, int newState, int flags) {
        proxy.setComponentEnabledSetting(componentName, newState, flags);
    }

    @Override
    public int getComponentEnabledSetting(ComponentName componentName) {
        return proxy.getComponentEnabledSetting(componentName);
    }

    @Override
    public boolean getSyntheticAppDetailsActivityEnabled(String packageName) {
        return proxy.getSyntheticAppDetailsActivityEnabled(packageName);
    }

    @Override
    public void setApplicationEnabledSetting(String packageName, int newState, int flags) {
        proxy.setApplicationEnabledSetting(packageName, newState, flags);
    }

    @Override
    public int getApplicationEnabledSetting(String packageName) {
        return proxy.getApplicationEnabledSetting(packageName);
    }

    @Override
    public boolean isSafeMode() {
        return proxy.isSafeMode();
    }

    @Override
    public boolean isPackageSuspended(String packageName) throws NameNotFoundException {
        return proxy.isPackageSuspended(packageName);
    }

    @Override
    public boolean isPackageSuspended() {
        return proxy.isPackageSuspended();
    }

    @Override
    public Bundle getSuspendedPackageAppExtras() {
        return proxy.getSuspendedPackageAppExtras();
    }

    @Override
    public void setApplicationCategoryHint(String packageName, int categoryHint) {
        proxy.setApplicationCategoryHint(packageName, categoryHint);
    }

    @Override
    public boolean isDeviceUpgrading() {
        return proxy.isDeviceUpgrading();
    }

    @Override
    public PackageInstaller getPackageInstaller() {
        return proxy.getPackageInstaller();
    }

    @Override
    public boolean canRequestPackageInstalls() {
        return proxy.canRequestPackageInstalls();
    }

    @Override
    public boolean hasSigningCertificate(String packageName, byte[] certificate, int type) {
        return proxy.hasSigningCertificate(packageName, certificate, type);
    }

    @Override
    public boolean hasSigningCertificate(int uid, byte[] certificate, int type) {
        return proxy.hasSigningCertificate(uid, certificate, type);
    }

    @Override
    public boolean isAutoRevokeWhitelisted() {
        return proxy.isAutoRevokeWhitelisted();
    }

    @Override
    public boolean isDefaultApplicationIcon(Drawable drawable) {
        return proxy.isDefaultApplicationIcon(drawable);
    }

    @Override
    public void setMimeGroup(String mimeGroup, Set<String> mimeTypes) {
        proxy.setMimeGroup(mimeGroup, mimeTypes);
    }

    @Override
    public Set<String> getMimeGroup(String mimeGroup) {
        return proxy.getMimeGroup(mimeGroup);
    }
}
