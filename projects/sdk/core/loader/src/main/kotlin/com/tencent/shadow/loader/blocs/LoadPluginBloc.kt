package com.tencent.shadow.loader.blocs

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.tencent.hydevteam.common.progress.ProgressFuture
import com.tencent.hydevteam.common.progress.ProgressFutureImpl
import com.tencent.hydevteam.pluginframework.installedplugin.InstalledPlugin
import com.tencent.hydevteam.pluginframework.pluginloader.LoadPluginException
import com.tencent.hydevteam.pluginframework.pluginloader.RunningPlugin
import com.tencent.shadow.loader.ShadowRunningPlugin
import com.tencent.shadow.loader.classloaders.InterfaceClassLoader
import com.tencent.shadow.loader.infos.PluginParts
import com.tencent.shadow.loader.managers.CommonPluginPackageManager
import com.tencent.shadow.loader.managers.ComponentManager
import com.tencent.shadow.loader.managers.PluginBroadcastManager
import com.tencent.shadow.loader.managers.PluginPackageManager
import com.tencent.shadow.runtime.ShadowApplication
import java.util.concurrent.Callable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

object LoadPluginBloc {
    @Throws(LoadPluginException::class)
    fun loadPlugin(
            executorService: ExecutorService,
            abi: String,
            commonPluginPackageManager: CommonPluginPackageManager,
            componentManager: ComponentManager,
            pluginBroadcastManager: PluginBroadcastManager,
            lock: ReentrantLock,
            pluginPartsMap: MutableMap<String, PluginParts>,
            hostAppContext: Context,
            installedPlugin: InstalledPlugin,
            parentClassLoader: ClassLoader
    ): ProgressFuture<RunningPlugin> {
        if (installedPlugin.pluginFile == null) {
            throw LoadPluginException("pluginFile==null")
        } else {
            val buildClassLoader = executorService.submit(Callable {
                val soDir = CopySoBloc.copySo(hostAppContext,installedPlugin, abi)
                LoadApkBloc.loadPlugin(hostAppContext, installedPlugin, soDir, parentClassLoader)
            })

            val buildPackageManager = executorService.submit(Callable {
                val pluginInfo = ParsePluginApkBloc.parse(installedPlugin, hostAppContext)
                PluginPackageManager(commonPluginPackageManager, pluginInfo)
            })

            val buildResources = executorService.submit(Callable {
                CreateResourceBloc.create(installedPlugin.pluginFile.absolutePath, hostAppContext)
            })

            val buildApplication = executorService.submit(Callable {
                val pluginClassLoader = buildClassLoader.get()
                val pluginPackageManager = buildPackageManager.get()
                val resources = buildResources.get()
                val pluginInfo = pluginPackageManager.pluginInfo

                CreateApplicationBloc.createShadowApplication(
                        pluginClassLoader,
                        pluginInfo.applicationClassName,
                        pluginPackageManager,
                        resources,
                        hostAppContext,
                        componentManager,
                        pluginBroadcastManager.getBroadcastsByPartKey(pluginInfo.partKey)
                )
            })

            val buildRunningPlugin = executorService.submit(Callable<RunningPlugin> {
                if (installedPlugin.pluginFile.exists().not()) {
                    throw LoadPluginException("插件文件不存在.pluginFile==" + installedPlugin.pluginFile.absolutePath)
                }
                val pluginPackageManager = buildPackageManager.get()
                val pluginClassLoader = buildClassLoader.get()
                val resources = buildResources.get()
                val pluginInfo = pluginPackageManager.pluginInfo
                val shadowApplication = buildApplication.get()
                lock.withLock {
                    componentManager.addPluginApkInfo(pluginInfo)
                    pluginPartsMap[pluginInfo.partKey] = PluginParts(
                            pluginPackageManager,
                            shadowApplication,
                            pluginClassLoader,
                            resources
                    )
                }

                fun callApplicationOnCreate(shadowApplication: ShadowApplication) {
                    val uiHandler = Handler(Looper.getMainLooper())
                    val waitUiLock = CountDownLatch(1)
                    uiHandler.post {
                        shadowApplication.onCreate()
                        waitUiLock.countDown()
                    }
                    waitUiLock.await()
                }

                callApplicationOnCreate(shadowApplication)

                ShadowRunningPlugin(shadowApplication, installedPlugin, pluginInfo, componentManager)
            })

            return ProgressFutureImpl(buildRunningPlugin, null)//todo cubershi:加载进度没有实现
        }
    }

    fun loadInterface(
            executorService: ExecutorService,
            abi: String,
            hostAppContext: Context,
            comInterface: InterfaceClassLoader,
            installedPlugin: InstalledPlugin
    ): ProgressFuture<RunningPlugin> {
        if (installedPlugin.pluginFile == null) {
            throw LoadPluginException("pluginFile==null")
        } else {

            val buildRunningPlugin = executorService.submit(Callable<RunningPlugin> {
                val soDir = CopySoBloc.copySo(hostAppContext, installedPlugin, abi)
                val pluginLoaderClassLoader = LoadApkBloc::class.java.classLoader
                val hostAppParentClassLoader = pluginLoaderClassLoader.parent.parent
                val pluginClassLoader = LoadApkBloc.loadPlugin(hostAppContext, installedPlugin, soDir, hostAppParentClassLoader)

                comInterface.addInterfaceClassLoader(pluginClassLoader)

                FakeRunningPlugin()
            })

            return ProgressFutureImpl(buildRunningPlugin, null)

        }
    }


    class FakeRunningPlugin: RunningPlugin {
        override fun startLauncherActivity(p0: Intent?): ProgressFuture<*>? {
            return null
        }

        override fun unload() {
        }

        override fun startInitActivity(p0: Intent?): ProgressFuture<*>? {
           return null
        }
    }
}