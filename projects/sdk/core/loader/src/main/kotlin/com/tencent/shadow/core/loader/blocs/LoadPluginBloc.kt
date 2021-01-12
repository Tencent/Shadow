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
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import com.tencent.shadow.core.common.InstalledApk
import com.tencent.shadow.core.load_parameters.LoadParameters
import com.tencent.shadow.core.loader.exceptions.LoadPluginException
import com.tencent.shadow.core.loader.infos.PluginParts
import com.tencent.shadow.core.loader.managers.ComponentManager
import com.tencent.shadow.core.loader.managers.PluginPackageManagerImpl
import com.tencent.shadow.core.runtime.PluginPartInfo
import com.tencent.shadow.core.runtime.PluginPartInfoManager
import com.tencent.shadow.core.runtime.ShadowAppComponentFactory
import com.tencent.shadow.core.runtime.ShadowContext
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
            pluginPackageInfoSet: MutableSet<PackageInfo>,
            allPluginPackageInfo: () -> (Array<PackageInfo>),
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

            val getPackageInfo = executorService.submit(Callable {
                val archiveFilePath = installedApk.apkFilePath
                val packageManager = hostAppContext.packageManager

                val packageArchiveInfo = packageManager.getPackageArchiveInfo(
                        archiveFilePath,
                        PackageManager.GET_ACTIVITIES
                                or PackageManager.GET_META_DATA
                                or PackageManager.GET_SERVICES
                                or PackageManager.GET_PROVIDERS
                                or PackageManager.GET_SIGNATURES
                )
                        ?: throw NullPointerException("getPackageArchiveInfo return null.archiveFilePath==$archiveFilePath")

                val tempContext = ShadowContext(hostAppContext, 0).apply {
                    setBusinessName(loadParameters.businessName)
                }
                val dataDir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    tempContext.dataDir
                } else {
                    File(tempContext.filesDir, "dataDir")
                }
                dataDir.mkdirs()

                packageArchiveInfo.applicationInfo.nativeLibraryDir = installedApk.libraryPath
                packageArchiveInfo.applicationInfo.dataDir = dataDir.absolutePath
                packageArchiveInfo.applicationInfo.processName = hostAppContext.applicationInfo.processName
                packageArchiveInfo.applicationInfo.uid = hostAppContext.applicationInfo.uid

                lock.withLock { pluginPackageInfoSet.add(packageArchiveInfo) }
                packageArchiveInfo
            })

            val buildPluginInfo = executorService.submit(Callable {
                val packageInfo = getPackageInfo.get()
                ParsePluginApkBloc.parse(packageInfo, loadParameters, hostAppContext)
            })

            val buildPackageManager = executorService.submit(Callable {
                val packageInfo = getPackageInfo.get()
                val hostPackageManager = hostAppContext.packageManager
                PluginPackageManagerImpl(hostPackageManager, packageInfo, allPluginPackageInfo)
            })

            val buildResources = executorService.submit(Callable {
                val packageInfo = getPackageInfo.get()
                CreateResourceBloc.create(packageInfo, installedApk.apkFilePath, hostAppContext)
            })

            val buildAppComponentFactory = executorService.submit(Callable<ShadowAppComponentFactory> {
                val pluginClassLoader = buildClassLoader.get()
                val pluginInfo = buildPluginInfo.get()
                if (pluginInfo.appComponentFactory != null) {
                    val clazz = pluginClassLoader.loadClass(pluginInfo.appComponentFactory)
                    ShadowAppComponentFactory::class.java.cast(clazz.newInstance())
                } else ShadowAppComponentFactory()
            })

            val buildApplication = executorService.submit(Callable {
                val pluginClassLoader = buildClassLoader.get()
                val resources = buildResources.get()
                val pluginInfo = buildPluginInfo.get()
                val packageInfo = getPackageInfo.get()
                val appComponentFactory = buildAppComponentFactory.get()

                CreateApplicationBloc.createShadowApplication(
                        pluginClassLoader,
                        pluginInfo,
                        resources,
                        hostAppContext,
                        componentManager,
                        packageInfo.applicationInfo,
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
                val pluginInfo = buildPluginInfo.get()
                val shadowApplication = buildApplication.get()
                val appComponentFactory = buildAppComponentFactory.get()
                lock.withLock {
                    componentManager.addPluginApkInfo(pluginInfo)
                    pluginPartsMap[pluginInfo.partKey] = PluginParts(
                            appComponentFactory,
                            shadowApplication,
                            pluginClassLoader,
                            resources,
                            pluginInfo.businessName,
                            pluginPackageManager
                    )
                    PluginPartInfoManager.addPluginInfo(pluginClassLoader, PluginPartInfo(shadowApplication, resources,
                            pluginClassLoader, pluginPackageManager))
                }
            }

            return buildRunningPlugin
        }
    }


}