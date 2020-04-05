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

import android.content.ContentValues;

import org.json.JSONArray;

import java.util.Arrays;

public class InstalledRow {

    public String hash;

    public long installedTime;

    public String partKey;

    public String businessName;

    public String[] dependsOn;

    public String[] hostWhiteList;

    public String filePath;

    public int type;

    public String UUID;

    public String version;

    public String soDir;

    public String oDexDir;

    public InstalledRow() {
    }

    public InstalledRow(String hash, String partKey, String filePath, int type, String soDir, String oDexDir) {
        this.hash = hash;
        this.partKey = partKey;
        this.filePath = filePath;
        this.type = type;
        this.soDir = soDir;
        this.oDexDir = oDexDir;
    }

    public InstalledRow(String hash, String businessName, String partKey, String[] dependsOn, String filePath, int type, String[] hostWhiteList, String soDir, String oDexDir) {
        this(hash, partKey, filePath, type, soDir, oDexDir);
        this.businessName = businessName;
        this.dependsOn = dependsOn;
        this.hostWhiteList = hostWhiteList;
    }

    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(InstalledPluginDBHelper.COLUMN_HASH, hash);
        contentValues.put(InstalledPluginDBHelper.COLUMN_INSTALL_TIME, installedTime);
        if (businessName != null) {
            contentValues.put(InstalledPluginDBHelper.COLUMN_BUSINESS_NAME, businessName);
        }
        if (partKey != null) {
            contentValues.put(InstalledPluginDBHelper.COLUMN_PARTKEY, partKey);
        }
        if (dependsOn != null) {
            JSONArray jsonArray = new JSONArray(Arrays.asList(dependsOn));
            contentValues.put(InstalledPluginDBHelper.COLUMN_DEPENDSON, jsonArray.toString());
        }
        if (hostWhiteList != null) {
            JSONArray jsonArray = new JSONArray(Arrays.asList(hostWhiteList));
            contentValues.put(InstalledPluginDBHelper.COLUMN_HOST_WHITELIST, jsonArray.toString());
        }
        contentValues.put(InstalledPluginDBHelper.COLUMN_TYPE, type);
        contentValues.put(InstalledPluginDBHelper.COLUMN_UUID, UUID);
        contentValues.put(InstalledPluginDBHelper.COLUMN_VERSION, version);
        contentValues.put(InstalledPluginDBHelper.COLUMN_PATH, filePath);
        contentValues.put(InstalledPluginDBHelper.COLUMN_PLUGIN_LIB, soDir);
        contentValues.put(InstalledPluginDBHelper.COLUMN_PLUGIN_ODEX, oDexDir);
        return contentValues;
    }
}
