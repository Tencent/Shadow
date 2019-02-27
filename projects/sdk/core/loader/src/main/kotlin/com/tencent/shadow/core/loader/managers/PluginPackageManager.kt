package com.tencent.shadow.core.loader.managers

import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.*
import android.content.res.Resources
import android.content.res.XmlResourceParser
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.UserHandle
import com.tencent.shadow.core.loader.ImplementLater
import com.tencent.shadow.core.loader.infos.PluginInfo

class PluginPackageManager(val commonPluginPackageManager: CommonPluginPackageManager,
                           val pluginInfo: PluginInfo) : PackageManager() {
    override fun getApplicationInfo(packageName: String?, flags: Int): ApplicationInfo {
        val applicationInfo = ApplicationInfo()
        applicationInfo.metaData = pluginInfo.metaData
        applicationInfo.className = pluginInfo.applicationClassName
        return applicationInfo
    }

    override fun getPackageInfo(packageName: String?, flags: Int): PackageInfo? {
        if (pluginInfo.packageName == packageName) {
            val info = PackageInfo()
            info.versionCode = pluginInfo.versionCode
            info.versionName = pluginInfo.versionName
            info.signatures = pluginInfo.signatures
            return info;
        }
        return null;
    }

    override fun getActivityInfo(component: ComponentName, flags: Int): ActivityInfo {
        val find = pluginInfo.mActivities.find {
            it.className == component.className
        }
        if (find == null) {
            return commonPluginPackageManager.getActivityInfo(component, flags)
        } else {
            return find.activityInfo
        }
    }

    override fun canonicalToCurrentPackageNames(names: Array<out String>?): Array<String> {
        ImplementLater()
    }

    override fun getLaunchIntentForPackage(packageName: String?): Intent {
        ImplementLater()
    }

    override fun getResourcesForApplication(app: ApplicationInfo?): Resources {
        ImplementLater()
    }

    override fun getResourcesForApplication(appPackageName: String?): Resources {
        ImplementLater()
    }

    override fun getProviderInfo(component: ComponentName?, flags: Int): ProviderInfo {
        ImplementLater()
    }

    override fun getReceiverInfo(component: ComponentName?, flags: Int): ActivityInfo {
        ImplementLater()
    }

    override fun queryIntentActivityOptions(caller: ComponentName?, specifics: Array<out Intent>?, intent: Intent?, flags: Int): MutableList<ResolveInfo> {
        ImplementLater()
    }

    override fun clearPackagePreferredActivities(packageName: String?) {
        ImplementLater()
    }

    override fun getPackageInstaller(): PackageInstaller {
        ImplementLater()
    }

    override fun resolveService(intent: Intent?, flags: Int): ResolveInfo {
        ImplementLater()
    }

    override fun verifyPendingInstall(id: Int, verificationCode: Int) {
        ImplementLater()
    }

    override fun getInstantAppCookie(): ByteArray {
        ImplementLater()
    }

    override fun getApplicationIcon(info: ApplicationInfo?): Drawable {
        ImplementLater()
    }

    override fun getApplicationIcon(packageName: String?): Drawable {
        ImplementLater()
    }

    override fun extendVerificationTimeout(id: Int, verificationCodeAtTimeout: Int, millisecondsToDelay: Long) {
        ImplementLater()
    }

    override fun getText(packageName: String?, resid: Int, appInfo: ApplicationInfo?): CharSequence {
        ImplementLater()
    }

    override fun resolveContentProvider(name: String?, flags: Int): ProviderInfo {
        val find = pluginInfo.mProviders.find {
            it.authority == name
        }
        if (find == null) {
            return ProviderInfo()
        } else {
            return find.providerInfo
        }
    }

    override fun getApplicationEnabledSetting(packageName: String?): Int {
        ImplementLater()
    }

    override fun queryIntentServices(intent: Intent?, flags: Int): MutableList<ResolveInfo> {
        ImplementLater()
    }

    override fun hasSystemFeature(name: String?): Boolean {
        ImplementLater()
    }

    override fun hasSystemFeature(name: String?, version: Int): Boolean {
        ImplementLater()
    }

    override fun getInstrumentationInfo(className: ComponentName?, flags: Int): InstrumentationInfo {
        ImplementLater()
    }

    override fun getInstalledApplications(flags: Int): MutableList<ApplicationInfo> {
        ImplementLater()
    }

    override fun isPermissionRevokedByPolicy(permName: String?, pkgName: String?): Boolean {
        ImplementLater()
    }

    override fun getUserBadgedDrawableForDensity(drawable: Drawable?, user: UserHandle?, badgeLocation: Rect?, badgeDensity: Int): Drawable {
        ImplementLater()
    }

    override fun checkPermission(permName: String?, pkgName: String?): Int {
        ImplementLater()
    }

    override fun getInstantAppCookieMaxBytes(): Int {
        ImplementLater()
    }

    override fun getDefaultActivityIcon(): Drawable {
        ImplementLater()
    }

    override fun getPreferredPackages(flags: Int): MutableList<PackageInfo> {
        ImplementLater()
    }

    override fun checkSignatures(pkg1: String?, pkg2: String?): Int {
        ImplementLater()
    }

    override fun checkSignatures(uid1: Int, uid2: Int): Int {
        ImplementLater()
    }

    override fun addPreferredActivity(filter: IntentFilter?, match: Int, set: Array<out ComponentName>?, activity: ComponentName?) {
        ImplementLater()
    }

    override fun removePackageFromPreferred(packageName: String?) {
        ImplementLater()
    }

    override fun getSharedLibraries(flags: Int): MutableList<SharedLibraryInfo> {
        ImplementLater()
    }

    override fun queryIntentActivities(intent: Intent?, flags: Int): MutableList<ResolveInfo> {
        ImplementLater()
    }

    override fun addPermission(info: PermissionInfo?): Boolean {
        ImplementLater()
    }

    override fun getActivityBanner(activityName: ComponentName?): Drawable {
        ImplementLater()
    }

    override fun getActivityBanner(intent: Intent?): Drawable {
        ImplementLater()
    }

    override fun getDrawable(packageName: String?, resid: Int, appInfo: ApplicationInfo?): Drawable {
        ImplementLater()
    }

    override fun setComponentEnabledSetting(componentName: ComponentName?, newState: Int, flags: Int) {
        ImplementLater()
    }

    override fun getChangedPackages(sequenceNumber: Int): ChangedPackages {
        ImplementLater()
    }

    override fun resolveActivity(intent: Intent?, flags: Int): ResolveInfo {
        ImplementLater()
    }

    override fun queryBroadcastReceivers(intent: Intent?, flags: Int): MutableList<ResolveInfo> {
        ImplementLater()
    }

    override fun getXml(packageName: String?, resid: Int, appInfo: ApplicationInfo?): XmlResourceParser {
        ImplementLater()
    }

    override fun getPackageInfo(versionedPackage: VersionedPackage?, flags: Int): PackageInfo {
        ImplementLater()
    }

    override fun getPackagesHoldingPermissions(permissions: Array<out String>?, flags: Int): MutableList<PackageInfo> {
        ImplementLater()
    }

    override fun addPermissionAsync(info: PermissionInfo?): Boolean {
        ImplementLater()
    }

    override fun getSystemAvailableFeatures(): Array<FeatureInfo> {
        ImplementLater()
    }

    override fun getActivityLogo(activityName: ComponentName?): Drawable {
        ImplementLater()
    }

    override fun getActivityLogo(intent: Intent?): Drawable {
        ImplementLater()
    }

    override fun getSystemSharedLibraryNames(): Array<String> {
        ImplementLater()
    }

    override fun queryPermissionsByGroup(group: String?, flags: Int): MutableList<PermissionInfo> {
        ImplementLater()
    }

    override fun queryIntentContentProviders(intent: Intent?, flags: Int): MutableList<ResolveInfo> {
        ImplementLater()
    }

    override fun getApplicationBanner(info: ApplicationInfo?): Drawable {
        ImplementLater()
    }

    override fun getApplicationBanner(packageName: String?): Drawable {
        ImplementLater()
    }

    override fun queryContentProviders(processName: String?, uid: Int, flags: Int): MutableList<ProviderInfo> {
        ImplementLater()
    }

    override fun getPackageGids(packageName: String?): IntArray {
        ImplementLater()
    }

    override fun getPackageGids(packageName: String?, flags: Int): IntArray {
        ImplementLater()
    }

    override fun getResourcesForActivity(activityName: ComponentName?): Resources {
        ImplementLater()
    }

    override fun getPackagesForUid(uid: Int): Array<String> {
        ImplementLater()
    }

    override fun getPermissionGroupInfo(name: String?, flags: Int): PermissionGroupInfo {
        ImplementLater()
    }

    override fun getPermissionInfo(name: String?, flags: Int): PermissionInfo {
        ImplementLater()
    }

    override fun removePermission(name: String?) {
        ImplementLater()
    }

    override fun queryInstrumentation(targetPackage: String?, flags: Int): MutableList<InstrumentationInfo> {
        ImplementLater()
    }

    override fun clearInstantAppCookie() {
        ImplementLater()
    }

    override fun addPackageToPreferred(packageName: String?) {
        ImplementLater()
    }

    override fun currentToCanonicalPackageNames(names: Array<out String>?): Array<String> {
        ImplementLater()
    }

    override fun getPackageUid(packageName: String?, flags: Int): Int {
        ImplementLater()
    }

    override fun getComponentEnabledSetting(componentName: ComponentName?): Int {
        ImplementLater()
    }

    override fun getLeanbackLaunchIntentForPackage(packageName: String?): Intent {
        ImplementLater()
    }

    override fun getInstalledPackages(flags: Int): MutableList<PackageInfo> {
        ImplementLater()
    }

    override fun getUserBadgedIcon(icon: Drawable?, user: UserHandle?): Drawable {
        ImplementLater()
    }

    override fun getAllPermissionGroups(flags: Int): MutableList<PermissionGroupInfo> {
        ImplementLater()
    }

    override fun getNameForUid(uid: Int): String {
        ImplementLater()
    }

    override fun updateInstantAppCookie(cookie: ByteArray?) {
        ImplementLater()
    }

    override fun getApplicationLogo(info: ApplicationInfo?): Drawable {
        ImplementLater()
    }

    override fun getApplicationLogo(packageName: String?): Drawable {
        ImplementLater()
    }

    override fun getApplicationLabel(info: ApplicationInfo?): CharSequence {
        ImplementLater()
    }

    override fun getPreferredActivities(outFilters: MutableList<IntentFilter>?, outActivities: MutableList<ComponentName>?, packageName: String?): Int {
        ImplementLater()
    }

    override fun setApplicationCategoryHint(packageName: String?, categoryHint: Int) {
        ImplementLater()
    }

    override fun isSafeMode(): Boolean {
        ImplementLater()
    }

    override fun setInstallerPackageName(targetPackage: String?, installerPackageName: String?) {
        ImplementLater()
    }

    override fun getUserBadgedLabel(label: CharSequence?, user: UserHandle?): CharSequence {
        ImplementLater()
    }

    override fun getInstallerPackageName(packageName: String?): String {
        ImplementLater()
    }

    override fun setApplicationEnabledSetting(packageName: String?, newState: Int, flags: Int) {
        ImplementLater()
    }

    override fun canRequestPackageInstalls(): Boolean {
        ImplementLater()
    }

    override fun getServiceInfo(component: ComponentName?, flags: Int): ServiceInfo {
        ImplementLater()
    }

    override fun isInstantApp(): Boolean {
        ImplementLater()
    }

    override fun isInstantApp(packageName: String?): Boolean {
        ImplementLater()
    }

    override fun getActivityIcon(activityName: ComponentName?): Drawable {
        ImplementLater()
    }

    override fun getActivityIcon(intent: Intent?): Drawable {
        ImplementLater()
    }
}