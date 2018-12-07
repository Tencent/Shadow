package com.tencent.shadow.core.loader

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.RemoteException
import com.tencent.hydevteam.common.classloader.ApkClassLoader
import com.tencent.hydevteam.pluginframework.installedplugin.InstalledPlugin
import com.tencent.hydevteam.pluginframework.pluginloader.PluginLoader
import java.io.File
import java.util.concurrent.CountDownLatch

open class PluginLoaderService(hostContext: Context) : IPluginLoaderServiceInterface.Stub() {

    companion object {

        private val CLASSS_PLUGIN_LOADER_IMPL = "com.tencent.shadow.sdk.pluginloader.pluginLoaderImpl"
    }

    private val mPluginLoader: ShadowPluginLoader

    private val mApkClassLoader = PluginLoaderService::class.java.classLoader as ApkClassLoader

    private var mContext: Context;

    private val mUiHandler = Handler(Looper.getMainLooper())

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
        val installedPlugin = InstalledPlugin(partKey, pluginFile.lastModified().toString(), pluginFile, 0)

        val runningPlugin = mPluginLoader.loadPlugin(mContext, installedPlugin)
        runningPlugin.get()
    }

    @Throws(RemoteException::class)
    @Synchronized
    override fun callApplicationOnCreate(partKey: String) {

        fun realAction() {
            val pluginParts = mPluginLoader.getPluginParts(partKey)

            pluginParts?.let {
                pluginParts.application.onCreate()
            }
        }


        // 确保在ui线程调用
        if (isUiThread()) {
            realAction()
        } else {
            val waitUiLock = CountDownLatch(1)
            mUiHandler.post {
                realAction()
                waitUiLock.countDown()
            }
        }
    }

    @Throws(RemoteException::class)
    override fun convertActivityIntent(pluginActivityIntent: Intent): Intent? {
        return mPluginLoader.mComponentManager.convertPluginActivityIntent(pluginActivityIntent)
    }

    @Throws(RemoteException::class)
    @Synchronized
    override fun startPluginService(pluginServiceIntent: Intent): ComponentName? {

        fun realAction(): ComponentName? {
            return mPluginLoader.getPluginServiceManager().startPluginService(pluginServiceIntent)
        }


        // 确保在ui线程调用
        var componentName: ComponentName? = null
        if (isUiThread()) {
            componentName = realAction()
        } else {
            val waitUiLock = CountDownLatch(1)
            mUiHandler.post {
                componentName = realAction()
                waitUiLock.countDown()
            }
        }

        return componentName
    }

    @Throws(RemoteException::class)
    @Synchronized
    override fun stopPluginService(pluginServiceIntent: Intent): Boolean {

        fun realAction(): Boolean {
            return mPluginLoader.getPluginServiceManager().stopPluginService(pluginServiceIntent)
        }

        // 确保在ui线程调用
        var stopped: Boolean = false
        if (isUiThread()) {
            stopped = realAction()
        } else {
            val waitUiLock = CountDownLatch(1)
            mUiHandler.post {
                stopped = realAction()
                waitUiLock.countDown()
            }
        }
        return stopped
    }

    @Throws(RemoteException::class)
    @Synchronized
    override fun bindPluginService(pluginServiceIntent: Intent, connection: IServiceConnection, flags: Int): Boolean {

        fun realAction(): Boolean {
            // client端同一个IServiceConnection对象，通过IPC传过来不会对应服务端这边的同一个IServiceConnection
            // 但是asBinder返回是同一个对象
            val connBinder = connection.asBinder()

            if (mConnectionMap[connBinder] == null) {
                mConnectionMap[connBinder] = ServiceConnectionWrapper(connection)
            }

            val connWrapper = mConnectionMap[connBinder]!!
            return mPluginLoader.getPluginServiceManager().bindPluginService(pluginServiceIntent, connWrapper, flags)
        }

        // 确保在ui线程调用
        var stop: Boolean = false
        if (isUiThread()) {
            stop = realAction()
        } else {
            val waitUiLock = CountDownLatch(1)
            mUiHandler.post {
                stop = realAction()
                waitUiLock.countDown()
            }
        }

        return stop

    }

    @Throws(RemoteException::class)
    @Synchronized
    override fun unbindService(conn: IServiceConnection) {
        mUiHandler.post {
            val connBinder = conn.asBinder()
            mConnectionMap[connBinder]?.let {
                mConnectionMap.remove(connBinder)
                mPluginLoader.getPluginServiceManager().unbindPluginService(it)
            }
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

    private fun isUiThread(): Boolean {

        return Looper.myLooper() == Looper.getMainLooper()
    }
}
