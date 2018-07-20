package com.tencent.shadow.pluginloader.blocs

import android.content.Context
import android.content.res.Resources
import android.os.Handler
import android.os.Looper

import com.tencent.cubershi.mock_interface.MockApplication
import com.tencent.shadow.pluginloader.PluginPackageManager
import com.tencent.shadow.pluginloader.classloaders.PluginClassLoader
import com.tencent.shadow.pluginloader.exceptions.CreateApplicationException
import com.tencent.shadow.pluginloader.managers.PluginActivitiesManager
import com.tencent.shadow.pluginloader.managers.PluginServicesManager
import java.util.concurrent.CountDownLatch

/**
 * 初始化插件Application类
 *
 * @author cubershi
 */
object CreateApplicationBloc {
    @Throws(CreateApplicationException::class)
    fun callPluginApplicationOnCreate(
            pluginClassLoader: PluginClassLoader,
            appClassName: String,
            pluginPackageManager: PluginPackageManager,
            resources: Resources,
            hostAppContext: Context,
            businessPluginActivitiesManager: PluginActivitiesManager,
            businessPluginServiceManager: PluginServicesManager,
            receivers: Map<String, String>
    ): MockApplication {
        try {
            val appClass = pluginClassLoader.loadClass(appClassName)
            val mockApplication = MockApplication::class.java.cast(appClass.newInstance())
            mockApplication.setPluginResources(resources)
            mockApplication.setPluginClassLoader(pluginClassLoader)
            mockApplication.setPluginActivityLauncher(businessPluginActivitiesManager)
            mockApplication.setServiceOperator(businessPluginServiceManager)
            mockApplication.setHostApplicationContextAsBase(hostAppContext)
            mockApplication.setReceivers(receivers)
            mockApplication.setPluginPackageManager(pluginPackageManager)
            mockApplication.setLibrarySearchPath(pluginClassLoader.getLibrarySearchPath())
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
