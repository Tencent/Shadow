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
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Pair;

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
import com.tencent.shadow.core.manager.installplugin.SafeZipFile;
import com.tencent.shadow.core.manager.installplugin.UnpackManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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
    final private InstalledDao mInstalledDao;

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
        String zipHash;
        if (hash != null) {
            zipHash = hash;
        } else {
            zipHash = mUnpackManager.zipHash(zip);
        }
        File pluginUnpackDir = mUnpackManager.getPluginUnpackDir(zipHash, zip);
        JSONObject configJson = mUnpackManager.getConfigJson(zip);
        PluginConfig pluginConfig = PluginConfig.parseFromJson(configJson, pluginUnpackDir);

        if (!pluginConfig.isUnpacked()) {
            mUnpackManager.unpackPlugin(zip, pluginUnpackDir);
        }

        return pluginConfig;
    }

    /**
     * 安装完成时调用
     * <p>
     * 将插件信息持久化到数据库
     *
     * @param pluginConfig 插件配置信息
     * @param soDirMap     key:type+partKey
     */
    public final void onInstallCompleted(PluginConfig pluginConfig,
                                         Map<String, String> soDirMap) {
        File root = mUnpackManager.getAppDir();
        String oDexDir = ODexBloc.isEffective() ?
                AppCacheFolderManager.getODexDir(root, pluginConfig.UUID).getAbsolutePath() : null;

        mInstalledDao.insert(pluginConfig, soDirMap, oDexDir);
    }

    protected InstalledPlugin.Part getPluginPartByPartKey(String uuid, String partKey) {
        InstalledPlugin installedPlugin = mInstalledDao.getInstalledPluginByUUID(uuid);
        if (installedPlugin == null) {
            throw new RuntimeException("没有找到uuid:" + uuid);
        }
        InstalledPlugin.Part part = installedPlugin.getPart(partKey);
        if (part == null) {
            throw new RuntimeException("没有找到Part partKey:" + partKey);
        }
        return part;
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
        if (!ODexBloc.isEffective()) {
            return;
        }

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
     *
     * @param uuid    插件包的uuid
     * @param type    要oDex的插件类型 @class IntalledType  loader or runtime
     * @param apkFile 插件apk文件
     */
    public final void oDexPluginLoaderOrRunTime(String uuid, int type, File apkFile) throws InstallPluginException {
        if (!ODexBloc.isEffective()) {
            return;
        }

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
     * 解压插件apk中的so。
     * <p>
     * 插件的ABI和宿主正在使用的保持一致。
     * 注意：如果宿主没有打包so，它的ABI会被系统自动设置为设备默认值，
     * 默认值可能和插件apk中打包的ABI不一致，导致插件so解压不正确。
     *
     * @param uuid    插件包的uuid
     * @param partKey 要解压so的插件partkey
     * @param apkFile 插件apk文件
     * @return soDirMap条目
     */
    public final Pair<String, String> extractSo(String uuid, String partKey, File apkFile) throws InstallPluginException {
        try {
            File root = mUnpackManager.getAppDir();
            File soDir = AppCacheFolderManager.getLibDir(root, uuid);
            String soDirMapKey = InstalledType.TYPE_PLUGIN + partKey;
            String soDirPath = soDir.getAbsolutePath();

            String pluginPreferredAbi = getPluginPreferredAbi(getPluginSupportedAbis(), apkFile);
            if (pluginPreferredAbi.isEmpty()) {
                if (mLogger.isInfoEnabled()) {
                    mLogger.info("插件没有so");
                }
            } else {
                String filter = "lib/" + pluginPreferredAbi + "/";

                // 插件如果设置了android:extractNativeLibs="false"，则不需要解压出so
                boolean needExtractNativeLibs = needExtractNativeLibs(apkFile, filter);

                if (mLogger.isInfoEnabled()) {
                    mLogger.info("extractSo uuid=={} partKey=={} apkFile=={} soDir=={} filter=={} needExtractNativeLibs=={}",
                            uuid, partKey, apkFile.getAbsolutePath(), soDir.getAbsolutePath(), filter, needExtractNativeLibs);
                }

                if (needExtractNativeLibs) {
                    CopySoBloc.copySo(apkFile, soDir
                            , AppCacheFolderManager.getLibCopiedFile(soDir, partKey), filter);
                } else {
                    soDirPath = apkFile.getAbsolutePath() + "!/" + filter;
                }
            }
            return new Pair<>(soDirMapKey, soDirPath);
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
     * @param uuid    插件包的uuid
     * @param type    要oDex的插件类型 @class IntalledType  loader or runtime
     * @param apkFile 插件apk文件
     * @return soDirMap条目
     */
    public final Pair<String, String> extractLoaderOrRunTimeSo(String uuid,
                                                               int type,
                                                               File apkFile)
            throws InstallPluginException {
        try {
            File root = mUnpackManager.getAppDir();
            String key = type == InstalledType.TYPE_PLUGIN_LOADER ? "loader" : "runtime";
            String pluginPreferredAbi = getPluginPreferredAbi(getPluginSupportedAbis(), apkFile);
            String filter = "lib/" + pluginPreferredAbi + "/";
            File soDir = AppCacheFolderManager.getLibDir(root, uuid);

            if (pluginPreferredAbi.isEmpty()) {
                if (mLogger.isInfoEnabled()) {
                    mLogger.info(key + "没有so");
                }
            } else {
                CopySoBloc.copySo(apkFile, soDir
                        , AppCacheFolderManager.getLibCopiedFile(soDir, key), filter);
            }

            String soDirMapKey = Integer.toString(type) + null;// 同InstalledDao.parseConfig
            String soDirPath = soDir.getAbsolutePath();
            return new Pair<>(soDirMapKey, soDirPath);
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
        return mInstalledDao.getLatestPlugins(limit);
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
     * 当前插件希望采用的ABI。
     * 子类可以override重新决定。
     *
     * @param pluginSupportedAbis 从getPluginSupportedAbis方法得到的可选ABI列表
     * @param apkFile             插件apk文件
     * @return 最终决定的ABI。插件没有so时返回空字符串。
     * @throws InstallPluginException 读取apk文件失败时抛出
     */
    protected String getPluginPreferredAbi(String[] pluginSupportedAbis, File apkFile)
            throws InstallPluginException {
        try (ZipFile zipFile = new SafeZipFile(apkFile)) {
            //找出插件apk中lib目录下都有哪些子目录
            Set<String> subDirsInLib = new LinkedHashSet<>();
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String name = entry.getName();
                if (name.startsWith("lib/")) {
                    String[] split = name.split("/");
                    if (split.length == 3) {// like "lib/arm64-v8a/libabc.so"
                        subDirsInLib.add(split[1]);
                    }
                }
            }

            for (String supportedAbi : pluginSupportedAbis) {
                if (subDirsInLib.contains(supportedAbi)) {
                    return supportedAbi;
                }
            }
            return "";
        } catch (IOException e) {
            throw new InstallPluginException("读取apk失败，apkFile==" + apkFile, e);
        }
    }

    /**
     * 获取可用的ABI列表。
     * 和Build.SUPPORTED_ABIS的区别是，这是宿主已经决定了当前进程用32位so还是64位so了，
     * 所以可用的ABI只能是其中一部分。
     */
    private String[] getPluginSupportedAbis() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String nativeLibraryDir = mHostContext.getApplicationInfo().nativeLibraryDir;
            int nextIndexOfLastSlash = nativeLibraryDir.lastIndexOf('/') + 1;
            String instructionSet = nativeLibraryDir.substring(nextIndexOfLastSlash);
            if (!isKnownInstructionSet(instructionSet)) {
                throw new IllegalStateException("不认识的instructionSet==" + instructionSet);
            }
            boolean is64Bit = is64BitInstructionSet(instructionSet);
            return is64Bit ? Build.SUPPORTED_64_BIT_ABIS : Build.SUPPORTED_32_BIT_ABIS;
        } else {
            String cpuAbi = Build.CPU_ABI;
            String cpuAbi2 = Build.CPU_ABI2;
            ArrayList<String> list = new ArrayList<>(2);
            if (cpuAbi != null && !cpuAbi.isEmpty()) {
                list.add(cpuAbi);
            }
            if (cpuAbi2 != null && !cpuAbi2.isEmpty()) {
                list.add(cpuAbi2);
            }
            return list.toArray(new String[0]);
        }
    }

    /**
     * 根据VMRuntime.ABI_TO_INSTRUCTION_SET_MAP
     */
    private static boolean isKnownInstructionSet(String instructionSet) {
        return "arm".equals(instructionSet) ||
                "mips".equals(instructionSet) ||
                "mips64".equals(instructionSet) ||
                "x86".equals(instructionSet) ||
                "x86_64".equals(instructionSet) ||
                "arm64".equals(instructionSet);
    }

    /**
     * Returns whether the given {@code instructionSet} is 64 bits.
     *
     * @param instructionSet a string representing an instruction set.
     * @return true if given {@code instructionSet} is 64 bits, false otherwise.
     * <p>
     * copy from VMRuntime.java
     */
    private static boolean is64BitInstructionSet(String instructionSet) {
        return "arm64".equals(instructionSet) ||
                "x86_64".equals(instructionSet) ||
                "mips64".equals(instructionSet);
    }

    private static boolean needExtractNativeLibs(File apkFile, String filter) throws InstallPluginException {
        //android:extractNativeLibs是API 23引入的
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        try (ZipFile zipFile = new SafeZipFile(apkFile)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String name = entry.getName();
                if (name.startsWith(filter)) {
                    return entry.getMethod() != ZipEntry.STORED;
                }
            }
            return false;
        } catch (IOException e) {
            throw new InstallPluginException("读取apk失败，apkFile==" + apkFile, e);
        }
    }

    /**
     * 释放资源
     */
    public void close() {
        mInstalledDao.close();
    }
}
