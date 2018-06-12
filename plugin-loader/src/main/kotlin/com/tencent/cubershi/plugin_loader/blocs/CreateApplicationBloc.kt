package com.tencent.cubershi.plugin_loader.blocs

import android.content.Context
import android.content.res.Resources
import android.os.Handler
import android.os.Looper

import com.tencent.cubershi.mock_interface.MockApplication
import com.tencent.cubershi.plugin_loader.PluginPackageManager
import com.tencent.cubershi.plugin_loader.exceptions.CreateApplicationException
import com.tencent.cubershi.plugin_loader.managers.PluginActivitiesManager
import com.tencent.cubershi.plugin_loader.managers.PluginServicesManager
import java.util.concurrent.CountDownLatch

/**
 * 初始化插件Application类
 *
 * @author cubershi
 */
object CreateApplicationBloc {
    @Throws(CreateApplicationException::class)
    fun callPluginApplicationOnCreate(
            pluginClassLoader: ClassLoader,
            appClassName: String,
            pluginPackageManager: PluginPackageManager,
            resources: Resources,
            hostAppContext: Context,
            businessPluginActivitiesManager: PluginActivitiesManager,
            businessPluginServiceManager: PluginServicesManager
    ): MockApplication {
        try {
            val appClass = pluginClassLoader.loadClass(appClassName)
            val mockApplication = MockApplication::class.java.cast(appClass.newInstance())
            mockApplication.setPluginResources(resources)
            mockApplication.setPluginClassLoader(pluginClassLoader)
            mockApplication.setPluginActivityLauncher(businessPluginActivitiesManager)
            mockApplication.setServiceOperator(businessPluginServiceManager)
            mockApplication.setHostApplicationContextAsBase(hostAppContext)
            mockApplication.setPluginPackageManager(pluginPackageManager)
            val uiHandler = Handler(Looper.getMainLooper())
            val lock = CountDownLatch(1)
            uiHandler.post {
                mockApplication.onCreate()
                lock.countDown()
            }
            lock.await()
            return mockApplication
        } catch (e: Exception) {
            throw CreateApplicationException(e)
        }

    }
}
