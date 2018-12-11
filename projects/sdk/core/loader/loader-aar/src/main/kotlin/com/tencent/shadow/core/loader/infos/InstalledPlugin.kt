package com.tencent.shadow.core.loader.infos

import java.io.File

class InstalledPlugin(
        val pluginFile: File,
        val pluginFileType: Int,
        val partKey: String,
        val pluginVersionForPluginLoaderManage: String,
        val dependsOn: Array<String>?
)
