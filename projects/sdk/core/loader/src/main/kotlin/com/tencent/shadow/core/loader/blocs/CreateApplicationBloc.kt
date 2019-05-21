package com.tencent.shadow.core.loader.blocs

import android.content.Context
import android.content.res.Resources
import com.tencent.shadow.core.loader.classloaders.PluginClassLoader
import com.tencent.shadow.core.loader.exceptions.CreateApplicationException
import com.tencent.shadow.core.loader.managers.ComponentManager
import com.tencent.shadow.core.loader.managers.PluginPackageManager
import com.tencent.shadow.core.runtime.ShadowApplication
import com.tencent.shadow.core.runtime.remoteview.ShadowRemoteViewCreatorProvider

/**
 * 初始化插件Application类
 *
 * @author cubershi
 */
object CreateApplicationBloc {
    @Throws(CreateApplicationException::class)
    fun createShadowApplication(
            pluginClassLoader: PluginClassLoader,
            appClassName: String?,
            pluginPackageManager: PluginPackageManager,
            resources: Resources,
            hostAppContext: Context,
            componentManager: ComponentManager,
            remoteViewCreatorProvider: ShadowRemoteViewCreatorProvider?
    ): ShadowApplication {
        try {
            val shadowApplication : ShadowApplication;
            shadowApplication = if (appClassName != null) {
                val appClass = pluginClassLoader.loadClass(appClassName)
                ShadowApplication::class.java.cast(appClass.newInstance())
            } else {
                object : ShadowApplication(){}
            }
            val partKey = pluginPackageManager.pluginInfo.partKey
            shadowApplication.setPluginResources(resources)
            shadowApplication.setPluginClassLoader(pluginClassLoader)
            shadowApplication.setPluginComponentLauncher(componentManager)
            shadowApplication.setHostApplicationContextAsBase(hostAppContext)
            shadowApplication.setBroadcasts(componentManager.getBroadcastsByPartKey(partKey))
            shadowApplication.setLibrarySearchPath(pluginClassLoader.getLibrarySearchPath())
            shadowApplication.setDexPath(pluginClassLoader.getDexPath())
            shadowApplication.setBusinessName(pluginPackageManager.pluginInfo.businessName)
            shadowApplication.setPluginPartKey(partKey)
            shadowApplication.remoteViewCreatorProvider = remoteViewCreatorProvider
            return shadowApplication
        } catch (e: Exception) {
            throw CreateApplicationException(e)
        }

    }
}
