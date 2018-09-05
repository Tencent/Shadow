package com.tencent.shadow.loader.delegates

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.IBinder
import com.tencent.hydevteam.pluginframework.plugincontainer.HostServiceDelegator
import com.tencent.shadow.runtime.ShadowService

/**
 * Created by tracyluo on 2018/6/5.
 */
class ShadowServiceDelegate(private val mDI: DI,
                            private val mHostServiceDelegator: HostServiceDelegator) : ShadowDelegate() {
    private lateinit var mPluginService: ShadowService
    private lateinit var mBinder: IBinder
    private var mIsSetService: Boolean = false
    private var mIsBound: Boolean = false
    fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return mPluginService.onStartCommand(intent, flags, startId)
    }

    fun onUnbind(intent: Intent, allUnBind: Boolean): Boolean {
        val conn = mPluginServicesManager.getConnection(intent)
        conn?.onServiceDisconnected(intent.component)
        mPluginServicesManager.deleteConnection(conn)
        if (allUnBind) {
            return mPluginService.onUnbind(intent)
        }
        return false
    }

    fun onLowMemory() {
        return mPluginService.onLowMemory()
    }

    fun onConfigurationChanged(configuration: Configuration?) {
        return mPluginService.onConfigurationChanged(configuration)
    }

    fun onBind(intent: Intent): IBinder {
        if (!mIsBound) {
            mBinder = mPluginService.onBind(intent)
            mIsBound = true
        }
        mPluginServicesManager.getConnection(intent)?.onServiceConnected(intent.component, mBinder)
        return mBinder
    }

    fun onTrimMemory(level: Int) {
        return mPluginService.onTrimMemory(level)
    }

    fun onTaskRemoved(rootIntent: Intent) {
        return mPluginService.onTaskRemoved(rootIntent)
    }

    fun onDestroy() {
        return mPluginService.onDestroy()
    }

    fun onCreate(intent: Intent) {
        if (!mIsSetService) {
            mDI.inject(this, "todo_support_multi_apk")
            val cls = intent.getStringExtra("className")
            val aClass = mPluginClassLoader.loadClass(cls)
            mPluginService = ShadowService::class.java.cast(aClass.newInstance())
            mPluginService.setHostContextAsBase(mHostServiceDelegator as Context)
            mPluginService.setPluginResources(mPluginResources)
            mPluginService.setPluginClassLoader(mPluginClassLoader)
            mPluginService.setShadowApplication(mPluginApplication)
            mPluginService.setPluginActivityLauncher(mPluginActivitiesManager)
            mPluginService.setServiceOperator(mPluginServicesManager)
            mPluginService.pendingIntentConverter = mPendingIntentManager;
            mPluginService.setLibrarySearchPath(mPluginClassLoader.getLibrarySearchPath())
            mIsSetService = true
        }
        return mPluginService.onCreate()
    }

}