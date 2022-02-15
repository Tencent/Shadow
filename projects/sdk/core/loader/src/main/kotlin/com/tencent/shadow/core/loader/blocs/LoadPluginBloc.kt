/*
 * Tencent is pleased to support the open source community by making Tencent Shadow available.
 * Copyright (C) 2019 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tencent.shadow.core.loader.blocs

import android.content.Context
import com.tencent.shadow.core.common.InstalledApk
import com.tencent.shadow.core.load_parameters.LoadParameters
import com.tencent.shadow.core.loader.exceptions.LoadPluginException
import com.tencent.shadow.core.loader.infos.PluginParts
import com.tencent.shadow.core.loader.managers.ComponentManager
import com.tencent.shadow.core.loader.managers.PluginPackageManagerImpl
import com.tencent.shadow.core.runtime.PluginPartInfo
import com.tencent.shadow.core.runtime.PluginPartInfoManager
import com.tencent.shadow.core.runtime.ShadowAppComponentFactory
import java.io.File
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

object LoadPluginBloc {
    @Throws(LoadPluginException::class)
    fun loadPlugin(
        executorService: ExecutorService,
        componentManager: ComponentManager,
        lock: ReentrantLock,
        pluginPartsMap: MutableMap<String, PluginParts>,
        hostAppContext: Context,
        installedApk: InstalledApk,
        loadParameters: LoadParameters
    ): Future<*> {
        if (installedApk.apkFilePath == null) {
            throw LoadPluginException("apkFilePath==null")
        } else {
            val buildClassLoader = executorService.submit(Callable {
                lock.withLock {
                    LoadApkBloc.loadPlugin(installedApk, loadParameters, pluginPartsMap)
                }
            })

            val buildPluginManifest = executorService.submit(Callable {
                val pluginClassLoader = buildClassLoader.get()
                val pluginManifest = pluginClassLoader.loadPluginManifest()
                CheckPackageNameBloc.check(pluginManifest, hostAppContext)
                pluginManifest
            })

            val buildPluginApplicationInfo = executorService.submit(Callable {
                val pluginManifest = buildPluginManifest.get()
                val pluginApplicationInfo = CreatePluginApplicationInfoBloc.create(
                    installedApk,
                    loadParameters,
                    pluginManifest,
                    hostAppContext
                )
                pluginApplicationInfo
            })

            val buildPackageManager = executorService.submit(Callable {
                val pluginApplicationInfo = buildPluginApplicationInfo.get()
                val hostPackageManager = hostAppContext.packageManager
                PluginPackageManagerImpl(
                    pluginApplicationInfo,
                    installedApk.apkFilePath,
                    componentManager,
                    hostPackageManager,
                )
            })

            val buildResources = executorService.submit(Callable {
                CreateResourceBloc.create(installedApk.apkFilePath, hostAppContext)
            })

            val buildAppComponentFactory = executorService.submit(Callable {
                val pluginClassLoader = buildClassLoader.get()
                val pluginManifest = buildPluginManifest.get()
                val appComponentFactory = pluginManifest.appComponentFactory
                if (appComponentFactory != null) {
                    val clazz = pluginClassLoader.loadClass(appComponentFactory)
                    ShadowAppComponentFactory::class.java.cast(clazz.newInstance())
                } else ShadowAppComponentFactory()
            })

            val buildApplication = executorService.submit(Callable {
                val pluginClassLoader = buildClassLoader.get()
                val resources = buildResources.get()
                val appComponentFactory = buildAppComponentFactory.get()
                val pluginManifest = buildPluginManifest.get()
                val pluginApplicationInfo = buildPluginApplicationInfo.get()

                CreateApplicationBloc.createShadowApplication(
                    pluginClassLoader,
                    loadParameters,
                    pluginManifest,
                    resources,
                    hostAppContext,
                    componentManager,
                    pluginApplicationInfo,
                    appComponentFactory
                )
            })

            val buildRunningPlugin = executorService.submit {
                if (File(installedApk.apkFilePath).exists().not()) {
                    throw LoadPluginException("插件文件不存在.pluginFile==" + installedApk.apkFilePath)
                }
                val pluginPackageManager = buildPackageManager.get()
                val pluginClassLoader = buildClassLoader.get()
                val resources = buildResources.get()
                val shadowApplication = buildApplication.get()
                val appComponentFactory = buildAppComponentFactory.get()
                val pluginManifest = buildPluginManifest.get()
                lock.withLock {
                    componentManager.addPluginApkInfo(
                        pluginManifest,
                        loadParameters,
                        installedApk.apkFilePath,
                    )
                    pluginPartsMap[loadParameters.partKey] = PluginParts(
                        appComponentFactory,
                        shadowApplication,
                        pluginClassLoader,
                        resources,
                        pluginPackageManager
                    )
                    PluginPartInfoManager.addPluginInfo(
                        pluginClassLoader, PluginPartInfo(
                            shadowApplication, resources,
                            pluginClassLoader, pluginPackageManager
                        )
                    )
                }
            }

            return buildRunningPlugin
        }
    }


}