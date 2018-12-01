package com.tencent.shadow.loader

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.RemoteException
import com.tencent.hydevteam.common.classloader.ApkClassLoader
import com.tencent.hydevteam.pluginframework.pluginloader.PluginLoader
import com.tencent.shadow.sdk.service.IPluginLoaderServiceInterface

open class PluginLoaderService(hostContext: Context) : IPluginLoaderServiceInterface.Stub() {

    companion object {

        private val CLASSS_PLUGIN_LOADER_IMPL = "com.tencent.shadow.sdk.pluginloader.pluginLoaderImpl"
    }

    private var mPluginLoader: PluginLoader? = null

    private val mApkClassLoader = PluginLoaderService::class.java.classLoader as ApkClassLoader

    private var mContext: Context;

    init {
        try {
            mPluginLoader = mApkClassLoader.getInterface(PluginLoader::class.java, CLASSS_PLUGIN_LOADER_IMPL)
        } catch (e: Exception) {
            throw RuntimeException("当前的classLoader找不到PluginLoader的实现", e)
        }
        mContext = hostContext;
    }

    @Throws(RemoteException::class)
    override fun loadPlugin(partKey: String, pluginApkFilePath: String, isInterface: Boolean) {

    }

    @Throws(RemoteException::class)
    override fun callApplicationOnCreate(partKey: String) {

    }

    @Throws(RemoteException::class)
    override fun convertActivityIntent(pluginActivityIntent: Intent): Intent? {
        return null
    }

    @Throws(RemoteException::class)
    override fun startPluginService(pluginServiceIntent: Intent): ComponentName? {
        return null
    }

    override fun stopPluginService(pluginServiceIntent: Intent): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun bindPluginService(pluginServiceIntent: Intent, connection: IServiceConnection, flags: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun unbindService(conn: IServiceConnection) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
