package com.tencent.shadow.loader.remoteview

import android.content.Context
import android.view.View
import com.tencent.shadow.core.loader.ShadowPluginLoader
import com.tencent.shadow.runtime.ShadowContext
import com.tencent.shadow.runtime.remoteview.ShadowRemoteViewCreateCallback
import com.tencent.shadow.runtime.remoteview.ShadowRemoteViewCreateException
import com.tencent.shadow.runtime.remoteview.ShadowRemoteViewCreator

internal class ShadowRemoteViewCreatorImp(private val context: Context, private val shadowPluginLoader: ShadowPluginLoader) : ShadowRemoteViewCreator {


    @Throws(ShadowRemoteViewCreateException::class)
    override fun createView(partKey: String, viewClass: String): View {

        val pluginParts = shadowPluginLoader.getPluginParts(partKey)

        if (pluginParts != null) {

            try {
                val clazz = pluginParts.classLoader.loadClass(viewClass)

                val constructor = clazz.getConstructor(Context::class.java)

                // 构造context
                val shadowContext = ShadowContext(context, 0)
                shadowContext.setPluginClassLoader(pluginParts.classLoader)
                shadowContext.setPluginComponentLauncher(shadowPluginLoader.mComponentManager)
                shadowContext.setPluginPartKey(partKey)
                shadowContext.setPluginResources(pluginParts.resources)
                shadowContext.setShadowApplication(pluginParts.application)
                shadowContext.setLibrarySearchPath(pluginParts.classLoader.getLibrarySearchPath())

                val view = View::class.java.cast(constructor.newInstance(shadowContext))

                return view

            } catch (e: Exception) {
                throw ShadowRemoteViewCreateException("创建 $viewClass 失败", e)
            }

        } else {
            throw ShadowRemoteViewCreateException("创建 $viewClass 失败，插件(partKey:$partKey)不存在或者还未加载")
        }

    }


    override fun createView(partKey: String, viewClass: String, callback: ShadowRemoteViewCreateCallback?) {
        // TODO("not implemented")
        if (callback != null) {
            callback.onViewCreateFailed(java.lang.Exception("创建View失败，createView(String apkKey, String viewClass, ShadowRemoteViewCreateCallback callback)暂未实现！"))
        }
    }
}