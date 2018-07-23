package com.tencent.shadow.loader

import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.*
import android.content.res.Resources
import android.content.res.XmlResourceParser
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.UserHandle
import com.tencent.shadow.loader.infos.PluginInfo

class PluginPackageManager(val pluginInfo: PluginInfo) : PackageManager() {
    override fun canonicalToCurrentPackageNames(names: Array<out String>?): Array<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getLaunchIntentForPackage(packageName: String?): Intent {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getResourcesForApplication(app: ApplicationInfo?): Resources {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getResourcesForApplication(appPackageName: String?): Resources {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getProviderInfo(component: ComponentName?, flags: Int): ProviderInfo {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getReceiverInfo(component: ComponentName?, flags: Int): ActivityInfo {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun queryIntentActivityOptions(caller: ComponentName?, specifics: Array<out Intent>?, intent: Intent?, flags: Int): MutableList<ResolveInfo> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun clearPackagePreferredActivities(packageName: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPackageInstaller(): PackageInstaller {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun resolveService(intent: Intent?, flags: Int): ResolveInfo {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun verifyPendingInstall(id: Int, verificationCode: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getInstantAppCookie(): ByteArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getApplicationIcon(info: ApplicationInfo?): Drawable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getApplicationIcon(packageName: String?): Drawable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun extendVerificationTimeout(id: Int, verificationCodeAtTimeout: Int, millisecondsToDelay: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getText(packageName: String?, resid: Int, appInfo: ApplicationInfo?): CharSequence {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun resolveContentProvider(name: String?, flags: Int): ProviderInfo {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getApplicationEnabledSetting(packageName: String?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun queryIntentServices(intent: Intent?, flags: Int): MutableList<ResolveInfo> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hasSystemFeature(name: String?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hasSystemFeature(name: String?, version: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getInstrumentationInfo(className: ComponentName?, flags: Int): InstrumentationInfo {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getInstalledApplications(flags: Int): MutableList<ApplicationInfo> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isPermissionRevokedByPolicy(permName: String?, pkgName: String?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getUserBadgedDrawableForDensity(drawable: Drawable?, user: UserHandle?, badgeLocation: Rect?, badgeDensity: Int): Drawable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun checkPermission(permName: String?, pkgName: String?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getInstantAppCookieMaxBytes(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getDefaultActivityIcon(): Drawable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPreferredPackages(flags: Int): MutableList<PackageInfo> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun checkSignatures(pkg1: String?, pkg2: String?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun checkSignatures(uid1: Int, uid2: Int): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addPreferredActivity(filter: IntentFilter?, match: Int, set: Array<out ComponentName>?, activity: ComponentName?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removePackageFromPreferred(packageName: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getSharedLibraries(flags: Int): MutableList<SharedLibraryInfo> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun queryIntentActivities(intent: Intent?, flags: Int): MutableList<ResolveInfo> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addPermission(info: PermissionInfo?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getActivityBanner(activityName: ComponentName?): Drawable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getActivityBanner(intent: Intent?): Drawable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getDrawable(packageName: String?, resid: Int, appInfo: ApplicationInfo?): Drawable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setComponentEnabledSetting(componentName: ComponentName?, newState: Int, flags: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getChangedPackages(sequenceNumber: Int): ChangedPackages {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getApplicationInfo(packageName: String?, flags: Int): ApplicationInfo {
        val applicationInfo = ApplicationInfo()
        applicationInfo.metaData = pluginInfo.metaData
        return applicationInfo
    }

    override fun resolveActivity(intent: Intent?, flags: Int): ResolveInfo {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun queryBroadcastReceivers(intent: Intent?, flags: Int): MutableList<ResolveInfo> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getXml(packageName: String?, resid: Int, appInfo: ApplicationInfo?): XmlResourceParser {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPackageInfo(packageName: String?, flags: Int): PackageInfo {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPackageInfo(versionedPackage: VersionedPackage?, flags: Int): PackageInfo {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPackagesHoldingPermissions(permissions: Array<out String>?, flags: Int): MutableList<PackageInfo> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addPermissionAsync(info: PermissionInfo?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getSystemAvailableFeatures(): Array<FeatureInfo> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getActivityLogo(activityName: ComponentName?): Drawable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getActivityLogo(intent: Intent?): Drawable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getSystemSharedLibraryNames(): Array<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun queryPermissionsByGroup(group: String?, flags: Int): MutableList<PermissionInfo> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun queryIntentContentProviders(intent: Intent?, flags: Int): MutableList<ResolveInfo> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getApplicationBanner(info: ApplicationInfo?): Drawable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getApplicationBanner(packageName: String?): Drawable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun queryContentProviders(processName: String?, uid: Int, flags: Int): MutableList<ProviderInfo> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPackageGids(packageName: String?): IntArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPackageGids(packageName: String?, flags: Int): IntArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getResourcesForActivity(activityName: ComponentName?): Resources {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPackagesForUid(uid: Int): Array<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPermissionGroupInfo(name: String?, flags: Int): PermissionGroupInfo {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPermissionInfo(name: String?, flags: Int): PermissionInfo {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removePermission(name: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun queryInstrumentation(targetPackage: String?, flags: Int): MutableList<InstrumentationInfo> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun clearInstantAppCookie() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addPackageToPreferred(packageName: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun currentToCanonicalPackageNames(names: Array<out String>?): Array<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPackageUid(packageName: String?, flags: Int): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getComponentEnabledSetting(componentName: ComponentName?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getLeanbackLaunchIntentForPackage(packageName: String?): Intent {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getInstalledPackages(flags: Int): MutableList<PackageInfo> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getUserBadgedIcon(icon: Drawable?, user: UserHandle?): Drawable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAllPermissionGroups(flags: Int): MutableList<PermissionGroupInfo> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getActivityInfo(component: ComponentName, flags: Int): ActivityInfo {
        val find = pluginInfo.mActivities.find {
            it.className == component.className
        }
        if (find == null) {
            throw NameNotFoundException(component.className)
        } else {
            return find.activityInfo
        }
    }

    override fun getNameForUid(uid: Int): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateInstantAppCookie(cookie: ByteArray?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getApplicationLogo(info: ApplicationInfo?): Drawable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getApplicationLogo(packageName: String?): Drawable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getApplicationLabel(info: ApplicationInfo?): CharSequence {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPreferredActivities(outFilters: MutableList<IntentFilter>?, outActivities: MutableList<ComponentName>?, packageName: String?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setApplicationCategoryHint(packageName: String?, categoryHint: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isSafeMode(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setInstallerPackageName(targetPackage: String?, installerPackageName: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getUserBadgedLabel(label: CharSequence?, user: UserHandle?): CharSequence {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getInstallerPackageName(packageName: String?): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setApplicationEnabledSetting(packageName: String?, newState: Int, flags: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun canRequestPackageInstalls(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getServiceInfo(component: ComponentName?, flags: Int): ServiceInfo {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isInstantApp(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isInstantApp(packageName: String?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getActivityIcon(activityName: ComponentName?): Drawable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getActivityIcon(intent: Intent?): Drawable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}