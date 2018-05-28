package com.tencent.cubershi.plugin_loader.blocs

import android.content.res.Resources

import com.tencent.cubershi.mock_interface.MockApplication
import com.tencent.cubershi.plugin_loader.exceptions.CreateApplicationException

/**
 * 初始化插件Application类
 *
 * @author cubershi
 */
object CreateApplicationBloc {
    @Throws(CreateApplicationException::class)
    fun callPluginApplicationOnCreate(pluginClassLoader: ClassLoader, appClassName: String, resources: Resources): MockApplication {
        try {
            val appClass = pluginClassLoader.loadClass(appClassName)
            val mockApplication = MockApplication::class.java.cast(appClass.newInstance())
            mockApplication.setPluginResources(resources)
            mockApplication.onCreate()
            return mockApplication
        } catch (e: Exception) {
            throw CreateApplicationException(e)
        }

    }
}
