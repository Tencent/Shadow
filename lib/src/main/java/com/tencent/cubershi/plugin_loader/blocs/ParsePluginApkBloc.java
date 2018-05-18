package com.tencent.cubershi.plugin_loader.blocs;

import com.tencent.cubershi.plugin_loader.exceptions.ParsePluginApkException;
import com.tencent.cubershi.plugin_loader.infos.ApkInfo;

import java.io.File;

/**
 * 解析插件apk逻辑
 *
 * @author cubershi
 */
public class ParsePluginApkBloc {
    /**
     * 解析插件apk
     *
     * @param pluginFile 插件apk文件
     * @return 解析信息
     * @throws ParsePluginApkException 解析失败时抛出
     */
    public static ApkInfo parse(File pluginFile) throws ParsePluginApkException {
        if (pluginFile.exists() && pluginFile.length() > 0) {
            return new ApkInfo();
        } else {
            throw new ParsePluginApkException("测试代码,但文件不合法");
        }

    }
}
