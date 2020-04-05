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

package com.tencent.shadow.core.loader.infos

class PluginInfo(
        val businessName: String?,
        val partKey: String,
        val packageName: String,
        val applicationClassName: String?
) {
    private val _mActivities: MutableSet<PluginActivityInfo> = HashSet()
    private val _mServices: MutableSet<PluginServiceInfo> = HashSet()
    private val _mProviders: MutableSet<PluginProviderInfo> = HashSet()
    internal val mActivities: Set<PluginActivityInfo>
        get() = _mActivities
    internal val mServices: Set<PluginServiceInfo>
        get() = _mServices
    internal val mProviders: Set<PluginProviderInfo>
        get() = _mProviders

    internal var appComponentFactory: String? = null

    fun putActivityInfo(pluginActivityInfo: PluginActivityInfo) {
        _mActivities.add(pluginActivityInfo)
    }

    fun putServiceInfo(pluginServiceInfo: PluginServiceInfo) {
        _mServices.add(pluginServiceInfo)
    }

    fun putPluginProviderInfo(pluginProviderInfo: PluginProviderInfo) {
        _mProviders.add(pluginProviderInfo)
    }
}
