package com.tencent.cubershi.plugin_loader.blocs

import android.content.Context
import android.content.pm.PackageManager.GET_ACTIVITIES
import com.tencent.cubershi.plugin_loader.exceptions.ParsePluginApkException
import com.tencent.cubershi.plugin_loader.infos.PluginActivityInfo
import com.tencent.cubershi.plugin_loader.infos.PluginInfo

/**
 * 解析插件apk逻辑
 *
 * @author cubershi
 */
object ParsePluginApkBloc {
    /**
     * 解析插件apk
     *
     * @param pluginFile 插件apk文件
     * @return 解析信息
     * @throws ParsePluginApkException 解析失败时抛出
     */
    @Throws(ParsePluginApkException::class)
    fun parse(archiveFilePath: String, hostAppContext: Context): PluginInfo {
        val packageManager = hostAppContext.packageManager
        val packageArchiveInfo = packageManager.getPackageArchiveInfo(archiveFilePath, GET_ACTIVITIES)
        val pluginInfo = PluginInfo(
                packageArchiveInfo.applicationInfo.packageName,
                packageArchiveInfo.applicationInfo.className
        )
        packageArchiveInfo.activities.forEach {
            pluginInfo.putActivityInfo(PluginActivityInfo(it.name))
        }
        return pluginInfo
    }
}
