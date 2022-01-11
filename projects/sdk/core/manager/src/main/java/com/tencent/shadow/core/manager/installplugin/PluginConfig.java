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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class PluginConfig {

    /**
     * 配置json文件的格式版本号
     */
    public int version;
    /**
     * 配置json文件的格式兼容版本号
     */
    public int[] compact_version;
    /**
     * 标识一次插件发布的id
     */
    public String UUID;
    /**
     * 标识一次插件发布的id，可以使用自定义格式描述版本信息
     */
    public String UUID_NickName;
    /**
     * pluginLoaderAPk 文件信息
     */
    public FileInfo pluginLoader;
    /**
     * runtime 文件信息
     */
    public FileInfo runTime;
    /**
     * 业务插件 key: partKey value:文件信息
     */
    public Map<String, PluginFileInfo> plugins = new HashMap<>();
    /**
     * 插件的存储目录
     */
    public File storageDir;

    public boolean isUnpacked() {
        boolean pluginLoaderUnpacked = true;
        if (pluginLoader != null) {
            pluginLoaderUnpacked = pluginLoader.file.exists();
        }

        boolean runtimeUnpacked = true;
        if (runTime != null) {
            runtimeUnpacked = runTime.file.exists();
        }

        boolean pluginsUnpacked = true;
        for (PluginFileInfo pluginFileInfo : plugins.values()) {
            pluginsUnpacked = pluginsUnpacked && pluginFileInfo.file.exists();
        }
        return pluginLoaderUnpacked && runtimeUnpacked && pluginsUnpacked;
    }

    public static class FileInfo {
        public final File file;
        public final String hash;

        FileInfo(File file, String hash) {
            this.file = file;
            this.hash = hash;
        }
    }

    public static class PluginFileInfo extends FileInfo {
        final String[] dependsOn;
        final String[] hostWhiteList;
        final String businessName;

        PluginFileInfo(String businessName, FileInfo fileInfo, String[] dependsOn, String[] hostWhiteList) {
            this(businessName, fileInfo.file, fileInfo.hash, dependsOn, hostWhiteList);
        }

        PluginFileInfo(String businessName, File file, String hash, String[] dependsOn, String[] hostWhiteList) {
            super(file, hash);
            this.businessName = businessName;
            this.dependsOn = dependsOn;
            this.hostWhiteList = hostWhiteList;
        }
    }

    public static PluginConfig parseFromJson(JSONObject configJson, File storageDir) throws JSONException {
        PluginConfig pluginConfig = new PluginConfig();
        pluginConfig.version = configJson.getInt("version");
        JSONArray compact_version_json = configJson.optJSONArray("compact_version");
        if (compact_version_json != null && compact_version_json.length() > 0) {
            pluginConfig.compact_version = new int[compact_version_json.length()];
            for (int i = 0; i < compact_version_json.length(); i++) {
                pluginConfig.compact_version[i] = compact_version_json.getInt(i);
            }
        }
        //todo #27 json的版本检查和不兼容检查
        pluginConfig.UUID = configJson.getString("UUID");
        pluginConfig.UUID_NickName = configJson.getString("UUID_NickName");

        JSONObject loaderJson = configJson.optJSONObject("pluginLoader");
        if (loaderJson != null) {
            pluginConfig.pluginLoader = getFileInfo(loaderJson, storageDir);
        }

        JSONObject runtimeJson = configJson.optJSONObject("runtime");
        if (runtimeJson != null) {
            pluginConfig.runTime = getFileInfo(runtimeJson, storageDir);
        }

        JSONArray pluginArray = configJson.optJSONArray("plugins");
        if (pluginArray != null && pluginArray.length() > 0) {
            for (int i = 0; i < pluginArray.length(); i++) {
                JSONObject plugin = pluginArray.getJSONObject(i);
                String partKey = plugin.getString("partKey");
                pluginConfig.plugins.put(partKey, getPluginFileInfo(plugin, storageDir));
            }
        }

        pluginConfig.storageDir = storageDir;
        return pluginConfig;
    }

    private static FileInfo getFileInfo(JSONObject jsonObject, File storageDir) throws JSONException {
        String name = jsonObject.getString("apkName");
        String hash = jsonObject.getString("hash");
        return new FileInfo(new File(storageDir, name), hash);
    }

    private static PluginFileInfo getPluginFileInfo(JSONObject jsonObject, File storageDir) throws JSONException {
        String businessName = jsonObject.optString("businessName", "");
        FileInfo fileInfo = getFileInfo(jsonObject, storageDir);
        String[] dependsOn = getArrayStringByName(jsonObject, "dependsOn");
        String[] hostWhiteList = getArrayStringByName(jsonObject, "hostWhiteList");
        return new PluginFileInfo(businessName, fileInfo, dependsOn, hostWhiteList);
    }

    private static String[] getArrayStringByName(JSONObject jsonObject, String name) throws JSONException {
        JSONArray jsonArray = jsonObject.optJSONArray(name);
        String[] dependsOn;
        if (jsonArray != null) {
            dependsOn = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                dependsOn[i] = jsonArray.getString(i);
            }
        } else {
            dependsOn = new String[]{};
        }
        return dependsOn;
    }
}
