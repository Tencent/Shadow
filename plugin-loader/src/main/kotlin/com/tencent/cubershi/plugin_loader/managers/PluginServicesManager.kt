package com.tencent.cubershi.plugin_loader.managers


import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import com.tencent.cubershi.mock_interface.MockService.PluginServiceOperator
import com.tencent.cubershi.plugin_loader.infos.PluginInfo
import com.tencent.cubershi.plugin_loader.infos.PluginServiceInfo
import com.tencent.cubershi.plugin_loader.managers.PluginServicesManager.Operate.*
import java.nio.charset.IllegalCharsetNameException

/**
 * Created by tracyluo on 2018/6/6.
 */
abstract class PluginServicesManager : PluginServiceOperator {
    companion object {
        val AVOID_CLASS_VERIFY_EXCEPTION = PluginServicesManager::class
        const val KEY_PKG_NAME = "packageName"
        const val KEY_CLASS_NAME = "className"
        const val KEY_OPT_NAME = "ServiceOpt"
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
     * key:intent
     * value:connection
     */
    private val connectionMap: MutableMap<Intent, ServiceConnection> = HashMap()

    /**
     * key:connection
     * value:intent
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

    private fun doServiceOpt(activity: Activity, intent: Intent, opt: Operate): Boolean {
        val className = intent.component.className
        val packageName = packageNameMap[className] ?: return false
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
        activity.startService(containerServiceIntent)
        return true
    }


    override fun startService(activity: Activity, intent: Intent): Boolean {
        return doServiceOpt(activity, intent, START)
    }

    override fun stopService(activity: Activity, name: Intent): Boolean {
        return doServiceOpt(activity, name, STOP)
    }

    override fun bindService(activity: Activity, service: Intent, conn: ServiceConnection, flags: Int): Boolean {
        connectionMap[service] = conn
        return doServiceOpt(activity, service, BIND)
    }

    override fun unbindService(activity: Activity, conn: ServiceConnection): Boolean {
        var intent = intentMap[conn]?:return false
        return doServiceOpt(activity, intent, UNBIND)
    }

    abstract fun onBindContainerService(mockService: ComponentName): ComponentName

    fun getConnection(intent: Intent): ServiceConnection?{
        return connectionMap[intent]
    }

    fun deleteConnection(conn: ServiceConnection?){
        if (conn != null){
            val intent = intentMap[conn]
            if (intent != null) {
                intentMap.remove(conn)
                connectionMap.remove(intent)
            }
        }
    }
}