package com.tencent.shadow.loader.blocs

import android.content.Context
import com.tencent.shadow.loader.classloaders.PluginClassLoader
import com.tencent.shadow.loader.exceptions.LoadApkException
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
     * @param apk    插件apk
     * @return 加载了插件的ClassLoader
     */
    @Throws(LoadApkException::class)
    fun loadPlugin(hostAppContext:Context, apk: File): PluginClassLoader {
        val pluginLoaderClassLoader = LoadApkBloc::class.java.classLoader
        val hostAppClassLoader = pluginLoaderClassLoader.parent
        val odexDir = File(apk.parent, apk.name + "_odex")
        val libDir = File(apk.parent, apk.name + "_lib")
        prepareDirs(odexDir, libDir)
        return PluginClassLoader(
                hostAppContext,
                apk.absolutePath,
                odexDir.absolutePath,
                libDir.absolutePath,
                hostAppClassLoader
        )
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
