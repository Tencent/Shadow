package com.tencent.cubershi.plugin_loader.delegates

import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.IBinder
import com.tencent.cubershi.mock_interface.MockApplication
import com.tencent.cubershi.mock_interface.MockService
import com.tencent.hydevteam.pluginframework.plugincontainer.HostServiceDelegate
import com.tencent.hydevteam.pluginframework.plugincontainer.HostServiceDelegator
import dalvik.system.DexClassLoader

/**
 * Created by tracyluo on 2018/6/5.
 */
class HostServiceDelegateImpl(private val mPluginApplication: MockApplication,
                              private val mPluginClassLoader: DexClassLoader,
                              private val mPluginResources: Resources,
                              private val mPluginService: MockService) : HostServiceDelegate {
    private lateinit var mHostServiceDelegator: HostServiceDelegator
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return mPluginService.onStartCommand(intent, flags, startId)
    }

    override fun onUnbind(intent: Intent?): Boolean {
       return mPluginService.onUnbind(intent)
    }

    override fun onLowMemory() {
        return mPluginService.onLowMemory();
    }

    override fun onConfigurationChanged(configuration: Configuration?) {
        return mPluginService.onConfigurationChanged(configuration);
    }

    override fun setDelegator(hostServiceDelegator: HostServiceDelegator) {
        mHostServiceDelegator = hostServiceDelegator
    }

    override fun onBind(intent: Intent?): IBinder {
        return mPluginService.onBind(intent)
    }

    override fun onTrimMemory(level: Int) {
        return mPluginService.onTrimMemory(level)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        return mPluginService.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        return mPluginService.onDestroy()
    }

}