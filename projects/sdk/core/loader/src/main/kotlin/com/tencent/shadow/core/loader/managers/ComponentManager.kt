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
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.util.Pair
import com.tencent.shadow.coding.java_build_config.BuildConfig
import com.tencent.shadow.core.common.LoggerFactory
import com.tencent.shadow.core.load_parameters.LoadParameters
import com.tencent.shadow.core.loader.infos.ContainerActivityInfo
import com.tencent.shadow.core.loader.infos.ContainerProviderInfo
import com.tencent.shadow.core.runtime.PluginManifest
import com.tencent.shadow.core.runtime.ShadowContext
import com.tencent.shadow.core.runtime.ShadowContext.PluginComponentLauncher
import com.tencent.shadow.core.runtime.container.DelegateProvider.LOADER_VERSION_KEY
import com.tencent.shadow.core.runtime.container.DelegateProvider.PROCESS_ID_KEY
import com.tencent.shadow.core.runtime.container.DelegateProviderHolder
import com.tencent.shadow.core.runtime.container.GeneratedHostActivityDelegator
import com.tencent.shadow.core.runtime.container.PluginContainerService
import com.tencent.shadow.core.runtime.container.PluginContainerServiceDelegateHolder

/**
 * 插件组件管理
 * 主要功能是管理组件和宿主中注册的壳子之间的配对关系
 *
 * @author cubershi
 */
abstract class ComponentManager : PluginComponentLauncher {
    companion object {
        private val mLogger = LoggerFactory.getLogger(ComponentManager::class.java)
        const val CM_LOADER_BUNDLE_KEY = "CM_LOADER_BUNDLE"
        const val CM_EXTRAS_BUNDLE_KEY = "CM_EXTRAS_BUNDLE"
        const val CM_ACTIVITY_INFO_KEY = "CM_ACTIVITY_INFO"
        const val CM_ACTIVITY_LEASE_ID_KEY = "CM_ACTIVITY_LEASE_ID"
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

    /**
     * @param pluginActivity 插件Activity
     * @param pluginActivityInfo 插件Activity在manifest中声明的属性（含launchMode、taskAffinity等）
     * 容器Activity仍需预先注册在宿主manifest中，且launchMode/taskAffinity必须与
     * pluginActivityInfo匹配。系统不支持在运行时修改已注册Activity的这些属性。
     * @return 容器Activity
     */
    open fun onBindContainerActivity(
        pluginActivity: ComponentName,
        pluginActivityInfo: PluginManifest.ActivityInfo
    ): ComponentName = onBindContainerActivity(pluginActivity)

    /**
     * 宿主Manifest中预注册的通用Activity容器池。
     *
     * 不启用时返回空列表，框架保持原有onBindContainerActivity行为。启用后，所有非standard
     * Activity以及声明了非默认taskAffinity的Activity都会由框架分配容器。池中应按
     * launchMode、profile和最大并发独立Task数配置容器，而不是按SDK数量配置。
     * 同一taskAffinity任务组内如有多个非standard Activity，必须准备使用相同
     * 宿主taskAffinity、且launchMode/profile分别匹配的容器组件。
     *
     * 容器在插件Activity首次启动时获得租约。真实Activity因finish而销毁后释放租约；
     * 配置变化或系统回收不会释放。每次重新分配都会生成新租约ID，防止旧Task迟到的
     * onNewIntent被转发给新租户。
     */
    open fun getContainerActivityPool(): List<ContainerActivityInfo> = emptyList()

    /**
     * 返回插件Activity需要的容器Manifest能力Profile。宿主可以根据业务Activity
     * 类型返回自定义值，并在[getContainerActivityPool]中提供相同profile的容器。
     */
    open fun getContainerActivityProfile(
        pluginActivity: ComponentName,
        pluginActivityInfo: PluginManifest.ActivityInfo
    ): String = ContainerActivityInfo.DEFAULT_PROFILE

    abstract fun onBindContainerContentProvider(pluginContentProvider: ComponentName): ContainerProviderInfo

    open fun onBindContainerContentProvider(
        pluginContentProvider: ComponentName,
        pluginAuthority: String
    ): ContainerProviderInfo {
        return onBindContainerContentProvider(pluginContentProvider)
    }

    /**
     * @param pluginService 插件Service
     * @return 固定绑定的容器Service。多个插件Service可以返回同一个容器，框架会在
     * 容器内分别管理它们的生命周期。仅在某个插件Service需要路由到特定进程或特殊
     * foregroundServiceType容器时才覆写此方法。
     */
    open fun onBindContainerService(pluginService: ComponentName): ComponentName? = null

    /**
     * 宿主Manifest中预注册的通用Service容器。默认使用列表中的第一个容器承载所有
     * 未通过onBindContainerService指定容器的插件Service，不要求按并发数预留坑位。
     * 容器应声明宿主允许的foregroundServiceType并集。保留List返回值以兼容已有宿主；
     * 额外容器可供onBindContainerService按进程或Service类型显式选择。
     */
    open fun getContainerServicePool(): List<ComponentName> = emptyList()

    override fun startActivity(
        shadowContext: ShadowContext,
        pluginIntent: Intent,
        option: Bundle?
    ): Boolean {
        return if (pluginIntent.isPluginComponent()) {
            shadowContext.superStartActivity(pluginIntent.toActivityContainerIntent(), option)
            true
        } else {
            false
        }
    }


    override fun startActivityForResult(
        delegator: GeneratedHostActivityDelegator,
        pluginIntent: Intent,
        requestCode: Int,
        option: Bundle?,
        callingActivity: ComponentName
    ): Boolean {
        return if (pluginIntent.isPluginComponent()) {
            val containerIntent = pluginIntent.toActivityContainerIntent()
            containerIntent.putExtra(CM_CALLING_ACTIVITY_KEY, callingActivity)
            delegator.startActivityForResult(containerIntent, requestCode, option)
            true
        } else {
            false
        }
    }

    override fun startService(
        context: ShadowContext,
        service: Intent
    ): Pair<Boolean, ComponentName?> {
        if (service.isPluginComponent()) {
            val pluginComponent = service.component!!
            val declaredForegroundService =
                pluginServiceInfoMap[pluginComponent]?.foregroundServiceType != 0
            val container = acquireContainerService(pluginComponent, declaredForegroundService)
            if (container != null) {
                startContainerService(context, service, container, false)
                return Pair(true, service.component)
            }
            val component = mPluginServiceManager!!.startPluginService(service)
            if (component != null) {
                return Pair(true, component)
            }
        }

        return Pair(false, service.component)

    }

    override fun startForegroundService(
        context: ShadowContext,
        service: Intent
    ): Pair<Boolean, ComponentName?> {
        if (service.isPluginComponent()) {
            val pluginComponent = service.component!!
            val containerComponent = acquireContainerService(pluginComponent, true)
            if (containerComponent != null) {
                startContainerService(context, service, containerComponent, true)
                return Pair(true, pluginComponent)
            }
        }
        return Pair(false, service.component)
    }

    private fun startContainerService(
        context: ShadowContext,
        pluginIntent: Intent,
        containerComponent: ComponentName,
        foreground: Boolean
    ) {
        val pluginComponent = pluginIntent.component!!
        // stop之后的新start请求应取消尚未执行的pending stop。
        cancelContainerServiceStopPending(pluginComponent)
        val packageName = packageNameMap[pluginComponent.className]
            ?: throw IllegalArgumentException("Unknown plugin Service: $pluginComponent")
        val pluginExtras = pluginIntent.extras
        val containerIntent = Intent(pluginIntent)
        containerIntent.component = containerComponent
        containerIntent.replaceExtras(null as Bundle?)
        containerIntent.putExtra(
            PluginContainerService.EXTRA_PLUGIN_SERVICE_CLASS,
            pluginComponent.className
        )
        containerIntent.putExtra(PluginContainerService.EXTRA_PLUGIN_PACKAGE_NAME, packageName)
        containerIntent.putExtra(PluginContainerService.EXTRA_ORIGINAL_EXTRAS, pluginExtras)
        containerIntent.putExtra(
            PluginContainerService.EXTRA_DELEGATE_PROVIDER_KEY,
            mPluginLoaderDelegateProviderKey
        )
        containerIntent.putExtra(
            PluginContainerService.EXTRA_PROCESS_ID,
            PluginContainerServiceDelegateHolder.PROCESS_ID
        )

        try {
            if (foreground && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.baseContext.startForegroundService(containerIntent)
            } else {
                context.baseContext.startService(containerIntent)
            }
        } catch (throwable: Throwable) {
            releaseContainerService(pluginComponent, containerComponent)
            throw throwable
        }
    }

    override fun stopService(context: ShadowContext, intent: Intent): Pair<Boolean, Boolean> {
        if (intent.isPluginComponent()) {
            val pluginComponent = intent.component!!
            val serviceManager = mPluginServiceManager!!
            val stopped = serviceManager.stopPluginService(intent)
            if (!stopped && !serviceManager.hasPluginService(pluginComponent)) {
                // 容器启动已提交给AMS，但onStartCommand可能尚未回调到Loader。
                // startForegroundService不能在完成前台提升前直接停止，否则系统可能抛超时异常。
                // 记录pending stop，由容器在插件onStartCommand返回后完成停止。
                val pendingContainer = getAllocatedContainerService(pluginComponent)
                if (pendingContainer != null) {
                    markContainerServiceStopPending(pluginComponent)
                    return Pair(true, true)
                }
            }
            return Pair(true, stopped)
        }


        return Pair(false, true)
    }

    override fun bindService(
        context: ShadowContext,
        intent: Intent,
        conn: ServiceConnection,
        flags: Int
    ): Pair<Boolean, Boolean> {
        return if (intent.isPluginComponent()) {
            // 插件service intent不需要转换成container service intent，直接使用intent
            mPluginServiceManager!!.bindPluginService(intent, conn, flags)
            Pair(true, true)
        } else {
            Pair(false, false)
        }


    }

    override fun unbindService(
        context: ShadowContext,
        conn: ServiceConnection
    ): Pair<Boolean, Unit> {
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
     * value:LoadParameters
     */
    private val loadParametersMap: MutableMap<ComponentName, LoadParameters> = hashMapOf()

    /**
     * key:插件ComponentName
     * value:PluginManifest.ActivityInfo
     */
    private val pluginActivityInfoMap: MutableMap<ComponentName, PluginManifest.ActivityInfo> =
        hashMapOf()

    private val pluginToActivityContainerMap: MutableMap<ComponentName, ContainerActivityInfo> =
        hashMapOf()

    private val activityContainerToPluginMap: MutableMap<ComponentName, ComponentName> =
        hashMapOf()

    private val pluginToActivityLeaseIdMap: MutableMap<ComponentName, Long> = hashMapOf()

    private val pluginToActivityAffinityMap: MutableMap<ComponentName, String> = hashMapOf()

    private var nextActivityLeaseId: Long = 1L

    private val pluginAffinityToContainerAffinityMap: MutableMap<String, String> = hashMapOf()

    private val containerAffinityToPluginAffinityMap: MutableMap<String, String> = hashMapOf()

    private val pluginServiceInfoMap: MutableMap<ComponentName, PluginManifest.ServiceInfo> =
        hashMapOf()

    private val pluginToServiceContainerMap: MutableMap<ComponentName, ComponentName> =
        hashMapOf()

    private val pendingContainerServiceStops: MutableSet<ComponentName> = hashSetOf()

    private var mPluginLoaderDelegateProviderKey: String = DelegateProviderHolder.DEFAULT_KEY

    /**
     * 保存所有已加载插件对PluginManifest和apk文件路径对应关系
     * 用于在同一个Loader加载对多个插件之间相互查找组件
     */
    private val allLoadedPlugin: MutableList<kotlin.Pair<PluginManifest, String>> = mutableListOf()

    fun addPluginApkInfo(
        pluginManifest: PluginManifest,
        loadParameters: LoadParameters,
        archiveFilePath: String
    ) {
        fun common(componentInfo: PluginManifest.ComponentInfo, componentName: ComponentName) {
            packageNameMap[componentInfo.className] = componentName.packageName
            val previousValue = loadParametersMap.put(componentName, loadParameters)
            if (previousValue != null) {
                throw IllegalStateException("重复添加Component：$componentName")
            }
        }

        val applicationPackageName = pluginManifest.applicationPackageName
        pluginManifest.activities?.forEach {
            val componentName = ComponentName(applicationPackageName, it.className)
            common(it, componentName)
            componentMap[componentName] = onBindContainerActivity(componentName, it)
            pluginActivityInfoMap[componentName] = it
        }

        pluginManifest.services?.forEach {
            val componentName = ComponentName(applicationPackageName, it.className)
            common(it, componentName)
            pluginServiceInfoMap[componentName] = it
        }

        pluginManifest.providers?.forEach {
            val componentName = ComponentName(applicationPackageName, it.className)
            it.authorities.split(";")
                .filter { authority -> authority.isNotBlank() }
                .forEach { authority ->
                    mPluginContentProviderManager!!.addContentProviderInfo(
                        loadParameters.partKey,
                        it,
                        onBindContainerContentProvider(componentName, authority),
                        authority
                    )
                }
        }

        pluginManifest.receivers?.forEach {
            val componentName = ComponentName(applicationPackageName, it.className)
            common(it, componentName)
        }

        allLoadedPlugin.add(pluginManifest to archiveFilePath)
    }

    fun getComponentBusinessName(componentName: ComponentName): String? {
        return loadParametersMap[componentName]?.businessName
    }

    fun getComponentPartKey(componentName: ComponentName): String? {
        return loadParametersMap[componentName]?.partKey
    }

    @Synchronized
    internal fun acquireContainerActivity(
        pluginActivity: ComponentName,
        pluginActivityInfo: PluginManifest.ActivityInfo
    ): ComponentName? {
        val pool = getContainerActivityPool()
        if (pool.isEmpty()) {
            return null
        }
        validateContainerActivityPool(pool)

        val pluginAffinity = normalizeTaskAffinity(
            pluginActivity,
            pluginActivityInfo.taskAffinity
        )
        val requiredProfile = getContainerActivityProfile(pluginActivity, pluginActivityInfo)
        if (requiredProfile.isEmpty()) {
            throw IllegalStateException("Activity container profile must not be empty: $pluginActivity")
        }
        val usesDefaultTaskSemantics =
            pluginActivityInfo.launchMode == ActivityInfo.LAUNCH_MULTIPLE &&
                    pluginAffinity == pluginActivity.packageName
        if (usesDefaultTaskSemantics) {
            return null
        }

        pluginToActivityContainerMap[pluginActivity]?.let { existing ->
            if (existing.launchMode != pluginActivityInfo.launchMode) {
                throw IllegalStateException(
                    "Plugin Activity launchMode changed after allocation: $pluginActivity"
                )
            }
            if (existing.profile != requiredProfile) {
                throw IllegalStateException(
                    "Plugin Activity container profile changed after allocation: $pluginActivity"
                )
            }
            return existing.componentName
        }

        val assignedContainerAffinity =
            pluginAffinityToContainerAffinityMap[pluginAffinity]
        val container = if (assignedContainerAffinity != null) {
            pool.firstOrNull {
                it.launchMode == pluginActivityInfo.launchMode &&
                        it.profile == requiredProfile &&
                        it.taskAffinity == assignedContainerAffinity &&
                        !activityContainerToPluginMap.containsKey(it.componentName)
            } ?: throw IllegalStateException(
                "No idle host Activity container in taskAffinity group " +
                        "$assignedContainerAffinity for $pluginActivity " +
                        "(launchMode=${pluginActivityInfo.launchMode}, profile=$requiredProfile). " +
                        "Increase that group capacity."
            )
        } else {
            pool.firstOrNull {
                it.launchMode == pluginActivityInfo.launchMode &&
                        it.profile == requiredProfile &&
                        !activityContainerToPluginMap.containsKey(it.componentName) &&
                        !containerAffinityToPluginAffinityMap.containsKey(it.taskAffinity)
            } ?: throw IllegalStateException(
                "No idle host Activity container group for $pluginActivity " +
                        "(launchMode=${pluginActivityInfo.launchMode}, " +
                        "profile=$requiredProfile, taskAffinity=$pluginAffinity). " +
                        "Increase getContainerActivityPool capacity."
            )
        }

        pluginToActivityContainerMap[pluginActivity] = container
        activityContainerToPluginMap[container.componentName] = pluginActivity
        pluginToActivityLeaseIdMap[pluginActivity] = newActivityLeaseId()
        pluginToActivityAffinityMap[pluginActivity] = pluginAffinity
        pluginAffinityToContainerAffinityMap[pluginAffinity] = container.taskAffinity
        containerAffinityToPluginAffinityMap[container.taskAffinity] = pluginAffinity
        mLogger.info(
            "Allocate Activity container lease: plugin={}, container={}, leaseId={}, profile={}",
            pluginActivity,
            container.componentName,
            pluginToActivityLeaseIdMap[pluginActivity],
            requiredProfile
        )
        return container.componentName
    }

    private fun newActivityLeaseId(): Long {
        val result = nextActivityLeaseId
        nextActivityLeaseId = if (result == Long.MAX_VALUE) 1L else result + 1L
        return result
    }

    @Synchronized
    internal fun getContainerActivityLeaseId(pluginActivity: ComponentName): Long =
        pluginToActivityLeaseIdMap[pluginActivity] ?: 0L

    @Synchronized
    internal fun releaseContainerActivity(
        pluginActivity: ComponentName,
        containerActivity: ComponentName,
        leaseId: Long
    ): Boolean {
        if (leaseId == 0L || pluginToActivityLeaseIdMap[pluginActivity] != leaseId) {
            return false
        }
        val container = pluginToActivityContainerMap[pluginActivity] ?: return false
        if (container.componentName != containerActivity ||
            activityContainerToPluginMap[containerActivity] != pluginActivity
        ) {
            return false
        }

        val pluginAffinity = pluginToActivityAffinityMap.remove(pluginActivity)
        pluginToActivityContainerMap.remove(pluginActivity)
        activityContainerToPluginMap.remove(containerActivity)
        pluginToActivityLeaseIdMap.remove(pluginActivity)

        if (pluginAffinity != null &&
            pluginToActivityAffinityMap.values.none { it == pluginAffinity }
        ) {
            val containerAffinity = pluginAffinityToContainerAffinityMap.remove(pluginAffinity)
            if (containerAffinity != null &&
                pluginAffinityToContainerAffinityMap.values.none { it == containerAffinity }
            ) {
                containerAffinityToPluginAffinityMap.remove(containerAffinity)
            }
        }
        mLogger.info(
            "Release Activity container lease: plugin={}, container={}, leaseId={}",
            pluginActivity,
            containerActivity,
            leaseId
        )
        return true
    }

    private fun validateContainerActivityPool(pool: List<ContainerActivityInfo>) {
        if (pool.map { it.componentName }.distinct().size != pool.size) {
            throw IllegalStateException(
                "getContainerActivityPool contains duplicate container components"
            )
        }
        pool.forEach {
            if (it.taskAffinity.isEmpty()) {
                throw IllegalStateException(
                    "Activity container ${it.componentName} must declare a non-empty taskAffinity"
                )
            }
            if (it.profile.isEmpty()) {
                throw IllegalStateException(
                    "Activity container ${it.componentName} must declare a non-empty profile"
                )
            }
            if (it.launchMode < ActivityInfo.LAUNCH_MULTIPLE || it.launchMode > 4) {
                throw IllegalStateException(
                    "Activity container ${it.componentName} has invalid launchMode=${it.launchMode}"
                )
            }
        }
    }

    private fun normalizeTaskAffinity(
        pluginActivity: ComponentName,
        taskAffinity: String?
    ): String = when {
        taskAffinity == null -> pluginActivity.packageName
        taskAffinity.isEmpty() -> "<no-affinity>:${pluginActivity.flattenToString()}"
        taskAffinity.startsWith(":") -> pluginActivity.packageName + taskAffinity
        else -> taskAffinity
    }

    fun getServiceInfo(componentName: ComponentName): PluginManifest.ServiceInfo? =
        pluginServiceInfoMap[componentName]

    @Synchronized
    private fun acquireContainerService(
        pluginService: ComponentName,
        allowPool: Boolean
    ): ComponentName? {
        pluginToServiceContainerMap[pluginService]?.let { return it }

        val fixedContainer = onBindContainerService(pluginService)
        val container = fixedContainer ?: if (allowPool) {
            val pool = getContainerServicePool()
            if (pool.size != pool.distinct().size) {
                throw IllegalStateException("getContainerServicePool contains duplicate components")
            }
            pool.firstOrNull()
        } else {
            null
        }

        if (container != null) {
            pluginToServiceContainerMap[pluginService] = container
        }
        return container
    }

    @Synchronized
    private fun getAllocatedContainerService(pluginService: ComponentName): ComponentName? =
        pluginToServiceContainerMap[pluginService]

    @Synchronized
    private fun markContainerServiceStopPending(pluginService: ComponentName) {
        pendingContainerServiceStops.add(pluginService)
    }

    @Synchronized
    internal fun consumeContainerServiceStopPending(pluginService: ComponentName): Boolean =
        pendingContainerServiceStops.remove(pluginService)

    @Synchronized
    private fun cancelContainerServiceStopPending(pluginService: ComponentName) {
        pendingContainerServiceStops.remove(pluginService)
    }

    @Synchronized
    internal fun releaseContainerService(
        pluginService: ComponentName,
        containerService: ComponentName
    ) {
        if (pluginToServiceContainerMap[pluginService] == containerService) {
            pluginToServiceContainerMap.remove(pluginService)
        }
        pendingContainerServiceStops.remove(pluginService)
    }

    @Synchronized
    internal fun hasAllocatedContainerServices(containerService: ComponentName): Boolean =
        pluginToServiceContainerMap.values.any { it == containerService }

    @Synchronized
    internal fun releaseAllContainerServices(containerService: ComponentName): Set<ComponentName> {
        val plugins = pluginToServiceContainerMap
            .filterValues { it == containerService }
            .keys
            .toSet()
        plugins.forEach {
            pluginToServiceContainerMap.remove(it)
            pendingContainerServiceStops.remove(it)
        }
        return plugins
    }

    fun setPluginLoaderDelegateProviderKey(key: String) {
        mPluginLoaderDelegateProviderKey = key
    }

    private var mPluginServiceManager: PluginServiceManager? = null
    fun setPluginServiceManager(pluginServiceManager: PluginServiceManager) {
        mPluginServiceManager = pluginServiceManager
    }

    private var mPluginContentProviderManager: PluginContentProviderManager? = null
    fun setPluginContentProviderManager(pluginContentProviderManager: PluginContentProviderManager) {
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
        val pluginActivityInfo = pluginActivityInfoMap[component]!!
        bundleForPluginLoader.putParcelable(CM_ACTIVITY_INFO_KEY, pluginActivityInfo)
        return toContainerIntent(bundleForPluginLoader)
    }


    /**
     * 构造pluginIntent对应的ContainerIntent
     * 调用前必须先调用isPluginComponent判断Intent确实一个插件内的组件
     */
    private fun Intent.toContainerIntent(bundleForPluginLoader: Bundle): Intent {
        val component = this.component
            ?: throw IllegalArgumentException("Activity Intent必须指定ComponentName")
        val className = component.className

        val packageName = packageNameMap[className]
            ?: throw IllegalArgumentException("已加载的插件中找不到${className}对应的packageName")
        this.component = ComponentName(packageName, className)

        val loadParameters = loadParametersMap[component]
            ?: throw IllegalArgumentException("已加载的插件中找不到${component}对应的LoadParameters")
        val businessName = loadParameters.businessName
        val partKey = loadParameters.partKey

        val pluginExtras: Bundle? = extras
        replaceExtras(null as Bundle?)

        val pluginActivityInfo = pluginActivityInfoMap[component]
            ?: throw IllegalArgumentException("已加载的插件中找不到${component}对应的ActivityInfo")
        val containerComponent = acquireContainerActivity(component, pluginActivityInfo)
            ?: componentMap[component]
            ?: throw IllegalArgumentException("已加载的插件中找不到${component}对应的ContainerActivity")
        val containerIntent = Intent(this)
        containerIntent.component = containerComponent

        bundleForPluginLoader.putString(CM_CLASS_NAME_KEY, className)
        bundleForPluginLoader.putString(CM_PACKAGE_NAME_KEY, packageName)
        getContainerActivityLeaseId(component).takeIf { it != 0L }?.let {
            bundleForPluginLoader.putLong(CM_ACTIVITY_LEASE_ID_KEY, it)
        }

        containerIntent.putExtra(CM_EXTRAS_BUNDLE_KEY, pluginExtras)
        containerIntent.putExtra(CM_BUSINESS_NAME_KEY, businessName)
        containerIntent.putExtra(CM_PART_KEY, partKey)
        containerIntent.putExtra(CM_LOADER_BUNDLE_KEY, bundleForPluginLoader)
        containerIntent.putExtra(LOADER_VERSION_KEY, BuildConfig.VERSION_NAME)
        containerIntent.putExtra(PROCESS_ID_KEY, DelegateProviderHolder.sCustomPid)
        return containerIntent
    }

    fun getArchiveFilePathForActivity(className: String) =
        getArchiveFilePath(className, PluginManifest::getActivities)

    fun getArchiveFilePathForService(className: String) =
        getArchiveFilePath(className, PluginManifest::getServices)

    fun getArchiveFilePathForProviderByAction(action: String?): kotlin.Pair<String?, String?> {
        for ((pluginManifest, archiveFilePath) in allLoadedPlugin) {
            val providers = pluginManifest.providers
            if (providers != null) {
                for (provider in providers) {
                    if (action?.equals(provider.authorities) == true) {
                        return provider.className to archiveFilePath
                    }
                }
            }
        }
        return null to null
    }

    fun getArchiveFilePathForProviderByClassName(className: String): kotlin.Pair<String?, String?> {
        for ((pluginManifest, archiveFilePath) in allLoadedPlugin) {
            val providers = pluginManifest.providers
            if (providers != null) {
                for (provider in providers) {
                    if (className == provider.className) {
                        return provider.className to archiveFilePath
                    }
                }
            }
        }
        return null to null
    }

    fun getAllArchiveFilePaths() = allLoadedPlugin.map { it.second }.toList()

    private fun getArchiveFilePath(
        className: String,
        getComponents: (PluginManifest) -> Array<out PluginManifest.ComponentInfo>?
    ): String? {
        for ((pluginManifest, archiveFilePath) in allLoadedPlugin) {
            val components = getComponents(pluginManifest)
            if (components != null) {
                for (component in components) {
                    if (component.className == className) {
                        return archiveFilePath
                    }
                }
            }
        }
        return null
    }

}
