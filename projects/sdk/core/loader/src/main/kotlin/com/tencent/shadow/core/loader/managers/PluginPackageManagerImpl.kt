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

package com.tencent.shadow.core.loader.managers

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.pm.*
import com.tencent.shadow.core.runtime.PluginPackageManager

@SuppressLint("WrongConstant")
internal class PluginPackageManagerImpl(
    private val pluginApplicationInfoFromPluginManifest: ApplicationInfo,
    private val pluginArchiveFilePath: String,
    private val componentManager: ComponentManager,
    private val hostPackageManager: PackageManager
) : PluginPackageManager {
    override fun getApplicationInfo(packageName: String, flags: Int): ApplicationInfo =
        if (packageName.isPlugin()) {
            getPluginApplicationInfo(flags)
        } else {
            hostPackageManager.getApplicationInfo(packageName, flags)
        }

    override fun getPackageInfo(packageName: String, flags: Int): PackageInfo? {
        val hostPackageInfo = hostPackageManager.getPackageInfo(packageName, flags)
        return if (packageName.isPlugin()) {
            val packageInfo = hostPackageManager.getPackageArchiveInfo(pluginArchiveFilePath, flags)
            if (packageInfo != null) {
                packageInfo.applicationInfo = getPluginApplicationInfo(flags)
                packageInfo.permissions = hostPackageInfo.permissions
                packageInfo.requestedPermissions = hostPackageInfo.requestedPermissions
            }
            packageInfo
        } else {
            hostPackageInfo
        }
    }

    override fun getActivityInfo(component: ComponentName, flags: Int): ActivityInfo? =
        getComponentInfo(
            component,
            flags,
            ComponentManager::getArchiveFilePathForActivity,
            PackageManager.GET_ACTIVITIES,
            { it?.activities },
            PackageManager::getActivityInfo
        )

    override fun getServiceInfo(component: ComponentName, flags: Int): ServiceInfo? =
        getComponentInfo(
            component,
            flags,
            ComponentManager::getArchiveFilePathForService,
            PackageManager.GET_SERVICES,
            { it?.services },
            PackageManager::getServiceInfo
        )

    override fun getProviderInfo(component: ComponentName, flags: Int): ProviderInfo {
        val (className, archiveFilePath)
                = componentManager.getArchiveFilePathForProviderByClassName(component.className)
        if (archiveFilePath != null) {
            val packageInfo = hostPackageManager.getPackageArchiveInfo(
                archiveFilePath, PackageManager.GET_PROVIDERS or flags
            )
            val componentInfo = packageInfo?.providers?.find {
                it.name == className
            }
            if (componentInfo != null) {
                return componentInfo
            }
        }
        return hostPackageManager.getProviderInfo(component, flags)
    }

    override fun resolveActivity(intent: Intent, flags: Int): ResolveInfo? {
        val component = intent.component
        if (component != null) {
            val activityInfo = getActivityInfo(component, flags)
            if (activityInfo != null) {
                val resolveInfo = ResolveInfo()
                resolveInfo.activityInfo = activityInfo
                return resolveInfo
            }
        }
        return hostPackageManager.resolveActivity(intent, flags)
    }

    override fun resolveService(intent: Intent, flags: Int): ResolveInfo? {
        val component = intent.component
        if (component != null) {
            val serviceInfo = getServiceInfo(component, flags)
            if (serviceInfo != null) {
                val resolveInfo = ResolveInfo()
                resolveInfo.serviceInfo = serviceInfo
                return resolveInfo
            }
        }
        return hostPackageManager.resolveService(intent, flags)
    }

    override fun getArchiveFilePath() = pluginArchiveFilePath

    private fun <T : ComponentInfo> getComponentInfo(
        component: ComponentName,
        flags: Int,
        getArchiveFilePath: ComponentManager.(String) -> String?,
        componentGetFlag: Int,
        getFromPackageInfo: (PackageInfo?) -> Array<T>?,
        getFromHost: PackageManager.(component: ComponentName, flags: Int) -> T?
    ): T? {

        if (component.packageName.isPlugin()) {
            val archiveFilePath = componentManager.getArchiveFilePath(component.className)
            if (archiveFilePath != null) {
                val packageInfo = hostPackageManager.getPackageArchiveInfo(
                    archiveFilePath, componentGetFlag or flags
                )
                val componentInfo = getFromPackageInfo(packageInfo)?.find {
                    it.name == component.className
                }
                if (componentInfo != null) {
                    return componentInfo
                }
            }
        }
        return hostPackageManager.getFromHost(component, flags)
    }

    override fun resolveContentProvider(name: String, flags: Int): ProviderInfo? {
        val (className, archiveFilePath) = componentManager.getArchiveFilePathForProviderByAction(
            name
        )
        if (archiveFilePath != null) {
            val packageInfo = hostPackageManager.getPackageArchiveInfo(
                archiveFilePath, PackageManager.GET_PROVIDERS or flags
            )
            val componentInfo = packageInfo?.providers?.find {
                it.name == className
            }
            if (componentInfo != null) {
                return componentInfo
            }
        }
        return hostPackageManager.resolveContentProvider(name, flags)
    }

    override fun queryContentProviders(processName: String?, uid: Int, flags: Int) =
        if (processName == null) {
            val allNormalProviders =
                hostPackageManager.queryContentProviders(null, 0, flags)
            val allPluginProviders = allPluginProviders(flags)
            listOf(allNormalProviders, allPluginProviders).flatten()
        } else if (processName == pluginApplicationInfoFromPluginManifest.processName &&
            uid == pluginApplicationInfoFromPluginManifest.uid
        ) {
            allPluginProviders(flags)
        } else {
            hostPackageManager.queryContentProviders(processName, uid, flags)
        }

    private fun allPluginProviders(flags: Int): List<ProviderInfo> =
        componentManager.getAllArchiveFilePaths().flatMap {
            val packageInfo = hostPackageManager.getPackageArchiveInfo(
                it,
                PackageManager.GET_PROVIDERS or flags
            )
            packageInfo?.providers?.asList().orEmpty()
        }

    private fun String.isPlugin() = pluginApplicationInfoFromPluginManifest.packageName == this

    private fun getPluginApplicationInfo(flags: Int): ApplicationInfo {
        val copy = ApplicationInfo(pluginApplicationInfoFromPluginManifest)

        val needMetaData = flags and PackageManager.GET_META_DATA != 0
        if (needMetaData) {
            val packageInfo = hostPackageManager.getPackageArchiveInfo(
                pluginArchiveFilePath,
                PackageManager.GET_META_DATA
            )!!
            val metaData = packageInfo.applicationInfo.metaData
            copy.metaData = metaData
        }

        return copy
    }
}