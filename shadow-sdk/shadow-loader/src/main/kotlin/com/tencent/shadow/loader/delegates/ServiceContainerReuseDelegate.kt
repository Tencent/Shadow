package com.tencent.shadow.loader.delegates

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.res.Configuration
import android.os.IBinder
import com.tencent.hydevteam.pluginframework.plugincontainer.HostServiceDelegate
import com.tencent.hydevteam.pluginframework.plugincontainer.HostServiceDelegator
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
        private val OPT_EXTRA_KEY = "ServiceOpt"
    }

    private lateinit var mHostServiceDelegator: HostServiceDelegator
    private lateinit var mShadowServiceDelegateManager: ShadowServiceDelegateManager

    override fun setDelegator(delegator: HostServiceDelegator) {
        mHostServiceDelegator = delegator
        mShadowServiceDelegateManager = ShadowServiceDelegateManager(mDI, delegator)
        mShadowServiceDelegateManager.setOnDelegateChanged(object : OnDelegateChanged {
            override fun onRemoveDelegate(shadowServiceDelegate: ShadowServiceDelegate) {
                if (mShadowServiceDelegateManager.allDelegates.isEmpty()) {
                    mHostServiceDelegator.superStopSelf()
                } else {
                    shadowServiceDelegate.onDestroy()
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

    /**
     * http://tapd.oa.com/androidQQ/bugtrace/bugs/view?bug_id=1010066461057998459
     * 2.17.6.6 目前默认认为：所有的service的onStartCommand都只返回{@link Service#START_STICKY}
     * 在这种默认情况下
     * 1、强制返回 {@link Service#START_REDELIVER_INTENT}
     * 2、对于 {@link Service#START_FLAG_REDELIVERY}的调用，强制用null作为intent参数传入实际调用
     * <p>
     * 2017/11/28   add by owenguo
     * 多插件的时候，由于群视频插件和交友插件的包名一致，先启动群视频插件后，需要杀进程再启动花样交友插件，这样
     * service如果这个时候被重启了，在SixGodServiceDelegate创建service的逻辑里面会重新加载群视频插件，导致交友插件无法被加载
     * 且启动交友插件变成了启动群视频插件了。这里返回值改成START_NOT_STICKY，让service不要在杀死后自动重启
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            return Service.START_NOT_STICKY
        }
        val opt = intent.getStringExtra(OPT_EXTRA_KEY)
        when (opt) {
            "stop" -> {
                mShadowServiceDelegateManager.stopDelegate(intent)
                return Service.START_NOT_STICKY
            }
            "bind" -> {
                mShadowServiceDelegateManager.bindToDelegate(intent)
                return Service.START_NOT_STICKY
            }
            "unbind" -> {
                mShadowServiceDelegateManager.unbindToDelegate(intent)
                return Service.START_NOT_STICKY
            }
        }
        return mShadowServiceDelegateManager.startDelegate(intent, flags, startId)
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

    fun getDelegate(intent: Intent): ShadowServiceDelegate {
        val pkg = intent.getStringExtra(KEY_PKG_NAME)
        val cls = intent.getStringExtra(KEY_CLASS_NAME)
        return getDelegate(pkg, cls)
    }

    private fun getComponetName(intent: Intent): ComponentName {
        val pkg = intent.getStringExtra(KEY_PKG_NAME)
        val cls = intent.getStringExtra(KEY_CLASS_NAME)
        return ComponentName(pkg, cls)
    }

    private fun removeDelegate(intent: Intent) {
        val delegate = serviceDelegates.remove(getComponetName(intent))
        if (delegate != null) {
            mOnDelegateChanged!!.onRemoveDelegate(delegate)
        }

    }

    fun getDelegate(pkg: String, cls: String): ShadowServiceDelegate {
        val componentName = ComponentName(pkg, cls)
        var delegate: ShadowServiceDelegate? = serviceDelegates[componentName]
        if (delegate == null) {
            delegate = ShadowServiceDelegate(mDI, mHostServiceDelegator)
        }

        return delegate
    }

    fun onDestroy() {
        serviceDelegates.clear()
        servicesBindCount.clear()
    }

    fun bindToDelegate(intent: Intent) {
        val delegate = getDelegate(intent)
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
        val delegate = serviceDelegates[getComponetName(intent)]
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
        val delegate = getDelegate(intent)
        if (servicesStarter.add(delegate))
            delegate.onCreate(intent)
        return delegate.onStartCommand(intent, flags, startId)
    }

    fun stopDelegate(intent: Intent) {
        val delegate = serviceDelegates[getComponetName(intent)]
        if (delegate != null) {
            if (servicesStarter.remove(delegate)) {
                handleUnbindAndStopDelegate(intent)
            }
        }

    }

    fun handleUnbindAndStopDelegate(intent: Intent) {
        val delegate = serviceDelegates[getComponetName(intent)]
        if (delegate != null) {
            if (!servicesBindCount.containsKey(delegate) && !servicesStarter.contains(delegate)) {
                removeDelegate(intent)
            }
        }
    }

    companion object {
        // hardcode了sixgod中的intent参数
        private val KEY_PKG_NAME = "packageName"
        private val KEY_CLASS_NAME = "className"
    }
}