package com.tencent.shadow.core.loader.blocs

import android.content.Context
import com.tencent.shadow.core.loader.classloaders.CombineClassLoader
import com.tencent.shadow.core.loader.classloaders.PluginClassLoader
import com.tencent.shadow.core.loader.exceptions.LoadApkException
import com.tencent.shadow.core.loader.infos.InstalledPlugin
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
    fun loadInterface(hostAppContext: Context, installedPlugin: InstalledPlugin, soDir: File, parentClassLoader: ClassLoader): PluginClassLoader {
        val apk = installedPlugin.pluginFile
        val odexDir = PluginRunningPath.getPluginOptDexDir(hostAppContext, installedPlugin.partKey, installedPlugin.pluginVersionForPluginLoaderManage)
        prepareDirs(odexDir, soDir)
        return PluginClassLoader(
                hostAppContext,
                apk.absolutePath,
                odexDir.absolutePath,
                soDir.absolutePath,
                parentClassLoader
        )
    }

    /**
     * 加载插件到ClassLoader中.
     *
     * @param installedPlugin    已安装（PluginManager已经下载解包）的插件
     * @return 加载了插件的ClassLoader
     */
    @Throws(LoadApkException::class)
    fun loadPlugin(hostAppContext: Context, installedPlugin: InstalledPlugin, soDir: File, parentClassLoader: ClassLoader, pluginPartsMap: MutableMap<String, PluginParts>): PluginClassLoader {
        val apk = installedPlugin.pluginFile
        val odexDir = PluginRunningPath.getPluginOptDexDir(hostAppContext, installedPlugin.partKey, installedPlugin.pluginVersionForPluginLoaderManage)
        prepareDirs(odexDir, soDir)

        val dependsOn = installedPlugin.dependsOn
        if (dependsOn == null) {
            return PluginClassLoader(
                    hostAppContext,
                    apk.absolutePath,
                    odexDir.absolutePath,
                    soDir.absolutePath,
                    parentClassLoader
            )
        } else if (dependsOn.size == 1) {
            val partKey = dependsOn[0]
            val pluginParts = pluginPartsMap[partKey]
            if (pluginParts == null) {
                throw LoadApkException("加载" + installedPlugin.partKey + "时它的依赖" + partKey + "还没有加载")
            } else {
                return PluginClassLoader(
                        hostAppContext,
                        apk.absolutePath,
                        odexDir.absolutePath,
                        soDir.absolutePath,
                        pluginParts.classLoader
                )
            }
        } else {
            val dependsOnClassLoaders = dependsOn.map {
                val pluginParts = pluginPartsMap[it]
                if (pluginParts == null) {
                    throw LoadApkException("加载" + installedPlugin.partKey + "时它的依赖" + it + "还没有加载")
                } else {
                    pluginParts.classLoader
                }
            }.toTypedArray()
            val combineClassLoader = CombineClassLoader(dependsOnClassLoaders, parentClassLoader)
            return PluginClassLoader(
                    hostAppContext,
                    apk.absolutePath,
                    odexDir.absolutePath,
                    soDir.absolutePath,
                    combineClassLoader
            )
        }
    }

    @Throws(LoadApkException::class)
    private fun prepareDirs(odexDir: File, libDir: File) {
        if (odexDir.exists() && !odexDir.isDirectory) {
            throw LoadApkException("odexDir目标路径" + odexDir.absolutePath
                    + "已被其他文件占用")
        } else if (!odexDir.exists()) {
            val success = odexDir.mkdir()
            if (!success) {
                throw LoadApkException("odexDir目标路径" + odexDir.absolutePath
                        + "创建目录失败")
            }
        }

        if (!libDir.exists()) {
            if (!libDir.mkdirs()) {
                throw LoadApkException("libDir目标路径" + libDir.absolutePath
                        + "创建目录失败")
            }
        }
    }
}
