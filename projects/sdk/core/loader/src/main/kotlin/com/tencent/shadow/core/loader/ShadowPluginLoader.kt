package com.tencent.shadow.core.loader

import android.content.Context
import com.tencent.hydevteam.common.progress.ProgressFuture
import com.tencent.hydevteam.pluginframework.installedplugin.InstalledPlugin
import com.tencent.hydevteam.pluginframework.plugincontainer.*
import com.tencent.hydevteam.pluginframework.pluginloader.LoadPluginException
import com.tencent.hydevteam.pluginframework.pluginloader.PluginLoader
import com.tencent.hydevteam.pluginframework.pluginloader.RunningPlugin
import com.tencent.shadow.core.loader.blocs.LoadPluginBloc
import com.tencent.shadow.core.loader.delegates.DI
import com.tencent.shadow.core.loader.delegates.ServiceContainerReuseDelegate
import com.tencent.shadow.core.loader.delegates.ShadowActivityDelegate
import com.tencent.shadow.core.loader.delegates.ShadowDelegate
import com.tencent.shadow.core.loader.infos.PluginParts
import com.tencent.shadow.core.loader.managers.CommonPluginPackageManager
import com.tencent.shadow.core.loader.managers.ComponentManager
import com.tencent.shadow.core.loader.managers.PluginBroadcastManager
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

abstract class ShadowPluginLoader : PluginLoader, DelegateProvider, DI {

    private val mExecutorService = Executors.newCachedThreadPool()

    /**
     * loadPlugin方法是在子线程被调用的。而getHostActivityDelegate方法是在主线程被调用的。
     * 两个方法需要传递数据（主要是PluginParts），因此需要同步。
     */
    private val mLock = ReentrantLock()

    /**
     * 多插件Map
     * key: partKey
     * value: PluginParts
     * @GuardedBy("mLock")
     */
    private val mPluginPartsMap = hashMapOf<String, PluginParts>()

    /**
     * @GuardedBy("mLock")
     */
    abstract val mComponentManager: ComponentManager

    /**
     * @GuardedBy("mLock")
     */
    abstract fun getBusinessPluginReceiverManager(hostAppContext: Context): PluginBroadcastManager

    abstract val mExceptionReporter: Reporter

    private val mCommonPluginPackageManager = CommonPluginPackageManager()

    private lateinit var mPluginServiceManager: PluginServiceManager

    private val mPluginServiceManagerLock = ReentrantLock()

    /**
     * 插件将要使用的so的ABI，Loader会将其从apk中解压出来。
     * 如果插件不需要so，则返回""空字符串。
     */
    abstract val mAbi: String


    fun getPluginServiceManager(): PluginServiceManager {
        mPluginServiceManagerLock.withLock {
            return mPluginServiceManager
        }

    }

    fun getPluginParts(partKey: String): PluginParts? {
        mLock.withLock {
            return mPluginPartsMap[partKey]
        }
    }

    @Throws(LoadPluginException::class)
    override fun loadPlugin(
            hostAppContext: Context,
            installedPlugin: InstalledPlugin) : ProgressFuture<RunningPlugin> {

        // 在这里初始化PluginServiceManager
        mPluginServiceManagerLock.withLock {
            if (!::mPluginServiceManager.isInitialized) {
                mPluginServiceManager = PluginServiceManager(this, hostAppContext)
            }

            mComponentManager.setPluginServiceManager(mPluginServiceManager)
        }



        return LoadPluginBloc.loadPlugin(
                mExecutorService,
                mAbi,
                mCommonPluginPackageManager,
                mComponentManager,
                getBusinessPluginReceiverManager(hostAppContext),
                mLock,
                mPluginPartsMap,
                hostAppContext,
                installedPlugin
        )
    }

    override fun setPluginDisabled(installedPlugin: InstalledPlugin): Boolean {
        return false
    }

    override fun getHostActivityDelegate(aClass: Class<out HostActivityDelegator>): HostActivityDelegate {
        return ShadowActivityDelegate(this)
    }

    override fun getHostServiceDelegate(aClass: Class<out HostServiceDelegator>): HostServiceDelegate {
        return ServiceContainerReuseDelegate(this)
    }

    override fun inject(delegate: ShadowDelegate, partKey: String) {
        mLock.withLock {
            val pluginParts = mPluginPartsMap[partKey]
            if (pluginParts == null) {
                throw IllegalStateException("partKey==${partKey}在map中找不到。此时map：${mPluginPartsMap}")
            } else {
                delegate.inject(pluginParts.packageManager)
                delegate.inject(pluginParts.application)
                delegate.inject(pluginParts.classLoader)
                delegate.inject(pluginParts.resources)
                delegate.inject(mExceptionReporter)
                delegate.inject(mComponentManager)
            }
        }
    }

    companion object {
        private val mLogger = LoggerFactory.getLogger(ShadowPluginLoader::class.java)
    }
}
