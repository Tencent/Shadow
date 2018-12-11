package com.tencent.shadow.core.loader.blocs

import android.content.Context
import com.tencent.shadow.core.loader.classloaders.InterfaceClassLoader
import com.tencent.shadow.core.loader.exceptions.LoadPluginException
import com.tencent.shadow.core.loader.infos.InstalledPlugin
import com.tencent.shadow.core.loader.infos.PluginParts
import com.tencent.shadow.core.loader.managers.CommonPluginPackageManager
import com.tencent.shadow.core.loader.managers.ComponentManager
import com.tencent.shadow.core.loader.managers.PluginBroadcastManager
import com.tencent.shadow.core.loader.managers.PluginPackageManager
import com.tencent.shadow.runtime.remoteview.ShadowRemoteViewCreatorProvider
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
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
            parentClassLoader: ClassLoader,
            remoteViewCreatorProvider: ShadowRemoteViewCreatorProvider?
    ): Future<*> {
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
                        pluginBroadcastManager.getBroadcastsByPartKey(pluginInfo.partKey),
                        remoteViewCreatorProvider
                )
            })

            val buildRunningPlugin = executorService.submit {
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
            }

            return buildRunningPlugin
        }
    }

    fun loadInterface(
            executorService: ExecutorService,
            abi: String,
            hostAppContext: Context,
            comInterface: InterfaceClassLoader,
            installedPlugin: InstalledPlugin
    ): Future<*> {
        if (installedPlugin.pluginFile == null) {
            throw LoadPluginException("pluginFile==null")
        } else {

            return executorService.submit {
                val soDir = CopySoBloc.copySo(hostAppContext, installedPlugin, abi)
                val pluginLoaderClassLoader = LoadApkBloc::class.java.classLoader
                val hostAppParentClassLoader = pluginLoaderClassLoader.parent.parent
                val pluginClassLoader = LoadApkBloc.loadPlugin(hostAppContext, installedPlugin, soDir, hostAppParentClassLoader)

                comInterface.addInterfaceClassLoader(pluginClassLoader)
            }

        }
    }


}