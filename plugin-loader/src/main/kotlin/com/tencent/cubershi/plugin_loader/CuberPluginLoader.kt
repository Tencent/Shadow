package com.tencent.cubershi.plugin_loader

import android.content.Context
import android.content.res.Resources
import com.tencent.cubershi.mock_interface.MockApplication
import com.tencent.cubershi.plugin_loader.blocs.*
import com.tencent.cubershi.plugin_loader.delegates.DefaultHostActivityDelegate
import com.tencent.cubershi.plugin_loader.managers.PluginActivitiesManager
import com.tencent.cubershi.plugin_loader.test.FakeRunningPlugin
import com.tencent.hydevteam.common.progress.ProgressFuture
import com.tencent.hydevteam.common.progress.ProgressFutureImpl
import com.tencent.hydevteam.pluginframework.installedplugin.InstalledPlugin
import com.tencent.hydevteam.pluginframework.plugincontainer.*
import com.tencent.hydevteam.pluginframework.pluginloader.LoadPluginException
import com.tencent.hydevteam.pluginframework.pluginloader.PluginLoader
import com.tencent.hydevteam.pluginframework.pluginloader.RunningPlugin
import dalvik.system.DexClassLoader
import org.slf4j.LoggerFactory
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class CuberPluginLoader : PluginLoader, DelegateProvider {

    private val mExecutorService = Executors.newSingleThreadExecutor()

    private val mLock = ReentrantLock()

    private lateinit var mPluginClassLoader: DexClassLoader

    private lateinit var mPluginResources: Resources

    private val mPluginActivitiesManager = PluginActivitiesManager()

    private lateinit var mPluginApplication: MockApplication

    @Throws(LoadPluginException::class)
    override fun loadPlugin(hostAppContext: Context, installedPlugin: InstalledPlugin): ProgressFuture<RunningPlugin> {
        if (mLogger.isInfoEnabled) {
            mLogger.info("loadPlugin installedPlugin=={}", installedPlugin)
        }
        if (installedPlugin.pluginFile != null && installedPlugin.pluginFile.exists()) {
            val submit = mExecutorService.submit(Callable<RunningPlugin> {
                //todo cubershi 下面这些步骤可能可以并发起来.
                val pluginInfo = ParsePluginApkBloc.parse(installedPlugin.pluginFile.absolutePath, hostAppContext)
                CopySoBloc.copySo(installedPlugin.pluginFile, "armeabi")
                val pluginClassLoader = LoadApkBloc.loadPlugin(installedPlugin.pluginFile)
                val resources = CreateResourceBloc.create(installedPlugin.pluginFile.absolutePath, hostAppContext)
                val mockApplication = CreateApplicationBloc.callPluginApplicationOnCreate(pluginClassLoader, pluginInfo.applicationClassName, resources)
                mockApplication.hostApplicationContext = hostAppContext

                mLock.withLock {
                    mPluginActivitiesManager.addPluginApkInfo(pluginInfo)
                    mPluginClassLoader = pluginClassLoader
                    mPluginResources = resources
                    mPluginApplication = mockApplication
                }

                FakeRunningPlugin(mockApplication, installedPlugin, pluginInfo)
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
        //todo cubershi 这里返回的DefaultHostActivityDelegate直接绑定了mPluginClassLoader限制了多插件的实现
        mLock.withLock {
            return DefaultHostActivityDelegate(
                    mPluginApplication,
                    mPluginClassLoader,
                    mPluginResources,
                    mPluginActivitiesManager
            )
        }
    }

    override fun getHostServiceDelegate(aClass: Class<out HostServiceDelegator>): HostServiceDelegate? {
        return null
    }

    companion object {
        private val mLogger = LoggerFactory.getLogger(CuberPluginLoader::class.java)
    }
}
