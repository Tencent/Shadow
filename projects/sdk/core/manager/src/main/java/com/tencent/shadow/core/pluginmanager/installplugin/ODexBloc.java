package com.tencent.shadow.core.pluginmanager.installplugin;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import dalvik.system.DexClassLoader;

public class ODexBloc {

    private static ConcurrentHashMap<String, Object> sLocks = new ConcurrentHashMap<>();

    public static File oDexPlugin(File apkFile, File oDexDir, File copiedTagFile) throws InstallPluginException {

        String key = apkFile.getAbsolutePath();
        Object lock = sLocks.get(key);
        if (lock == null) {
            lock = new Object();
            sLocks.put(key, lock);
        }


        synchronized (lock) {
            if (copiedTagFile.exists()) {
                return oDexDir;
            }

            //如果odex目录存在但是个文件，不是目录，那超出预料了。删除了也不一定能工作正常。
            if (oDexDir.exists() && oDexDir.isFile()) {
                throw new InstallPluginException("oDexDir=" + oDexDir.getAbsolutePath() + "已存在，但它是个文件，不敢贸然删除");
            }
            //创建oDex目录
            oDexDir.mkdirs();

            new DexClassLoader(apkFile.getAbsolutePath(), oDexDir.getAbsolutePath(), null, ODexBloc.class.getClassLoader());

            try {
                copiedTagFile.createNewFile();
            } catch (IOException e) {
                throw new InstallPluginException("oDexPlugin完毕 创建tag文件失败：" + copiedTagFile.getAbsolutePath(), e);
            }
        }

        return oDexDir;


    }
}
