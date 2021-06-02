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

package com.tencent.shadow.core.manager.installplugin;

import com.tencent.shadow.core.common.Logger;
import com.tencent.shadow.core.common.LoggerFactory;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.tencent.shadow.core.utils.Md5.md5File;


public class UnpackManager {

    private static final Logger mLogger = LoggerFactory.getLogger(UnpackManager.class);

    private static final String UNPACK_DONE_PRE_FIX = "unpacked.";
    private static final String CONFIG_FILENAME = "config.json";//todo #28 json的格式需要沉淀文档。
    private static final String DEFAULT_STORE_DIR_NAME = "ShadowPluginManager";

    private final File mPluginUnpackedDir;

    private final String mAppName;

    public UnpackManager(File root, String appName) {
        File parent = new File(root, DEFAULT_STORE_DIR_NAME);
        mPluginUnpackedDir = new File(parent, "UnpackedPlugin");
        mPluginUnpackedDir.mkdirs();
        mAppName = appName;
    }


    File getVersionDir(String appHash) {
        return AppCacheFolderManager.getVersionDir(mPluginUnpackedDir, mAppName, appHash);
    }

    public File getAppDir() {
        return AppCacheFolderManager.getAppDir(mPluginUnpackedDir, mAppName);
    }

    /**
     * 获取插件解包的目标目录。根据target的文件名决定。
     *
     * @param target Target
     * @return 插件解包的目标目录
     */
    File getPluginUnpackDir(String appHash, File target) {
        return new File(getVersionDir(appHash), target.getName());
    }

    /**
     * 判断一个插件是否已经解包了
     *
     * @param target Target
     * @return <code>true</code>表示已经解包了,即无需下载。
     */
    boolean isPluginUnpacked(String versionHash, File target) {
        File pluginUnpackDir = getPluginUnpackDir(versionHash, target);
        return isDirUnpacked(pluginUnpackDir);
    }

    /**
     * 判断一个插件解包目录是否解包了
     *
     * @param pluginUnpackDir 插件解包目录
     * @return <code>true</code>表示已经解包了,即无需下载。
     */
    boolean isDirUnpacked(File pluginUnpackDir) {
        File tag = getUnpackedTag(pluginUnpackDir);
        return tag.exists();
    }


    /**
     * 解包一个下载好的插件
     *  @param zipHash 插件包的hash
     * @param target  插件包
     */
    public PluginConfig unpackPlugin(String zipHash, File target) throws IOException, JSONException {
        if (zipHash == null) {
            zipHash = md5File(target);
        }
        File pluginUnpackDir = getPluginUnpackDir(zipHash, target);

        pluginUnpackDir.mkdirs();
        File tag = getUnpackedTag(pluginUnpackDir);

        if (isDirUnpacked(pluginUnpackDir)) {
            try {
                return getDownloadedPluginInfoFromPluginUnpackedDir(pluginUnpackDir, zipHash);
            } catch (Exception e) {
                if (!tag.delete()) {
                    throw new IOException("解析版本信息失败，且无法删除标记:" + tag.getAbsolutePath());
                }
            }
        }
        MinFileUtils.cleanDirectory(pluginUnpackDir);

        ZipFile zipFile = null;
        try {
            zipFile = new SafeZipFile(target);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (!entry.isDirectory()) {
                    MinFileUtils.writeOutZipEntry(zipFile, entry, pluginUnpackDir, entry.getName());
                }
            }

            PluginConfig pluginConfig = getDownloadedPluginInfoFromPluginUnpackedDir(pluginUnpackDir, zipHash);

            // 外边创建完成标记
            tag.createNewFile();

            return pluginConfig;
        } finally {
            try {
                if (zipFile != null) {
                    zipFile.close();
                }
            } catch (IOException e) {
                mLogger.warn("zip关闭时出错忽略", e);
            }
        }
    }

    File getUnpackedTag(File pluginUnpackDir) {
        return new File(pluginUnpackDir.getParentFile(), UNPACK_DONE_PRE_FIX + pluginUnpackDir.getName());
    }

    PluginConfig getDownloadedPluginInfoFromPluginUnpackedDir(File pluginUnpackDir, String appHash)
            throws IOException, JSONException {
        File config = new File(pluginUnpackDir, CONFIG_FILENAME);
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(config)));
        StringBuilder stringBuilder = new StringBuilder("");
        String lineStr;
        try {
            while ((lineStr = br.readLine()) != null) {
                stringBuilder.append(lineStr).append("\n");
            }
        } finally {
            //noinspection ThrowFromFinallyBlock
            br.close();
        }
        String versionJson = stringBuilder.toString();
        return PluginConfig.parseFromJson(versionJson, pluginUnpackDir);
    }

}
