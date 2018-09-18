package com.tencent.shadow.loader.blocs

import android.content.Context
import com.tencent.hydevteam.common.progress.ProgressFuture
import com.tencent.hydevteam.common.progress.ProgressFutureImpl
import com.tencent.hydevteam.pluginframework.installedplugin.InstalledPlugin
import com.tencent.hydevteam.pluginframework.pluginloader.LoadPluginException
import com.tencent.hydevteam.pluginframework.pluginloader.RunningPlugin
import com.tencent.shadow.loader.ShadowRunningPlugin
import com.tencent.shadow.loader.infos.PluginParts
import com.tencent.shadow.loader.managers.CommonPluginPackageManager
import com.tencent.shadow.loader.managers.ComponentManager
import com.tencent.shadow.loader.managers.PluginPackageManager
import com.tencent.shadow.loader.managers.PluginReceiverManager
import java.util.concurrent.Callable
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
            pluginReceiverManager: PluginReceiverManager,
            lock: ReentrantLock,
            pluginPartsMap: MutableMap<String, PluginParts>,
            hostAppContext: Context,
            installedPlugin: InstalledPlugin
    ): ProgressFuture<RunningPlugin> {
        if (installedPlugin.pluginFile == null) {
            throw LoadPluginException("pluginFile==null")
        } else {
            val buildClassLoader = executorService.submit(Callable {
                val soDir = CopySoBloc.copySo(installedPlugin, abi)
                LoadApkBloc.loadPlugin(hostAppContext, installedPlugin, soDir)
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

                CreateApplicationBloc.callPluginApplicationOnCreate(
                        pluginClassLoader,
                        pluginInfo.applicationClassName,
                        pluginPackageManager,
                        resources,
                        hostAppContext,
                        componentManager,
                        pluginReceiverManager.getActionAndReceiverByApplication(pluginInfo.applicationClassName)
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
                lock.withLock {
                    componentManager.addPluginApkInfo(pluginInfo)
                }

                val shadowApplication = buildApplication.get()
                lock.withLock {
                    pluginPartsMap[pluginInfo.partKey] = PluginParts(
                            pluginPackageManager,
                            shadowApplication,
                            pluginClassLoader,
                            resources
                    )
                }
                ShadowRunningPlugin(shadowApplication, installedPlugin, pluginInfo, componentManager)
            })

            return ProgressFutureImpl(buildRunningPlugin, null)//todo cubershi:加载进度没有实现
        }
    }
}