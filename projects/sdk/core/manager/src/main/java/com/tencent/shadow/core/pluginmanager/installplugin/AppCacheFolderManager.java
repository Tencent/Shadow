package com.tencent.shadow.core.pluginmanager.installplugin;

import java.io.File;

/**
 * 目录各模块的目录关系管理
 */
public class AppCacheFolderManager {

    public static File getVersionDir(File root, String appName, String version) {
        return new File(getAppDir(root, appName), version);
    }

    public static File getAppDir(File root, String appName) {
        return new File(root, appName);
    }


    public static File getODexDir(File root,String key){
        return new File(getODexRootDir(root), key+ "_odex");
    }

    public static File getODexCopiedFile(File oDexDir,String key){
        return new File(oDexDir, key+ "_copied");
    }


    private static File getODexRootDir(File root){
        return new File(root,"oDex");
    }

    public static File getLibDir(File root,String key){
        return new File(getLibRootDir(root), key+ "_lib");
    }

    public static File getLibCopiedFile(File soDir,String key){
        return new File(soDir, key+ "_copied");
    }


    private static File getLibRootDir(File root){
        return new File(root,"lib");
    }

}
