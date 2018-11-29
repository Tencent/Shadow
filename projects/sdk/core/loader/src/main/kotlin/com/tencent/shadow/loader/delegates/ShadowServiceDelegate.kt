package com.tencent.shadow.loader.delegates

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.IBinder
import com.tencent.shadow.loader.managers.ComponentManager
import com.tencent.shadow.loader.managers.ComponentManager.Companion.CM_PART_KEY
import com.tencent.shadow.runtime.ShadowService

/**
 * Created by tracyluo on 2018/6/5.
 */
class ShadowServiceDelegate(private val mDI: DI, private val mHostContext : Context) : ShadowDelegate() {
    private lateinit var mPluginService: ShadowService

    fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        replaceIntentExtra(intent)
        return mPluginService.onStartCommand(intent, flags, startId)
    }


    fun onBind(intent: Intent): IBinder? {
        replaceIntentExtra(intent)
        return mPluginService.onBind(intent)
    }

    fun onUnbind(intent: Intent): Boolean {
        replaceIntentExtra(intent)
        return mPluginService.onUnbind(intent)
    }

    fun onLowMemory() {
        return mPluginService.onLowMemory()
    }

    fun onConfigurationChanged(configuration: Configuration?) {
        return mPluginService.onConfigurationChanged(configuration)
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

        val partKey = intent.getStringExtra(CM_PART_KEY)!!
        mDI.inject(this, partKey)
        val bundleForPluginLoader = intent.getBundleExtra(ComponentManager.CM_LOADER_BUNDLE_KEY)
        bundleForPluginLoader.classLoader = this.javaClass.classLoader
        val cls = bundleForPluginLoader.getString(ComponentManager.CM_CLASS_NAME_KEY)
        val aClass = mPluginClassLoader.loadClass(cls)
        mPluginService = ShadowService::class.java.cast(aClass.newInstance())
        mPluginService.setHostContextAsBase(mHostContext)
        //mPluginService.setHostServiceDelegator(mHostServiceDelegator)
        mPluginService.setPluginResources(mPluginResources)
        mPluginService.setPluginClassLoader(mPluginClassLoader)
        mPluginService.setShadowApplication(mPluginApplication)
        mPluginService.setPluginComponentLauncher(mComponentManager)
        mPluginService.setLibrarySearchPath(mPluginClassLoader.getLibrarySearchPath())
        mPluginService.setPluginPartKey(partKey)


        return mPluginService.onCreate()
    }

    private fun replaceIntentExtra(intent: Intent) {
        val pluginExtras: Bundle? = intent.getBundleExtra(ComponentManager.CM_EXTRAS_BUNDLE_KEY)
        intent.replaceExtras(pluginExtras)
    }
}