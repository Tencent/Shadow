package com.tencent.shadow.sdk.pluginmanager.installplugin;

import java.io.File;

/**
 * 目录各模块的目录关系管理
 * relativeRoot
 * -appName
 * --version
 */
class AppCacheFolderManager {
    static File getVersionDir(File root, String appName, String version) {
        return new File(getAppDir(root, appName), version);
    }

    static File getAppDir(File root, String appName) {
        return new File(root, appName);
    }
}
