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

import com.tencent.shadow.core.common.InstalledApk
import com.tencent.shadow.core.common.Logger
import com.tencent.shadow.core.load_parameters.LoadParameters
import com.tencent.shadow.core.loader.classloaders.CombineClassLoader
import com.tencent.shadow.core.loader.classloaders.PluginClassLoader
import com.tencent.shadow.core.loader.exceptions.LoadApkException
import com.tencent.shadow.core.loader.infos.PluginParts
import java.io.File

/**
 * 加载插件到ClassLoader中
 *
 * @author cubershi
 */
object LoadApkBloc {

    /**
     * 加载插件到ClassLoader中.
     *
     * @param installedPlugin    已安装（PluginManager已经下载解包）的插件
     * @return 加载了插件的ClassLoader
     */
    @Throws(LoadApkException::class)
    fun loadPlugin(installedApk: InstalledApk, loadParameters: LoadParameters, pluginPartsMap: MutableMap<String, PluginParts>): PluginClassLoader {
        val apk = File(installedApk.apkFilePath)
        val odexDir = if (installedApk.oDexPath == null) null else File(installedApk.oDexPath)
        val dependsOn = loadParameters.dependsOn
        //Logger类一定打包在宿主中，所在的classLoader即为加载宿主的classLoader
        val hostClassLoader: ClassLoader = Logger::class.java.classLoader!!
        val hostParentClassLoader = hostClassLoader.parent
        if (dependsOn == null || dependsOn.isEmpty()) {
            return PluginClassLoader(
                    apk.absolutePath,
                    odexDir,
                    installedApk.libraryPath,
                    hostClassLoader,
                    hostParentClassLoader,
                    loadParameters.hostWhiteList
            )
        } else if (dependsOn.size == 1) {
            val partKey = dependsOn[0]
            val pluginParts = pluginPartsMap[partKey]
            if (pluginParts == null) {
                throw LoadApkException("加载" + loadParameters.partKey + "时它的依赖" + partKey + "还没有加载")
            } else {
                return PluginClassLoader(
                        apk.absolutePath,
                        odexDir,
                        installedApk.libraryPath,
                        pluginParts.classLoader,
                        null,
                        loadParameters.hostWhiteList
                )
            }
        } else {
            val dependsOnClassLoaders = dependsOn.map {
                val pluginParts = pluginPartsMap[it]
                if (pluginParts == null) {
                    throw LoadApkException("加载" + loadParameters.partKey + "时它的依赖" + it + "还没有加载")
                } else {
                    pluginParts.classLoader
                }
            }.toTypedArray()
            val combineClassLoader = CombineClassLoader(dependsOnClassLoaders, hostParentClassLoader)
            return PluginClassLoader(
                    apk.absolutePath,
                    odexDir,
                    installedApk.libraryPath,
                    combineClassLoader,
                    null,
                    loadParameters.hostWhiteList
            )
        }
    }
}
