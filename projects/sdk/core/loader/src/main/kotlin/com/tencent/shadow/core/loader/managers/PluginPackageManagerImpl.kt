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

import android.content.ComponentName
import android.content.pm.*
import com.tencent.shadow.core.runtime.PluginPackageManager

internal class PluginPackageManagerImpl(private val hostPackageManager: PackageManager,
                                        private val packageInfo: PackageInfo,
                                        private val allPluginPackageInfo: () -> (Array<PackageInfo>))
    : PluginPackageManager {
    override fun getApplicationInfo(packageName: String?, flags: Int): ApplicationInfo =
            if (packageInfo.applicationInfo.packageName == packageName) {
                packageInfo.applicationInfo
            } else {
                hostPackageManager.getApplicationInfo(packageName, flags)
            }

    override fun getPackageInfo(packageName: String?, flags: Int): PackageInfo? =
            if (packageInfo.applicationInfo.packageName == packageName) {
                packageInfo
            } else {
                hostPackageManager.getPackageInfo(packageName, flags)
            }

    override fun getActivityInfo(component: ComponentName, flags: Int): ActivityInfo {
        if (component.packageName == packageInfo.applicationInfo.packageName) {
            val pluginActivityInfo = allPluginPackageInfo()
                    .mapNotNull { it.activities }
                    .flatMap { it.asIterable() }.find {
                        it.name == component.className
                    }
            if (pluginActivityInfo != null) {
                return pluginActivityInfo
            }
        }
        return hostPackageManager.getActivityInfo(component, flags)
    }

    override fun resolveContentProvider(name: String?, flags: Int): ProviderInfo? {
        val pluginProviderInfo = allPluginPackageInfo()
                .flatMap { it.providers.asIterable() }.find {
                    it.authority == name
                }
        if (pluginProviderInfo != null) {
            return pluginProviderInfo
        }

        return hostPackageManager.resolveContentProvider(name, flags)
    }
}