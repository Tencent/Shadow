package com.tencent.shadow.loader.blocs

import android.content.Context
import android.content.res.Resources
import android.os.Handler
import android.os.Looper
import com.tencent.shadow.loader.PluginPackageManager
import com.tencent.shadow.loader.classloaders.PluginClassLoader
import com.tencent.shadow.loader.exceptions.CreateApplicationException
import com.tencent.shadow.loader.managers.PendingIntentManager
import com.tencent.shadow.loader.managers.PluginActivitiesManager
import com.tencent.shadow.loader.managers.PluginServicesManager
import com.tencent.shadow.runtime.ShadowApplication
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
            receivers: Map<String, String>,
            mPendingIntentManager: PendingIntentManager
    ): ShadowApplication {
        try {
            val appClass = pluginClassLoader.loadClass(appClassName)
            val shadowApplication = ShadowApplication::class.java.cast(appClass.newInstance())
            shadowApplication.setPluginResources(resources)
            shadowApplication.setPluginClassLoader(pluginClassLoader)
            shadowApplication.setPluginActivityLauncher(businessPluginActivitiesManager)
            shadowApplication.setServiceOperator(businessPluginServiceManager)
            shadowApplication.setHostApplicationContextAsBase(hostAppContext)
            shadowApplication.setReceivers(receivers)
            shadowApplication.setPluginPackageManager(pluginPackageManager)
            shadowApplication.setLibrarySearchPath(pluginClassLoader.getLibrarySearchPath())
            shadowApplication.pendingIntentConverter = mPendingIntentManager;
            val uiHandler = Handler(Looper.getMainLooper())
            val lock = CountDownLatch(1)
            uiHandler.post {
                shadowApplication.onCreate()
                lock.countDown()
            }
            lock.await()
            return shadowApplication
        } catch (e: Exception) {
            throw CreateApplicationException(e)
        }

    }
}
