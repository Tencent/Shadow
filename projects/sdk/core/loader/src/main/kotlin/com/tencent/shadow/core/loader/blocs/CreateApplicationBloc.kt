package com.tencent.shadow.core.loader.blocs

import android.content.Context
import android.content.res.Resources
import com.tencent.shadow.core.loader.classloaders.PluginClassLoader
import com.tencent.shadow.core.loader.exceptions.CreateApplicationException
import com.tencent.shadow.core.loader.managers.ComponentManager
import com.tencent.shadow.core.loader.managers.PluginPackageManager
import com.tencent.shadow.runtime.ShadowApplication
import com.tencent.shadow.runtime.remoteview.ShadowRemoteViewCreatorProvider

/**
 * 初始化插件Application类
 *
 * @author cubershi
 */
object CreateApplicationBloc {
    @Throws(CreateApplicationException::class)
    fun createShadowApplication(
            pluginClassLoader: PluginClassLoader,
            appClassName: String,
            pluginPackageManager: PluginPackageManager,
            resources: Resources,
            hostAppContext: Context,
            componentManager: ComponentManager,
            broadcasts: Map<String, List<String>>,
            remoteViewCreatorProvider: ShadowRemoteViewCreatorProvider?
    ): ShadowApplication {
        try {
            val appClass = pluginClassLoader.loadClass(appClassName)
            val shadowApplication = ShadowApplication::class.java.cast(appClass.newInstance())
            shadowApplication.setPluginResources(resources)
            shadowApplication.setPluginClassLoader(pluginClassLoader)
            shadowApplication.setPluginComponentLauncher(componentManager)
            shadowApplication.setHostApplicationContextAsBase(hostAppContext)
            shadowApplication.setBroadcasts(broadcasts)
            shadowApplication.setPluginPackageManager(pluginPackageManager)
            shadowApplication.setLibrarySearchPath(pluginClassLoader.getLibrarySearchPath())
            shadowApplication.setPluginPartKey(pluginPackageManager.pluginInfo.partKey)
            shadowApplication.remoteViewCreatorProvider = remoteViewCreatorProvider
            return shadowApplication
        } catch (e: Exception) {
            throw CreateApplicationException(e)
        }

    }
}
