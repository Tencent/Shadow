package com.tencent.shadow.loader.blocs

import android.content.Context
import android.content.res.Resources
import android.os.Handler
import android.os.Looper
import com.tencent.shadow.loader.classloaders.PluginClassLoader
import com.tencent.shadow.loader.exceptions.CreateApplicationException
import com.tencent.shadow.loader.managers.ComponentManager
import com.tencent.shadow.loader.managers.PluginPackageManager
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
            componentManager: ComponentManager,
            receivers: Map<String, String>
    ): ShadowApplication {
        try {
            val appClass = pluginClassLoader.loadClass(appClassName)
            val shadowApplication = ShadowApplication::class.java.cast(appClass.newInstance())
            shadowApplication.setPluginResources(resources)
            shadowApplication.setPluginClassLoader(pluginClassLoader)
            shadowApplication.setPluginComponentLauncher(componentManager)
            shadowApplication.setHostApplicationContextAsBase(hostAppContext)
            shadowApplication.setReceivers(receivers)
            shadowApplication.setPluginPackageManager(pluginPackageManager)
            shadowApplication.setLibrarySearchPath(pluginClassLoader.getLibrarySearchPath())
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
