package com.tencent.shadow.loader.blocs

import android.content.Context
import android.content.pm.PackageManager.*
import com.tencent.shadow.loader.exceptions.ParsePluginApkException
import com.tencent.shadow.loader.infos.PluginActivityInfo
import com.tencent.shadow.loader.infos.PluginInfo
import com.tencent.shadow.loader.infos.PluginServiceInfo

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
        val packageArchiveInfo = packageManager.getPackageArchiveInfo(
                archiveFilePath,
                GET_ACTIVITIES or GET_META_DATA or GET_SERVICES
        )
        val pluginInfo = PluginInfo(
                packageArchiveInfo.applicationInfo.packageName
                , packageArchiveInfo.applicationInfo.className
        )
        packageArchiveInfo.activities.forEach {
            pluginInfo.putActivityInfo(PluginActivityInfo(it.name, it.themeResource, it))
        }
        packageArchiveInfo.services.forEach { pluginInfo.putServiceInfo(PluginServiceInfo(it.name)) }
        pluginInfo.metaData = packageArchiveInfo.applicationInfo.metaData
        pluginInfo.versionCode = packageArchiveInfo.versionCode;
        pluginInfo.versionName = packageArchiveInfo.versionName;
        return pluginInfo
    }
}
