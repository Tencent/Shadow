package com.tencent.shadow.core.loader.infos

import java.io.File

/**
 * Loader加载插件的输入参数结构体
 * @author cubershi
 */
class InstalledPlugin(
        val pluginFileType: Int,
        val partKey: String,
        val dependsOn: Array<String>?,
        val apkFile: File,
        val oDexDir: File?,
        val libraryDir: File?
)
