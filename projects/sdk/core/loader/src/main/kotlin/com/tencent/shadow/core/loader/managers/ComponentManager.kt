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
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.util.Pair
import com.tencent.shadow.core.loader.BuildConfig
import com.tencent.shadow.core.loader.infos.ContainerProviderInfo
import com.tencent.shadow.core.loader.infos.PluginComponentInfo
import com.tencent.shadow.core.loader.infos.PluginInfo
import com.tencent.shadow.core.runtime.ShadowContext
import com.tencent.shadow.core.runtime.ShadowContext.PluginComponentLauncher
import com.tencent.shadow.core.runtime.container.DelegateProvider.LOADER_VERSION_KEY
import com.tencent.shadow.core.runtime.container.DelegateProvider.PROCESS_ID_KEY
import com.tencent.shadow.core.runtime.container.DelegateProviderHolder
import com.tencent.shadow.core.runtime.container.GeneratedHostActivityDelegator

/**
 * 插件组件管理
 * 主要功能是管理组件和宿主中注册的壳子之间的配对关系
 *
 * @author cubershi
 */
abstract class ComponentManager : PluginComponentLauncher {
    companion object {
        const val CM_LOADER_BUNDLE_KEY = "CM_LOADER_BUNDLE"
        const val CM_EXTRAS_BUNDLE_KEY = "CM_EXTRAS_BUNDLE"
        const val CM_ACTIVITY_INFO_KEY = "CM_ACTIVITY_INFO"
        const val CM_CLASS_NAME_KEY = "CM_CLASS_NAME"
        const val CM_CALLING_ACTIVITY_KEY = "CM_CALLING_ACTIVITY_KEY"
        const val CM_PACKAGE_NAME_KEY = "CM_PACKAGE_NAME"
        const val CM_BUSINESS_NAME_KEY = "CM_BUSINESS_NAME"
        const val CM_PART_KEY = "CM_PART"
    }

    /**
     * @param pluginActivity 插件Activity
     * @return 容器Activity
     */
    abstract fun onBindContainerActivity(pluginActivity: ComponentName): ComponentName

    abstract fun onBindContainerContentProvider(pluginContentProvider: ComponentName): ContainerProviderInfo

    abstract fun getBroadcastInfoList(partKey: String): List<BroadcastInfo>?

    override fun startActivity(shadowContext: ShadowContext, pluginIntent: Intent, option: Bundle?): Boolean {
        return if (pluginIntent.isPluginComponent()) {
            shadowContext.superStartActivity(pluginIntent.toActivityContainerIntent(), option)
            true
        } else {
            false
        }
    }


    override fun startActivityForResult(delegator: GeneratedHostActivityDelegator, pluginIntent: Intent, requestCode: Int, option: Bundle?, callingActivity: ComponentName): Boolean {
        return if (pluginIntent.isPluginComponent()) {
            val containerIntent = pluginIntent.toActivityContainerIntent()
            containerIntent.putExtra(CM_CALLING_ACTIVITY_KEY,callingActivity)
            delegator.startActivityForResult(containerIntent, requestCode, option)
            true
        } else {
            false
        }
    }

    override fun startService(context: ShadowContext, service: Intent): Pair<Boolean, ComponentName?> {
        if (service.isPluginComponent()) {
            // 插件service intent不需要转换成container service intent，直接使用intent
            val component = mPluginServiceManager!!.startPluginService(service)
            if (component != null) {
                return Pair(true, component)
            }
        }

        return Pair(false, service.component)

    }

    override fun stopService(context: ShadowContext, intent: Intent): Pair<Boolean, Boolean> {
        if (intent.isPluginComponent()) {
            // 插件service intent不需要转换成container service intent，直接使用intent
            val stopped = mPluginServiceManager!!.stopPluginService(intent)
            return Pair(true, stopped)
        }


        return Pair(false, true)
    }

    override fun bindService(context: ShadowContext, intent: Intent, conn: ServiceConnection, flags: Int): Pair<Boolean, Boolean> {
        return if (intent.isPluginComponent()) {
            // 插件service intent不需要转换成container service intent，直接使用intent
            mPluginServiceManager!!.bindPluginService(intent, conn, flags)
            Pair(true, true)
        } else {
            Pair(false, false)
        }


    }

    override fun unbindService(context: ShadowContext, conn: ServiceConnection): Pair<Boolean, Unit> {
        return Pair.create(
            mPluginServiceManager!!.unbindPluginService(conn).first,
            Unit
        )
    }

    override fun convertPluginActivityIntent(pluginIntent: Intent): Intent {
        return if (pluginIntent.isPluginComponent()) {
            pluginIntent.toActivityContainerIntent()
        } else {
            pluginIntent
        }
    }

    /**
     * key:插件Activity类名
     * value:插件PackageName
     */
    private val packageNameMap: MutableMap<String, String> = HashMap()

    /**
     * key:插件ComponentName
     * value:壳子ComponentName
     */
    private val componentMap: MutableMap<ComponentName, ComponentName> = HashMap()

    /**
     * key:插件ComponentName
     * value:PluginInfo
     */
    private val pluginInfoMap: MutableMap<ComponentName, PluginInfo> = hashMapOf()

    /**
     * key:插件ComponentName
     * value:PluginComponentInfo
     */
    private val pluginComponentInfoMap: MutableMap<ComponentName, PluginComponentInfo> = hashMapOf()


    private var application2broadcastInfo: MutableMap<String, MutableMap<String, List<String>>> = HashMap()

    fun addPluginApkInfo(pluginInfo: PluginInfo) {
        fun common(pluginComponentInfo: PluginComponentInfo,componentName:ComponentName) {
            packageNameMap[pluginComponentInfo.className!!] = pluginInfo.packageName
            val previousValue = pluginInfoMap.put(componentName, pluginInfo)
            if (previousValue != null) {
                throw IllegalStateException("重复添加Component：$componentName")
            }
            pluginComponentInfoMap[componentName] = pluginComponentInfo
        }

        pluginInfo.mActivities.forEach {
            val componentName = ComponentName(pluginInfo.packageName, it.className!!)
            common(it,componentName)
            componentMap[componentName] = onBindContainerActivity(componentName)
        }

        pluginInfo.mServices.forEach {
            val componentName = ComponentName(pluginInfo.packageName, it.className!!)
            common(it,componentName)
        }

        pluginInfo.mProviders.forEach {
            val componentName = ComponentName(pluginInfo.packageName, it.className!!)
            mPluginContentProviderManager!!.addContentProviderInfo(pluginInfo.partKey,it,onBindContainerContentProvider(componentName))
        }
    }

    fun getComponentBusinessName(componentName: ComponentName): String? {
        return pluginInfoMap[componentName]?.businessName
    }

    fun getComponentPartKey(componentName: ComponentName) : String? {
        return pluginInfoMap[componentName]?.partKey
    }

    private var mPluginServiceManager : PluginServiceManager? = null
    fun setPluginServiceManager(pluginServiceManager : PluginServiceManager) {
        mPluginServiceManager = pluginServiceManager
    }

    private var mPluginContentProviderManager : PluginContentProviderManager? = null
    fun setPluginContentProviderManager(pluginContentProviderManager : PluginContentProviderManager) {
        mPluginContentProviderManager = pluginContentProviderManager
    }

    private fun Intent.isPluginComponent(): Boolean {
        val component = component ?: return false
        val className = component.className
        return packageNameMap.containsKey(className)
    }

    /**
     * 调用前必须先调用isPluginComponent判断Intent确实一个插件内的组件
     */
    private fun Intent.toActivityContainerIntent(): Intent {
        val bundleForPluginLoader = Bundle()
        val pluginComponentInfo = pluginComponentInfoMap[component]!!
        bundleForPluginLoader.putParcelable(CM_ACTIVITY_INFO_KEY, pluginComponentInfo)
        return toContainerIntent(bundleForPluginLoader)
    }


    /**
     * 构造pluginIntent对应的ContainerIntent
     * 调用前必须先调用isPluginComponent判断Intent确实一个插件内的组件
     */
    private fun Intent.toContainerIntent(bundleForPluginLoader: Bundle): Intent {
        val component = this.component!!
        val className = component.className
        val packageName = packageNameMap[className]!!
        this.component = ComponentName(packageName, className)
        val containerComponent = componentMap[component]!!
        val businessName = pluginInfoMap[component]!!.businessName
        val partKey = pluginInfoMap[component]!!.partKey

        val pluginExtras: Bundle? = extras
        replaceExtras(null as Bundle?)

        val containerIntent = Intent(this)
        containerIntent.component = containerComponent

        bundleForPluginLoader.putString(CM_CLASS_NAME_KEY, className)
        bundleForPluginLoader.putString(CM_PACKAGE_NAME_KEY, packageName)

        containerIntent.putExtra(CM_EXTRAS_BUNDLE_KEY, pluginExtras)
        containerIntent.putExtra(CM_BUSINESS_NAME_KEY, businessName)
        containerIntent.putExtra(CM_PART_KEY, partKey)
        containerIntent.putExtra(CM_LOADER_BUNDLE_KEY, bundleForPluginLoader)
        containerIntent.putExtra(LOADER_VERSION_KEY, BuildConfig.VERSION_NAME)
        containerIntent.putExtra(PROCESS_ID_KEY, DelegateProviderHolder.sCustomPid)
        return containerIntent
    }


    class BroadcastInfo(val className: String, val actions: Array<String>)

    fun getBroadcastsByPartKey(partKey: String): MutableMap<String, List<String>> {
        if (application2broadcastInfo[partKey] == null) {
            application2broadcastInfo[partKey] = HashMap()
            val broadcastInfoList = getBroadcastInfoList(partKey)
            if (broadcastInfoList != null) {
                for (broadcastInfo in broadcastInfoList) {
                    application2broadcastInfo[partKey]!![broadcastInfo.className] =
                            broadcastInfo.actions.toList()
                }
            }
        }
        return application2broadcastInfo[partKey]!!
    }

}