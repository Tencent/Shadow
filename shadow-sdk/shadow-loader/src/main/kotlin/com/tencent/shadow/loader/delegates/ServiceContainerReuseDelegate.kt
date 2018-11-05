package com.tencent.shadow.loader.delegates

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.IBinder
import com.tencent.hydevteam.pluginframework.plugincontainer.HostServiceDelegate
import com.tencent.hydevteam.pluginframework.plugincontainer.HostServiceDelegator
import com.tencent.shadow.loader.delegates.ServiceContainerReuseDelegate.Companion.Operate.*
import com.tencent.shadow.loader.managers.ComponentManager
import java.util.*

/**
 * PluginContainerService复用实现的HostServiceDelegate
 * 用于在宿主中只注册一个Service，然后将Service的行为代理到这个实现中。
 * 再由这个实现持有多个插件Service，管理插件Service的生命周期。
 *
 * @author wenzeyang
 * @author owenguo
 * @author tracyluo
 * @author cubershi
 */
class ServiceContainerReuseDelegate(val mDI: DI) : HostServiceDelegate {
    companion object {
        const val OPT_EXTRA_KEY = "ServiceOpt"

        enum class Operate {
            START, STOP, BIND, UNBIND
        }
    }

    private lateinit var mHostServiceDelegator: HostServiceDelegator
    private lateinit var mShadowServiceDelegateManager: ShadowServiceDelegateManager

    override fun setDelegator(delegator: HostServiceDelegator) {
        mHostServiceDelegator = delegator
        mShadowServiceDelegateManager = ShadowServiceDelegateManager(mDI, delegator)
        mShadowServiceDelegateManager.setOnDelegateChanged(object : OnDelegateChanged {
            override fun onRemoveDelegate(shadowServiceDelegate: ShadowServiceDelegate) {
                shadowServiceDelegate.onDestroy()
                if (mShadowServiceDelegateManager.allDelegates.isEmpty()) {
                    mHostServiceDelegator.superStopSelf()
                }
            }
        })
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        mHostServiceDelegator.superOnCreate()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val bundleForPluginLoader = intent.getBundleExtra(ComponentManager.CM_LOADER_BUNDLE_KEY)!!
        bundleForPluginLoader.classLoader = this.javaClass.classLoader
        val opt = Operate::class.java.cast(bundleForPluginLoader.getSerializable(OPT_EXTRA_KEY))!!
        when (opt) {
            START -> {
                mShadowServiceDelegateManager.startDelegate(intent, flags, startId)
            }
            STOP -> {
                mShadowServiceDelegateManager.stopDelegate(intent)
            }
            BIND -> {
                mShadowServiceDelegateManager.bindToDelegate(intent)
            }
            UNBIND -> {
                mShadowServiceDelegateManager.unbindToDelegate(intent)
            }
        }
        return Service.START_NOT_STICKY
    }

    override fun onDestroy() {
        mShadowServiceDelegateManager.allDelegates.forEach {
            it.onDestroy()
        }
        mShadowServiceDelegateManager.onDestroy()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        mShadowServiceDelegateManager.allDelegates.forEach {
            it.onConfigurationChanged(newConfig)
        }
    }

    override fun onLowMemory() {
        mShadowServiceDelegateManager.allDelegates.forEach {
            it.onLowMemory()
        }
    }

    override fun onTrimMemory(level: Int) {
        mShadowServiceDelegateManager.allDelegates.forEach {
            it.onTrimMemory(level)
        }
    }

    override fun onUnbind(intent: Intent): Boolean {
        // 这里如果要用到返回值的特性需要特殊处理，先这样 tracyluo 2018/6/9
        var flag = false
        mShadowServiceDelegateManager.allDelegates.forEach {
            it.onUnbind(intent, true)
            flag = true
        }
        return if (!flag) {
            return mHostServiceDelegator.superOnUnbind(intent)
        } else false
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        mShadowServiceDelegateManager.allDelegates.forEach {
            it.onTaskRemoved(rootIntent)
        }
    }
}

interface OnDelegateChanged {
    fun onRemoveDelegate(shadowServiceDelegate: ShadowServiceDelegate)
}

class ShadowServiceDelegateManager(private val mDI: DI,
                                   private val mHostServiceDelegator: HostServiceDelegator) {
    private val serviceDelegates = HashMap<ComponentName, ShadowServiceDelegate>()
    private val servicesBindCount = HashMap<ShadowServiceDelegate, Int>()
    private val servicesStarter = HashSet<ShadowServiceDelegate>()
    private var mOnDelegateChanged: OnDelegateChanged? = null

    val allDelegates: Collection<ShadowServiceDelegate>
        get() = serviceDelegates.values

    fun setOnDelegateChanged(mOnDelegateChanged: OnDelegateChanged) {
        this.mOnDelegateChanged = mOnDelegateChanged
    }

    fun getDelegate(loaderBundle: Bundle): ShadowServiceDelegate {
        val pkg = loaderBundle.getString(ComponentManager.CM_PACKAGE_NAME_KEY)!!
        val cls = loaderBundle.getString(ComponentManager.CM_CLASS_NAME_KEY)!!
        return getDelegate(pkg, cls)
    }

    private fun getComponetName(loaderBundle: Bundle): ComponentName {
        val pkg = loaderBundle.getString(ComponentManager.CM_PACKAGE_NAME_KEY)!!
        val cls = loaderBundle.getString(ComponentManager.CM_CLASS_NAME_KEY)!!
        return ComponentName(pkg, cls)
    }

    private fun removeDelegate(intent: Intent) {
        val delegate = serviceDelegates.remove(getComponetName(intent.getBundleExtra(ComponentManager.CM_LOADER_BUNDLE_KEY)))
        if (delegate != null) {
            mOnDelegateChanged!!.onRemoveDelegate(delegate)
        }

    }

    fun getDelegate(pkg: String, cls: String): ShadowServiceDelegate {
        val componentName = ComponentName(pkg, cls)
        var delegate: ShadowServiceDelegate? = serviceDelegates[componentName]
        if (delegate == null) {
            delegate = ShadowServiceDelegate(mDI, mHostServiceDelegator)
            serviceDelegates[componentName] = delegate
        }

        return delegate
    }

    fun onDestroy() {
        serviceDelegates.clear()
        servicesBindCount.clear()
    }

    fun bindToDelegate(intent: Intent) {
        val delegate = getDelegate(intent.getBundleExtra(ComponentManager.CM_LOADER_BUNDLE_KEY))
        if (servicesBindCount.containsKey(delegate)) {
            servicesBindCount[delegate] = servicesBindCount[delegate]!! + 1
        } else {
            if (!servicesStarter.contains(delegate)) {
                delegate.onCreate(intent)
            }
            servicesBindCount[delegate] = 1
        }
        delegate.onBind(intent)
    }

    fun unbindToDelegate(intent: Intent) {
        val delegate = serviceDelegates[getComponetName(intent.getBundleExtra(ComponentManager.CM_LOADER_BUNDLE_KEY))]
        if (delegate != null) {
            if (servicesBindCount.containsKey(delegate)) {
                if (servicesBindCount[delegate]!! > 1) {
                    servicesBindCount[delegate] = servicesBindCount[delegate]!! - 1
                    delegate.onUnbind(intent, false)
                } else {
                    servicesBindCount.remove(delegate)
                    delegate.onUnbind(intent, true)
                    handleUnbindAndStopDelegate(intent)
                }
            }
        }

    }

    fun startDelegate(intent: Intent, flags: Int, startId: Int): Int {
        val delegate = getDelegate(intent.getBundleExtra(ComponentManager.CM_LOADER_BUNDLE_KEY))
        if (servicesStarter.add(delegate))
            delegate.onCreate(intent)
        return delegate.onStartCommand(intent, flags, startId)
    }

    fun stopDelegate(intent: Intent) {
        val delegate = serviceDelegates[getComponetName(intent.getBundleExtra(ComponentManager.CM_LOADER_BUNDLE_KEY))]
        if (delegate != null) {
            if (servicesStarter.remove(delegate)) {
                handleUnbindAndStopDelegate(intent)
            }
        }

    }

    fun handleUnbindAndStopDelegate(intent: Intent) {
        val delegate = serviceDelegates[getComponetName(intent.getBundleExtra(ComponentManager.CM_LOADER_BUNDLE_KEY))]
        if (delegate != null) {
            if (!servicesBindCount.containsKey(delegate) && !servicesStarter.contains(delegate)) {
                removeDelegate(intent)
            }
        }
    }

}