/*
 * Tencent is pleased to support the open source community by making Tencent Shadow available.
 * Copyright (C) 2019 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tencent.shadow.core.manager;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.tencent.shadow.core.common.Logger;
import com.tencent.shadow.core.common.LoggerFactory;
import com.tencent.shadow.core.manager.installplugin.AppCacheFolderManager;
import com.tencent.shadow.core.manager.installplugin.CopySoBloc;
import com.tencent.shadow.core.manager.installplugin.InstallPluginException;
import com.tencent.shadow.core.manager.installplugin.InstalledDao;
import com.tencent.shadow.core.manager.installplugin.InstalledPlugin;
import com.tencent.shadow.core.manager.installplugin.InstalledPluginDBHelper;
import com.tencent.shadow.core.manager.installplugin.InstalledType;
import com.tencent.shadow.core.manager.installplugin.ODexBloc;
import com.tencent.shadow.core.manager.installplugin.PluginConfig;
import com.tencent.shadow.core.manager.installplugin.UnpackManager;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public abstract class BasePluginManager {
    private static final Logger mLogger = LoggerFactory.getLogger(BasePluginManager.class);
    /*
     * 宿主的context对象
     */
    public Context mHostContext;

    /**
     * 从压缩包中将插件解压出来，解析成InstalledPlugin
     */
    private UnpackManager mUnpackManager;

    /**
     * 插件信息查询数据库接口
     */
    private InstalledDao mInstalledDao;

    /**
     * UI线程的handler
     */
    protected Handler mUiHandler = new Handler(Looper.getMainLooper());


    public BasePluginManager(Context context) {
        this.mHostContext = context.getApplicationContext();
        this.mUnpackManager = new UnpackManager(mHostContext.getFilesDir(), getName());
        this.mInstalledDao = new InstalledDao(new InstalledPluginDBHelper(mHostContext, getName()));
    }

    /**
     * PluginManager的名字
     * 用于和其他PluginManager区分持续化存储的名字
     */
    abstract protected String getName();

    /**
     * 从文件夹中解压插件
     *
     * @param dir 文件夹路径
     * @return PluginConfig
     */
    public final PluginConfig installPluginFromDir(File dir) {
        throw new UnsupportedOperationException("TODO");
    }

    /**
     * 从压缩包中解压插件
     *
     * @param zip  压缩包路径
     * @param hash 压缩包hash
     * @return PluginConfig
     */
    public final PluginConfig installPluginFromZip(File zip, String hash) throws IOException, JSONException {
        return mUnpackManager.unpackPlugin(hash, zip);
    }

    /**
     * 安装完成时调用
     * <p>
     * 将插件信息持久化到数据库
     *
     * @param pluginConfig 插件配置信息
     */
    public final void onInstallCompleted(PluginConfig pluginConfig) {
        File root = mUnpackManager.getAppDir();
        String soDir = AppCacheFolderManager.getLibDir(root, pluginConfig.UUID).getAbsolutePath();
        String oDexDir = AppCacheFolderManager.getODexDir(root, pluginConfig.UUID).getAbsolutePath();

        mInstalledDao.insert(pluginConfig, soDir, oDexDir);
    }

    protected InstalledPlugin.Part getPluginPartByPartKey(String uuid, String partKey) {
        InstalledPlugin installedPlugin = mInstalledDao.getInstalledPluginByUUID(uuid);
        if (installedPlugin != null) {
            return installedPlugin.getPart(partKey);
        }
        throw new RuntimeException("没有找到Part partKey:" + partKey);
    }

    protected InstalledPlugin getInstalledPlugin(String uuid) {
        return mInstalledDao.getInstalledPluginByUUID(uuid);
    }

    protected InstalledPlugin.Part getLoaderOrRunTimePart(String uuid, int type) {
        if (type != InstalledType.TYPE_PLUGIN_LOADER && type != InstalledType.TYPE_PLUGIN_RUNTIME) {
            throw new RuntimeException("不支持的type:" + type);
        }
        InstalledPlugin installedPlugin = mInstalledDao.getInstalledPluginByUUID(uuid);
        if (type == InstalledType.TYPE_PLUGIN_RUNTIME) {
            if (installedPlugin.runtimeFile != null) {
                return installedPlugin.runtimeFile;
            }
        } else if (type == InstalledType.TYPE_PLUGIN_LOADER) {
            if (installedPlugin.pluginLoaderFile != null) {
                return installedPlugin.pluginLoaderFile;
            }
        }
        throw new RuntimeException("没有找到Part type :" + type);
    }

    /**
     * odex优化
     *
     * @param uuid    插件包的uuid
     * @param partKey 要oDex的插件partkey
     */
    public final void oDexPlugin(String uuid, String partKey, File apkFile) throws InstallPluginException {
        try {
            File root = mUnpackManager.getAppDir();
            File oDexDir = AppCacheFolderManager.getODexDir(root, uuid);
            ODexBloc.oDexPlugin(apkFile, oDexDir, AppCacheFolderManager.getODexCopiedFile(oDexDir, partKey));
        } catch (InstallPluginException e) {
            if (mLogger.isErrorEnabled()) {
                mLogger.error("oDexPlugin exception:", e);
            }
            throw e;
        }
    }


    /**
     * odex优化
     * @param uuid 插件包的uuid
     * @param type 要oDex的插件类型 @class IntalledType  loader or runtime
     * @param apkFile 插件apk文件
     */
    public final void oDexPluginLoaderOrRunTime(String uuid, int type, File apkFile) throws InstallPluginException {
        try {
            File root = mUnpackManager.getAppDir();
            File oDexDir = AppCacheFolderManager.getODexDir(root, uuid);
            String key = type == InstalledType.TYPE_PLUGIN_LOADER ? "loader" : "runtime";
            ODexBloc.oDexPlugin(apkFile, oDexDir, AppCacheFolderManager.getODexCopiedFile(oDexDir, key));
        } catch (InstallPluginException e) {
            if (mLogger.isErrorEnabled()) {
                mLogger.error("oDexPluginLoaderOrRunTime exception:", e);
            }
            throw e;
        }
    }


    /**
     * 插件apk的so解压
     *
     * @param uuid    插件包的uuid
     * @param partKey 要解压so的插件partkey
     * @param apkFile 插件apk文件
     */
    public final void extractSo(String uuid, String partKey, File apkFile) throws InstallPluginException {
        try {
            File root = mUnpackManager.getAppDir();
            String filter = "lib/" + getAbi() + "/";
            File soDir = AppCacheFolderManager.getLibDir(root, uuid);
            CopySoBloc.copySo(apkFile, soDir
                    , AppCacheFolderManager.getLibCopiedFile(soDir, partKey), filter);
        } catch (InstallPluginException e) {
            if (mLogger.isErrorEnabled()) {
                mLogger.error("extractSo exception:", e);
            }
            throw e;
        }
    }

    /**
     * 插件apk的so解压
     *
     * @param uuid 插件包的uuid
     * @param type 要oDex的插件类型 @class IntalledType  loader or runtime
     * @param apkFile 插件apk文件
     */
    public final void extractLoaderOrRunTimeSo(String uuid, int type, File apkFile) throws InstallPluginException {
        try {
            File root = mUnpackManager.getAppDir();
            String key = type == InstalledType.TYPE_PLUGIN_LOADER ? "loader" : "runtime";
            String filter = "lib/" + getAbi() + "/";
            File soDir = AppCacheFolderManager.getLibDir(root, uuid);
            CopySoBloc.copySo(apkFile, soDir
                    , AppCacheFolderManager.getLibCopiedFile(soDir, key), filter);
        } catch (InstallPluginException e) {
            if (mLogger.isErrorEnabled()) {
                mLogger.error("extractLoaderOrRunTimeSo exception:", e);
            }
            throw e;
        }
    }


    /**
     * 获取已安装的插件，最后安装的排在返回List的最前面
     *
     * @param limit 最多获取个数
     */
    public final List<InstalledPlugin> getInstalledPlugins(int limit) {
        return mInstalledDao.getLastPlugins(limit);
    }


    /**
     * 删除指定uuid的插件
     *
     * @param uuid 插件包的uuid
     * @return 是否全部执行成功
     */
    public boolean deleteInstalledPlugin(String uuid) {
        InstalledPlugin installedPlugin = mInstalledDao.getInstalledPluginByUUID(uuid);
        boolean suc = true;
        if (installedPlugin.runtimeFile != null) {
            if (!deletePart(installedPlugin.runtimeFile)) {
                suc = false;
            }
        }
        if (installedPlugin.pluginLoaderFile != null) {
            if (!deletePart(installedPlugin.pluginLoaderFile)) {
                suc = false;
            }
        }
        for (Map.Entry<String, InstalledPlugin.PluginPart> plugin : installedPlugin.plugins.entrySet()) {
            if (!deletePart(plugin.getValue())) {
                suc = false;
            }
        }
        if (mInstalledDao.deleteByUUID(uuid) <= 0) {
            suc = false;
        }
        return suc;
    }

    private boolean deletePart(InstalledPlugin.Part part) {
        boolean suc = true;
        if (!part.pluginFile.delete()) {
            suc = false;
        }
        if (part.oDexDir != null) {
            if (!part.oDexDir.delete()) {
                suc = false;
            }
        }
        if (part.libraryDir != null) {
            if (!part.libraryDir.delete()) {
                suc = false;
            }
        }
        return suc;
    }


    /**
     * 业务插件的abi
     *
     * @return
     */
    public String getAbi() {
        return null;
    }
}
