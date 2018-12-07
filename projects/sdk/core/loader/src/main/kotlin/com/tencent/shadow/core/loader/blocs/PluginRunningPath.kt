package com.tencent.shadow.core.loader.blocs

import android.content.Context
import android.text.TextUtils

import com.tencent.hydevteam.common.annotation.API

import java.io.File

/**
 * PluginManager和PluginLoader用这个类来约定插件运行时的各个目录的路径。
 * 只有双方都遵循使用这个类决定的路径，才能完成由PluginManager生成odex供PluginLoader直接使用的功能（预加载）。
 *
 * @author cubershi
 */
@API
object PluginRunningPath {

    val PRELOAD_SUFFIX = ".preload"

    // 插件在宿主data目录中的根目录
    @API
    fun getPluginRootDir(context: Context, pluginPkgName: String): String {
        val dir = (context.getDir("HYDTPlugins", Context.MODE_PRIVATE).absolutePath + File.separator
                + pluginPkgName + File.separator)
        val file = File(dir)
        if (!file.exists()) {
            file.mkdirs()
        }
        return dir
    }

    // 存放插件optDex的目录
    @API
    fun getPluginOptDexDir(context: Context, pluginPkgName: String, versionName: String): File {
        var optDexDir = "optDex"
        if (!TextUtils.isEmpty(versionName)) {
            optDexDir = optDexDir + "_" + versionName
        }
        val dir = getPluginRootDir(context, pluginPkgName) + optDexDir + File.separator
        val file = File(dir)
        return file
    }

    // 存放插件lib的目录
    @API
    fun getPluginLibDir(context: Context, pluginPkgName: String, versionName: String): File {
        var libDir = "lib"
        if (!TextUtils.isEmpty(versionName)) {
            libDir = libDir + "_" + versionName
        }
        val dir = getPluginRootDir(context, pluginPkgName) + libDir + File.separator
        val file = File(dir)
        return file
    }

    // 存放插件data的目录
    @API
    fun getPluginDataDir(context: Context, pluginPkgName: String): String {
        val dir = getPluginRootDir(context, pluginPkgName) + "data" + File.separator
        val file = File(dir)
        if (!file.exists()) {
            file.mkdirs()
        }
        return dir
    }

    // 存放插件data的目录
    @API
    fun getPluginTmpDir(context: Context, pluginPkgName: String, versionName: String): String {
        var tmpDir = "tmp"
        if (!TextUtils.isEmpty(versionName)) {
            tmpDir = tmpDir + "_" + versionName
        }
        val dir = getPluginRootDir(context, pluginPkgName) + tmpDir + File.separator
        val file = File(dir)
        if (!file.exists()) {
            file.mkdirs()
        }
        return dir
    }

    // 是否已经存在创建odex的文件，标示是否odex过程已经完成了
    @API
    fun isPreOdexFileExists(context: Context, pluginPkgName: String, versionName: String, apkFileName: String): Boolean {
        val path = getPluginOptDexDir(context, pluginPkgName, versionName)
        val flagFileName = apkFileName + com.tencent.hydevteam.pluginframework.installedplugin.PluginRunningPath.PRELOAD_SUFFIX
        val file = File(path, flagFileName)
        return file.exists()
    }

}
