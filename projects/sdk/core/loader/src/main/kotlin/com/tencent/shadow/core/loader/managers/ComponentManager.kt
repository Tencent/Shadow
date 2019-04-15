package com.tencent.shadow.core.loader.managers

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.util.Pair
import com.tencent.shadow.core.loader.BuildConfig
import com.tencent.shadow.core.loader.PluginServiceManager
import com.tencent.shadow.core.loader.delegates.ServiceContainerReuseDelegate
import com.tencent.shadow.core.loader.delegates.ServiceContainerReuseDelegate.Companion.Operate
import com.tencent.shadow.core.loader.infos.ContainerProviderInfo
import com.tencent.shadow.core.loader.infos.PluginComponentInfo
import com.tencent.shadow.core.loader.infos.PluginInfo
import com.tencent.shadow.runtime.ShadowContext
import com.tencent.shadow.runtime.ShadowContext.PluginComponentLauncher
import com.tencent.shadow.runtime.container.DelegateProvider.LOADER_VERSION_KEY
import com.tencent.shadow.runtime.container.DelegateProvider.PROCESS_ID_KEY
import com.tencent.shadow.runtime.container.DelegateProviderHolder
import com.tencent.shadow.runtime.container.HostActivityDelegator

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
        const val CM_PACKAGE_NAME_KEY = "CM_PACKAGE_NAME"
        const val CM_INTENT_KEY = "CM_INTENT"
        const val CM_PART_KEY = "CM_PART"
    }

    abstract fun getLauncherActivity(partKey: String): ComponentName

    /**
     * @param pluginActivity 插件Activity
     * @return 容器Activity
     */
    abstract fun onBindContainerActivity(pluginActivity: ComponentName): ComponentName

    abstract fun onBindContainerService(shadowService: ComponentName): ComponentName

    abstract fun onBindContainerContentProvider(pluginContentProvider: ComponentName): ContainerProviderInfo

    abstract fun getBroadcastInfoList(partKey: String): List<BroadcastInfo>?

    override fun startActivity(shadowContext: ShadowContext, pluginIntent: Intent): Boolean {
        return if (pluginIntent.isPluginComponent()) {
            shadowContext.superStartActivity(pluginIntent.toActivityContainerIntent())
            true
        } else {
            false
        }
    }

    override fun startActivityForResult(delegator: HostActivityDelegator, pluginIntent: Intent, requestCode: Int): Boolean {
        return if (pluginIntent.isPluginComponent()) {
            delegator.startActivityForResult(pluginIntent.toActivityContainerIntent(), requestCode)
            true
        } else {
            false
        }
    }

    override fun startActivityForResult(delegator: HostActivityDelegator, pluginIntent: Intent, requestCode: Int, option: Bundle?): Boolean {
        return if (pluginIntent.isPluginComponent()) {
            delegator.startActivityForResult(pluginIntent.toActivityContainerIntent(), requestCode, option)
            true
        } else {
            false
        }
    }

    private fun doServiceOpt(context: ShadowContext, intent: Intent?): Boolean {
        intent ?: return false
        context.baseContext.startService(intent)
        return true
    }

    override fun startService(context: ShadowContext, service: Intent): Pair<Boolean, ComponentName> {
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
        mPluginServiceManager!!.unbindPluginService(conn)
        return Pair(true, Unit)
    }

    override fun convertPluginActivityIntent(pluginIntent: Intent): Intent {
        return if (pluginIntent.isPluginComponent()) {
            pluginIntent.toActivityContainerIntent()
        } else {
            pluginIntent
        }
    }

    override fun convertPluginServiceIntent(pluginIntent: Intent): Intent {
        return if (pluginIntent.isPluginComponent()) {
            pluginIntent.toServiceContainerIntent(Operate.START)
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

    /**
     * key:long
     * value:connection
     */
    private val containerIntentKeyToConnectionMap: MutableMap<Long, ServiceConnection> = HashMap()

    /**
     * key:connection
     * value:Intent
     */
    private val connectionToContainerIntentMap: MutableMap<ServiceConnection, Intent> = HashMap()

    private var application2broadcastInfo: MutableMap<String, MutableMap<String, List<String>>> = HashMap()

    fun addPluginApkInfo(pluginInfo: PluginInfo) {
        fun common(pluginComponentInfo: PluginComponentInfo,
                   bind: (name: ComponentName) -> ComponentName
        ) {
            val componentName = ComponentName(pluginInfo.packageName, pluginComponentInfo.className)
            packageNameMap[pluginComponentInfo.className] = pluginInfo.packageName
            val previousValue = pluginInfoMap.put(componentName, pluginInfo)
            if (previousValue != null) {
                throw IllegalStateException("重复添加Component：$componentName")
            }
            pluginComponentInfoMap[componentName] = pluginComponentInfo
            componentMap[componentName] = bind(componentName)
        }

        pluginInfo.mActivities.forEach {
            common(it, ::onBindContainerActivity)
        }

        pluginInfo.mServices.forEach {
            common(it, ::onBindContainerService)
        }

        pluginInfo.mProviders.forEach {
            val componentName = ComponentName(pluginInfo.packageName, it.className)
            mPluginContentProviderManager!!.addContentProviderInfo(pluginInfo.partKey,it,onBindContainerContentProvider(componentName))
        }
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
        if (component == null) {
            return false
        }
        val className = component.className ?: return false
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
     * 调用前必须先调用isPluginComponent判断Intent确实一个插件内的组件
     */
    private fun Intent.toServiceContainerIntent(opt: Operate): Intent {
        val bundleForPluginLoader = Bundle()
        bundleForPluginLoader.putSerializable(ServiceContainerReuseDelegate.OPT_EXTRA_KEY, opt)
        return toContainerIntent(bundleForPluginLoader)
    }


    /**
     * 构造pluginIntent对应的ContainerIntent
     * 调用前必须先调用isPluginComponent判断Intent确实一个插件内的组件
     */
    private fun Intent.toContainerIntent(bundleForPluginLoader: Bundle): Intent {
        val className = component.className!!
        val packageName = packageNameMap[className]!!
        component = ComponentName(packageName, className)
        val containerComponent = componentMap[component]!!
        val partKey = pluginInfoMap[component]!!.partKey

        val pluginExtras: Bundle? = extras
        replaceExtras(null as Bundle?)

        val containerIntent = Intent(this)
        containerIntent.component = containerComponent

        bundleForPluginLoader.putString(CM_CLASS_NAME_KEY, className)
        bundleForPluginLoader.putString(CM_PACKAGE_NAME_KEY, packageName)

        containerIntent.putExtra(CM_EXTRAS_BUNDLE_KEY, pluginExtras)
        containerIntent.putExtra(CM_PART_KEY, partKey)
        containerIntent.putExtra(CM_LOADER_BUNDLE_KEY, bundleForPluginLoader)
        containerIntent.putExtra(LOADER_VERSION_KEY, BuildConfig.VERSION_NAME)
        containerIntent.putExtra(PROCESS_ID_KEY,DelegateProviderHolder.sCustomPid)
        return containerIntent
    }

    fun getConnection(intentKey: Long): ServiceConnection? {
        return containerIntentKeyToConnectionMap[intentKey]
    }

    fun deleteConnection(conn: ServiceConnection?) {
        if (conn != null) {
            val intent = connectionToContainerIntentMap[conn]
            if (intent != null) {
                connectionToContainerIntentMap.remove(conn)
                val intentKey = intent.getBundleExtra(CM_LOADER_BUNDLE_KEY).getLong(CM_INTENT_KEY, -1)
                containerIntentKeyToConnectionMap.remove(intentKey)
            }
        }
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