package com.tencent.shadow.loader.delegates

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.IBinder
import com.tencent.hydevteam.pluginframework.plugincontainer.HostServiceDelegator
import com.tencent.shadow.loader.managers.ComponentManager
import com.tencent.shadow.loader.managers.ComponentManager.Companion.CM_PART_KEY
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
        replaceIntentExtra(intent)
        return mPluginService.onStartCommand(intent, flags, startId)
    }

    fun onUnbind(intent: Intent, allUnBind: Boolean): Boolean {
        val intentKey = intent.getBundleExtra(ComponentManager.CM_LOADER_BUNDLE_KEY).getLong(ComponentManager.CM_INTENT_KEY, -1)
        val conn = mComponentManager.getConnection(intentKey)
        conn?.onServiceDisconnected(intent.component)
        mComponentManager.deleteConnection(conn)
        if (allUnBind) {
            replaceIntentExtra(intent)
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
        val intentKey = intent.getBundleExtra(ComponentManager.CM_LOADER_BUNDLE_KEY).getLong(ComponentManager.CM_INTENT_KEY, -1)
        if (!mIsBound) {
            replaceIntentExtra(intent)
            mBinder = mPluginService.onBind(intent)
            mIsBound = true
        }
        mComponentManager.getConnection(intentKey)?.onServiceConnected(intent.component, mBinder)
        return mBinder
    }

    fun onTrimMemory(level: Int) {
        return mPluginService.onTrimMemory(level)
    }

    fun onTaskRemoved(rootIntent: Intent) {
        replaceIntentExtra(rootIntent)
        return mPluginService.onTaskRemoved(rootIntent)
    }

    fun onDestroy() {
        return mPluginService.onDestroy()
    }

    fun onCreate(intent: Intent) {
        if (!mIsSetService) {
            val partKey = intent.getStringExtra(CM_PART_KEY)!!
            mDI.inject(this, partKey)

            val bundleForPluginLoader = intent.getBundleExtra(ComponentManager.CM_LOADER_BUNDLE_KEY)
            bundleForPluginLoader.classLoader = this.javaClass.classLoader
            val cls = bundleForPluginLoader.getString(ComponentManager.CM_CLASS_NAME_KEY)

            val aClass = mPluginClassLoader.loadClass(cls)
            mPluginService = ShadowService::class.java.cast(aClass.newInstance())
            mPluginService.setHostContextAsBase(mHostServiceDelegator as Context)
            mPluginService.setHostServiceDelegator(mHostServiceDelegator)
            mPluginService.setPluginResources(mPluginResources)
            mPluginService.setPluginClassLoader(mPluginClassLoader)
            mPluginService.setShadowApplication(mPluginApplication)
            mPluginService.setPluginComponentLauncher(mComponentManager)
            mPluginService.setLibrarySearchPath(mPluginClassLoader.getLibrarySearchPath())
            mPluginService.setPluginPartKey(partKey)
            mPluginService.remoteViewCreatorProvider = mRemoteViewCreatorProvider
            mIsSetService = true
        }
        return mPluginService.onCreate()
    }

    fun replaceIntentExtra(intent: Intent) {
        val pluginExtras: Bundle? = intent.getBundleExtra(ComponentManager.CM_EXTRAS_BUNDLE_KEY)
        intent.replaceExtras(pluginExtras)
    }
}