package com.tencent.shadow.loader

import android.content.Context
import android.content.res.Resources
import com.tencent.hydevteam.common.progress.ProgressFuture
import com.tencent.hydevteam.common.progress.ProgressFutureImpl
import com.tencent.hydevteam.pluginframework.installedplugin.InstalledPlugin
import com.tencent.hydevteam.pluginframework.plugincontainer.*
import com.tencent.hydevteam.pluginframework.pluginloader.LoadPluginException
import com.tencent.hydevteam.pluginframework.pluginloader.PluginLoader
import com.tencent.hydevteam.pluginframework.pluginloader.RunningPlugin
import com.tencent.shadow.loader.blocs.*
import com.tencent.shadow.loader.classloaders.PluginClassLoader
import com.tencent.shadow.loader.delegates.DI
import com.tencent.shadow.loader.delegates.ShadowActivityDelegate
import com.tencent.shadow.loader.delegates.ShadowDelegate
import com.tencent.shadow.loader.delegates.ShadowServiceDelegate
import com.tencent.shadow.loader.managers.PendingIntentManager
import com.tencent.shadow.loader.managers.PluginActivitiesManager
import com.tencent.shadow.loader.managers.PluginReceiverManager
import com.tencent.shadow.loader.managers.PluginServicesManager
import com.tencent.shadow.runtime.ShadowApplication
import org.slf4j.LoggerFactory
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

abstract class ShadowPluginLoader : PluginLoader, DelegateProvider, DI {

    private val mExecutorService = Executors.newSingleThreadExecutor()

    private val mLock = ReentrantLock()

    private lateinit var mPluginClassLoader: PluginClassLoader

    private lateinit var mPluginResources: Resources

    abstract fun getBusinessPluginActivitiesManager(): PluginActivitiesManager

    abstract fun getBusinessPluginServiceManager(): PluginServicesManager

    abstract fun getBusinessPluginReceiverManger(hostAppContext: Context): PluginReceiverManager

    private lateinit var mPluginApplication: ShadowApplication

    private lateinit var mPluginPackageManager: PluginPackageManager

    private val mPendingIntentManager: PendingIntentManager = PendingIntentManager(this)

    abstract val mExceptionReporter: Reporter

    /**
     * 插件将要使用的so的ABI，Loader会将其从apk中解压出来。
     * 如果插件不需要so，则返回""空字符串。
     */
    abstract val mAbi: String

    @Throws(LoadPluginException::class)
    override fun loadPlugin(hostAppContext: Context, installedPlugin: InstalledPlugin): ProgressFuture<RunningPlugin> {
        if (installedPlugin.pluginFile != null && installedPlugin.pluginFile.exists()) {
            val submit = mExecutorService.submit(Callable<RunningPlugin> {
                //todo cubershi 下面这些步骤可能可以并发起来.
                val pluginInfo = ParsePluginApkBloc.parse(installedPlugin.pluginFile.absolutePath, hostAppContext)
                val pluginPackageManager = PluginPackageManager(pluginInfo)
                val soDir = CopySoBloc.copySo(installedPlugin, mAbi)
                val pluginClassLoader = LoadApkBloc.loadPlugin(hostAppContext, installedPlugin, soDir)
                val resources = CreateResourceBloc.create(installedPlugin.pluginFile.absolutePath, hostAppContext)
                val shadowApplication =
                        CreateApplicationBloc.callPluginApplicationOnCreate(
                                pluginClassLoader,
                                pluginInfo.applicationClassName,
                                pluginPackageManager,
                                resources,
                                hostAppContext,
                                getBusinessPluginActivitiesManager(),
                                getBusinessPluginServiceManager(),
                                getBusinessPluginReceiverManger(hostAppContext).getActionAndReceiverByApplication(pluginInfo.applicationClassName),
                                mPendingIntentManager
                        )
                mLock.withLock {
                    getBusinessPluginActivitiesManager().addPluginApkInfo(pluginInfo)
                    getBusinessPluginServiceManager().addPluginApkInfo(pluginInfo)
                    mPluginClassLoader = pluginClassLoader
                    mPluginResources = resources
                    mPluginApplication = shadowApplication
                    mPluginPackageManager = pluginPackageManager
                }

                ShadowRunningPlugin(shadowApplication, installedPlugin, pluginInfo, getBusinessPluginActivitiesManager())
            })
            return ProgressFutureImpl(submit, null)
        } else if (installedPlugin.pluginFile != null)
            throw LoadPluginException("插件文件不存在.pluginFile==" + installedPlugin.pluginFile.absolutePath)
        else
            throw LoadPluginException("pluginFile==null")

    }

    override fun setPluginDisabled(installedPlugin: InstalledPlugin): Boolean {
        return false
    }

    override fun getHostActivityDelegate(aClass: Class<out HostActivityDelegator>): HostActivityDelegate {
        return ShadowActivityDelegate(this)
    }

    override fun getHostServiceDelegate(aClass: Class<out HostServiceDelegator>): HostServiceDelegate? {
        return ShadowServiceDelegate(this)
    }

    override fun inject(delegate: ShadowDelegate, partKey: String) {
        mLock.withLock {
            delegate.inject(mPluginPackageManager)
            delegate.inject(mPluginApplication)
            delegate.inject(mPluginClassLoader)
            delegate.inject(mPluginResources)
            delegate.inject(getBusinessPluginActivitiesManager())
            delegate.inject(getBusinessPluginServiceManager())
            delegate.inject(mPendingIntentManager)
            delegate.inject(mExceptionReporter)
        }
    }

    companion object {
        private val mLogger = LoggerFactory.getLogger(ShadowPluginLoader::class.java)
    }
}
