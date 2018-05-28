package com.tencent.cubershi.plugin_loader.blocs

import com.tencent.cubershi.plugin_loader.exceptions.ParsePluginApkException
import com.tencent.cubershi.plugin_loader.infos.ApkInfo

import java.io.File

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
    fun parse(pluginFile: File): ApkInfo {
        return if (pluginFile.exists() && pluginFile.length() > 0) {
            ApkInfo("android.app.Application")
        } else {
            throw ParsePluginApkException("测试代码,但文件不合法")
        }

    }
}
