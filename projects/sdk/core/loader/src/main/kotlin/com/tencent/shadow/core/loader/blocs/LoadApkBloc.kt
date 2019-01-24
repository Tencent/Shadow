package com.tencent.shadow.core.loader.blocs

import android.content.Context
import com.tencent.shadow.core.common.InstalledApk
import com.tencent.shadow.core.loader.LoadParameters
import com.tencent.shadow.core.loader.classloaders.BootPluginClassLoader
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
    fun loadPlugin(hostAppContext: Context, installedApk: InstalledApk, loadParameters: LoadParameters, parentClassLoader: ClassLoader, pluginPartsMap: MutableMap<String, PluginParts>): PluginClassLoader {
        val apk = File(installedApk.apkFilePath)
        val odexDir = if (installedApk.oDexPath == null) null else File(installedApk.oDexPath)
        val dependsOn = loadParameters.dependsOn
        if (dependsOn == null || dependsOn.isEmpty()) {
            return BootPluginClassLoader(
                    hostAppContext,
                    apk.absolutePath,
                    odexDir,
                    installedApk.libraryPath,
                    parentClassLoader
            )
        } else if (dependsOn.size == 1) {
            val partKey = dependsOn[0]
            val pluginParts = pluginPartsMap[partKey]
            if (pluginParts == null) {
                throw LoadApkException("加载" + loadParameters.partKey + "时它的依赖" + partKey + "还没有加载")
            } else {
                return PluginClassLoader(
                        hostAppContext,
                        apk.absolutePath,
                        odexDir,
                        installedApk.libraryPath,
                        pluginParts.classLoader
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
            val combineClassLoader = CombineClassLoader(dependsOnClassLoaders, parentClassLoader)
            return PluginClassLoader(
                    hostAppContext,
                    apk.absolutePath,
                    odexDir,
                    installedApk.libraryPath,
                    combineClassLoader
            )
        }
    }
}
