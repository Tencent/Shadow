package com.tencent.shadow.loader

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import com.tencent.hydevteam.common.classloader.ApkClassLoader
import com.tencent.hydevteam.pluginframework.installedplugin.InstalledPlugin
import com.tencent.hydevteam.pluginframework.pluginloader.PluginLoader
import com.tencent.shadow.sdk.service.IPluginLoaderServiceInterface
import com.tencent.shadow.sdk.service.IServiceConnection
import java.io.File

open class PluginLoaderService(hostContext: Context) : IPluginLoaderServiceInterface.Stub() {

    companion object {

        private val CLASSS_PLUGIN_LOADER_IMPL = "com.tencent.shadow.sdk.pluginloader.pluginLoaderImpl"
    }

    private val mPluginLoader: ShadowPluginLoader

    private val mApkClassLoader = PluginLoaderService::class.java.classLoader as ApkClassLoader

    private var mContext: Context;

    /**
     * 同一个IServiceConnection只会对应一个ServiceConnection对象，此Map就是保存这种对应关系
     */
    private val mConnectionMap = HashMap<IBinder, ServiceConnection>()

    init {
        try {
            mPluginLoader = mApkClassLoader.getInterface(PluginLoader::class.java, CLASSS_PLUGIN_LOADER_IMPL) as ShadowPluginLoader
        } catch (e: Exception) {
            throw RuntimeException("当前的classLoader找不到PluginLoader的实现", e)
        }
        mContext = hostContext;
    }

    @Throws(RemoteException::class)
    override fun loadPlugin(partKey: String, pluginApkFilePath: String, isInterface: Boolean) {

        val pluginFile = File(pluginApkFilePath)
        val installedPlugin = InstalledPlugin(partKey, null, pluginFile, 0)

        mPluginLoader.loadPlugin(mContext, installedPlugin)
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
        return mPluginLoader.getPluginServiceManager().startPluginService(pluginServiceIntent)
    }

    override fun stopPluginService(pluginServiceIntent: Intent): Boolean {
        return mPluginLoader.getPluginServiceManager().stopPluginService(pluginServiceIntent)
    }

    override fun bindPluginService(pluginServiceIntent: Intent, connection: IServiceConnection, flags: Int): Boolean {

        // client端同一个IServiceConnection对象，通过IPC传过来不会对应服务端这边的同一个IServiceConnection
        // 但是asBinder返回是同一个对象
        val connBinder = connection.asBinder()

        if (mConnectionMap[connBinder] == null) {
            mConnectionMap[connBinder] = ServiceConnectionWrapper(connection)
        }

        val connWrapper = mConnectionMap[connBinder]!!
        return mPluginLoader.getPluginServiceManager().bindPluginService(pluginServiceIntent, connWrapper, flags)
    }

    override fun unbindService(conn: IServiceConnection) {
        val connBinder = conn.asBinder()
        mConnectionMap[connBinder]?.let {
            mConnectionMap.remove(connBinder)
            mPluginLoader.getPluginServiceManager().unbindPluginService(it)
        }
    }

    private class ServiceConnectionWrapper(private val mConnection: IServiceConnection) : ServiceConnection {

        override fun onServiceDisconnected(name: ComponentName) {
            mConnection.onServiceDisconnected(name)
        }

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            mConnection.onServiceConnected(name, service)
        }

    }
}
