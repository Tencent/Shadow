package com.tencent.cubershi.plugin_loader.managers


import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.util.Pair
import com.tencent.cubershi.mock_interface.MockContext
import com.tencent.cubershi.plugin_loader.infos.PluginInfo
import com.tencent.cubershi.plugin_loader.infos.PluginServiceInfo
import com.tencent.cubershi.plugin_loader.managers.PluginServicesManager.Operate.*

/**
 * Created by tracyluo on 2018/6/6.
 */
abstract class PluginServicesManager : MockContext.PluginServiceOperator {
    companion object {
        val AVOID_CLASS_VERIFY_EXCEPTION = PluginServicesManager::class
        const val KEY_PKG_NAME = "packageName"
        const val KEY_CLASS_NAME = "className"
        const val KEY_OPT_NAME = "ServiceOpt"
        const val KEY_INTENT_KEY = "intentKey"
    }

    enum class Operate {
        START, STOP, BIND, UNBIND
    }

    /**
     * key:插件ComponentName
     * value:壳子ComponentName
     */
    private val servicesMap: MutableMap<ComponentName, ComponentName> = HashMap()

    /**
     * key:插件Service类名
     * value:插件PackageName
     */
    private val packageNameMap: MutableMap<String, String> = HashMap()

    /**
     * key:插件ComponentName
     * value:PluginServiceInfo
     */
    private val serviceInfoMap: MutableMap<ComponentName, PluginServiceInfo> = HashMap()

    /**
     * key:long
     * value:connection
     */
    private val connectionMap: MutableMap<Long, ServiceConnection> = HashMap()

    /**
     * key:connection
     * value:Intent
     */
    private val intentMap: MutableMap<ServiceConnection, Intent> = HashMap()

    fun addPluginApkInfo(pluginInfo: PluginInfo) {
        pluginInfo.mServices.forEach {
            val componentName = ComponentName(pluginInfo.packageName, it.className)
            servicesMap[componentName] = onBindContainerService(componentName)
            packageNameMap[it.className] = pluginInfo.packageName
            serviceInfoMap[componentName] = it
        }
    }

    private fun getContainerService(pluginActivity: ComponentName): ComponentName =
            servicesMap[pluginActivity]!!

    private fun doServiceOpt(context: MockContext, intent: Intent?): Boolean {
        intent ?: return false
        context.baseContext.startService(intent)
        return true
    }

    public fun getContainerServiceIntent(intent: Intent, opt: Operate): Intent? {
        val className = intent.component.className
        val packageName = packageNameMap[className] ?: return null
        intent.component = ComponentName(packageName, className)
        val containerService = getContainerService(intent.component)
        val containerServiceIntent = Intent(intent)
        containerServiceIntent.component = containerService
        containerServiceIntent.putExtra(KEY_PKG_NAME, packageName)
        containerServiceIntent.putExtra(KEY_CLASS_NAME, className)
        when (opt) {
            START -> containerServiceIntent.putExtra(KEY_OPT_NAME, "start")
            STOP -> containerServiceIntent.putExtra(KEY_OPT_NAME, "stop")
            BIND -> containerServiceIntent.putExtra(KEY_OPT_NAME, "bind")
            UNBIND -> containerServiceIntent.putExtra(KEY_OPT_NAME, "unbind")
        }
        return containerServiceIntent
    }

    override fun startService(context: MockContext, intent: Intent): Pair<Boolean, ComponentName> {
        return Pair(doServiceOpt(context, getContainerServiceIntent(intent, START)), intent.component)
    }

    override fun stopService(context: MockContext, name: Intent): Pair<Boolean, Boolean> {
        return Pair(doServiceOpt(context, getContainerServiceIntent(name, STOP)), true)
    }

    override fun bindService(context: MockContext, service: Intent, conn: ServiceConnection, flags: Int): Pair<Boolean, Boolean> {
        var intent = getContainerServiceIntent(service, BIND)?:return Pair(false, false)
        var time = System.nanoTime()
        connectionMap[time] = conn
        intentMap[conn] = intent
        intent.putExtra(KEY_INTENT_KEY, time)
        return Pair(doServiceOpt(context, intent), true)
    }

    override fun unbindService(context: MockContext, conn: ServiceConnection): Pair<Boolean, Unit> {
        var intent = intentMap[conn] ?: return Pair(false, Unit)
        var unBindIntent = intent.putExtra(KEY_OPT_NAME, "unbind")
        return Pair(doServiceOpt(context, unBindIntent), Unit)
    }

    abstract fun onBindContainerService(mockService: ComponentName): ComponentName

    fun getConnection(intent: Intent): ServiceConnection? {
        return connectionMap[ intent.getLongExtra(KEY_INTENT_KEY, -1)]
    }

    fun deleteConnection(conn: ServiceConnection?) {
        if (conn != null) {
            val intent = intentMap[conn]
            if (intent != null) {
                intentMap.remove(conn)
                connectionMap.remove( intent.getLongExtra(KEY_INTENT_KEY, -1))
            }
        }
    }
}