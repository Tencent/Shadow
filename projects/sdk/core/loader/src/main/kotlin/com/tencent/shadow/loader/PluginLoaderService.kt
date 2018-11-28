package com.tencent.shadow.loader

import android.content.Intent
import android.os.IBinder
import android.os.RemoteException
import com.tencent.hydevteam.common.classloader.ApkClassLoader
import com.tencent.hydevteam.pluginframework.pluginloader.PluginLoader
import com.tencent.shadow.runtime.ShadowService

class PluginLoaderService : ShadowService() {

    private var mPluginLoader: PluginLoader? = null

    private val mApkClassLoader = PluginLoaderService::class.java.classLoader as ApkClassLoader

    private val mBinder = object : IPluginLoaderServiceInterface.Stub() {
        @Throws(RemoteException::class)
        override fun loadPlugin(partKey: String) {

        }

        @Throws(RemoteException::class)
        override fun callApplicationOnCreate(partKey: String) {

        }

        @Throws(RemoteException::class)
        override fun convertActivityIntent(pluginActivityIntent: Intent): Intent? {
            return null
        }

        @Throws(RemoteException::class)
        override fun startPluginService(pluginServiceIntent: Intent) {

        }

        @Throws(RemoteException::class)
        override fun bindPluginService(pluginServiceIntent: Intent): IBinder? {
            return null
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        if (mPluginLoader != null) {
            try {
                mPluginLoader = mApkClassLoader.getInterface(PluginLoader::class.java, CLASSS_PLUGIN_LOADER_IMPL)
            } catch (e: Exception) {
                throw RuntimeException("当前的classLoader找不到PluginLoader的实现", e)
            }

        }
        return mBinder
    }

    companion object {

        private val CLASSS_PLUGIN_LOADER_IMPL = "com.tencent.shadow.sdk.pluginloader.pluginLoaderImpl"
    }
}
