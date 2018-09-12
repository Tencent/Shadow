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
import com.tencent.shadow.loader.delegates.ServiceContainerReuseDelegate
import com.tencent.shadow.loader.delegates.ShadowActivityDelegate
import com.tencent.shadow.loader.delegates.ShadowDelegate
import com.tencent.shadow.loader.managers.CommonPluginPackageManager
import com.tencent.shadow.loader.managers.ComponentManager
import com.tencent.shadow.loader.managers.PluginPackageManager
import com.tencent.shadow.loader.managers.PluginReceiverManager
import com.tencent.shadow.runtime.ShadowApplication
import org.slf4j.LoggerFactory
import java.util.concurrent.Callable
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
    abstract fun getBusinessPluginReceiverManger(hostAppContext: Context): PluginReceiverManager

    abstract val mExceptionReporter: Reporter

    private val mCommonPluginPackageManager = CommonPluginPackageManager()

    /**
     * 插件将要使用的so的ABI，Loader会将其从apk中解压出来。
     * 如果插件不需要so，则返回""空字符串。
     */
    abstract val mAbi: String

    @Throws(LoadPluginException::class)
    override fun loadPlugin(hostAppContext: Context, installedPlugin: InstalledPlugin): ProgressFuture<RunningPlugin> {
        if (installedPlugin.pluginFile == null) {
            throw LoadPluginException("pluginFile==null")
        } else {
            val buildClassLoader = mExecutorService.submit(Callable {
                val soDir = CopySoBloc.copySo(installedPlugin, mAbi)
                LoadApkBloc.loadPlugin(hostAppContext, installedPlugin, soDir)
            })

            val buildPackageManager = mExecutorService.submit(Callable {
                val pluginInfo = ParsePluginApkBloc.parse(installedPlugin, hostAppContext)
                PluginPackageManager(mCommonPluginPackageManager, pluginInfo)
            })

            val buildResources = mExecutorService.submit(Callable {
                CreateResourceBloc.create(installedPlugin.pluginFile.absolutePath, hostAppContext)
            })

            val buildApplication = mExecutorService.submit(Callable {
                val pluginClassLoader = buildClassLoader.get()
                val pluginPackageManager = buildPackageManager.get()
                val resources = buildResources.get()
                val pluginInfo = pluginPackageManager.pluginInfo

                CreateApplicationBloc.callPluginApplicationOnCreate(
                        pluginClassLoader,
                        pluginInfo.applicationClassName,
                        pluginPackageManager,
                        resources,
                        hostAppContext,
                        mComponentManager,
                        getBusinessPluginReceiverManger(hostAppContext).getActionAndReceiverByApplication(pluginInfo.applicationClassName)
                )
            })

            val buildRunningPlugin = mExecutorService.submit(Callable<RunningPlugin> {
                val pluginPackageManager = buildPackageManager.get()
                val pluginClassLoader = buildClassLoader.get()
                val resources = buildResources.get()
                val shadowApplication = buildApplication.get()
                val pluginInfo = pluginPackageManager.pluginInfo

                mLock.withLock {
                    mComponentManager.addPluginApkInfo(pluginInfo)
                    mPluginPartsMap[pluginInfo.partKey] = PluginParts(
                            pluginPackageManager,
                            shadowApplication,
                            pluginClassLoader,
                            resources
                    )
                }
                ShadowRunningPlugin(shadowApplication, installedPlugin, pluginInfo, mComponentManager)
            })

            return ProgressFutureImpl(buildRunningPlugin, null)
        }
    }

    override fun setPluginDisabled(installedPlugin: InstalledPlugin): Boolean {
        return false
    }

    override fun getHostActivityDelegate(aClass: Class<out HostActivityDelegator>): HostActivityDelegate {
        return ShadowActivityDelegate(this)
    }

    override fun getHostServiceDelegate(aClass: Class<out HostServiceDelegator>): HostServiceDelegate? {
        return ServiceContainerReuseDelegate(this)
    }

    override fun inject(delegate: ShadowDelegate, partKey: String) {
        mLock.withLock {
            val pluginParts = mPluginPartsMap[partKey]!!
            delegate.inject(pluginParts.packageManager)
            delegate.inject(pluginParts.application)
            delegate.inject(pluginParts.classLoader)
            delegate.inject(pluginParts.resources)
            delegate.inject(mExceptionReporter)
            delegate.inject(mComponentManager)
        }
    }

    companion object {
        private val mLogger = LoggerFactory.getLogger(ShadowPluginLoader::class.java)
    }
}

class PluginParts(val packageManager: PluginPackageManager,
                  val application: ShadowApplication,
                  val classLoader: PluginClassLoader,
                  val resources: Resources)