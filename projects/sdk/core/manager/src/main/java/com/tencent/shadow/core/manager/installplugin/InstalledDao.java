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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InstalledDao {

    private InstalledPluginDBHelper mDBHelper;

    public InstalledDao(InstalledPluginDBHelper dbHelper) {
        mDBHelper = dbHelper;
    }

    /**
     * 根据插件配置信息插入一组数据
     *
     * @param pluginConfig 插件配置信息
     * @return 安装后的信息
     */
    public InstalledPlugin insert(PluginConfig pluginConfig) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        Pair<InstalledPlugin, List<ContentValues>> pair = parseConfig(pluginConfig);
        List<ContentValues> contentValuesList = pair.second;
        db.beginTransaction();
        try {
            for (ContentValues contentValues : contentValuesList) {
                db.replace(InstalledPluginDBHelper.TABLE_NAME_MANAGER, null, contentValues);
            }
            //把最后一次uuid的插件安装时间作为所有相同uuid的插件的安装时间
            ContentValues values = new ContentValues();
            values.put(InstalledPluginDBHelper.COLUMN_INSTALL_TIME, pluginConfig.storageDir.lastModified());
            db.update(InstalledPluginDBHelper.TABLE_NAME_MANAGER, values, InstalledPluginDBHelper.COLUMN_UUID + " = ?", new String[]{pluginConfig.UUID});

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return pair.first;
    }

    /**
     * 删除UUID相关的数据
     *
     * @param UUID 插件的发布id
     * @return 影响的数据行数
     */
    public int deleteByUUID(String UUID) {
        int row ;
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            row = db.delete(InstalledPluginDBHelper.TABLE_NAME_MANAGER, InstalledPluginDBHelper.COLUMN_UUID + " =?", new String[]{UUID});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return row;
    }

    /**
     * 根据uuid和APPID获取对应的插件信息
     *
     * @param UUID  插件的发布id
     * @return 插件安装数据
     */
    public InstalledPlugin getInstalledPluginByUUID(String UUID) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from shadowPluginManager where uuid = ?", new String[]{UUID});
        InstalledPlugin installedPlugin = new InstalledPlugin();
        installedPlugin.UUID = UUID;
        while (cursor.moveToNext()) {
            int type = cursor.getInt(cursor.getColumnIndex(InstalledPluginDBHelper.COLUMN_TYPE));
            if (type == InstalledType.TYPE_UUID) {
                installedPlugin.UUID_NickName = cursor.getString(cursor.getColumnIndex(InstalledPluginDBHelper.COLUMN_VERSION));
            } else {
                File pluginFile = new File(cursor.getString(cursor.getColumnIndex(InstalledPluginDBHelper.COLUMN_PATH)));
                String oDexPath = cursor.getString(cursor.getColumnIndex(InstalledPluginDBHelper.COLUMN_PLUGIN_ODEX));
                File oDexDir = oDexPath == null ? null : new File(oDexPath);
                String libPath = cursor.getString(cursor.getColumnIndex(InstalledPluginDBHelper.COLUMN_PLUGIN_LIB));
                File libDir = libPath == null ? null : new File(libPath);
                if (type == InstalledType.TYPE_PLUGIN_LOADER) {
                    installedPlugin.pluginLoaderFile = new InstalledPlugin.Part(type, pluginFile, oDexDir, libDir);
                } else if (type == InstalledType.TYPE_PLUGIN_RUNTIME) {
                    installedPlugin.runtimeFile = new InstalledPlugin.Part(type, pluginFile, oDexDir, libDir);
                } else {
                    String businessName = cursor.getString(cursor.getColumnIndex(InstalledPluginDBHelper.COLUMN_BUSINESS_NAME));

                    String partKey = cursor.getString(cursor.getColumnIndex(InstalledPluginDBHelper.COLUMN_PARTKEY));

                    if (type == InstalledType.TYPE_PLUGIN) {
                        String[] dependsOn = getArrayStringByColumnName(InstalledPluginDBHelper.COLUMN_DEPENDSON, cursor);
                        String[] hostWhiteList = getArrayStringByColumnName(InstalledPluginDBHelper.COLUMN_HOST_WHITELIST, cursor);
                        installedPlugin.plugins.put(partKey, new InstalledPlugin.PluginPart(type, businessName, pluginFile, oDexDir, libDir, dependsOn, hostWhiteList));
                    } else {
                        throw new RuntimeException("出现不认识的type==" + type);
                    }
                }
            }
        }
        cursor.close();
        return installedPlugin;
    }

    private String[] getArrayStringByColumnName(String columnName, Cursor cursor) {
        int columnIndex = cursor.getColumnIndex(columnName);
        boolean hasColumn = !cursor.isNull(columnIndex);
        String[] arrayString;
        if (hasColumn) {
            String string = cursor.getString(columnIndex);
            try {
                JSONArray jsonArray = new JSONArray(string);
                arrayString = new String[jsonArray.length()];
                for (int i = 0; i < jsonArray.length(); i++) {
                    arrayString[i] = jsonArray.getString(i);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        } else {
            arrayString = null;
        }
        return arrayString;
    }

    /**
     * 获取最近的插件列表数据
     *
     * @param limit 获取的数据数量
     * @return 插件列表数据
     */
    public List<InstalledPlugin> getLastPlugins(int limit) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select uuid from shadowPluginManager where  type = ?   order by installedTime desc limit " + limit, new String[]{String.valueOf(InstalledType.TYPE_UUID)});
        List<String> uuids = new ArrayList<>();
        while (cursor.moveToNext()) {
            String uuid = cursor.getString(cursor.getColumnIndex(InstalledPluginDBHelper.COLUMN_UUID));
            uuids.add(uuid);
        }
        cursor.close();
        List<InstalledPlugin> installedPlugins = new ArrayList<>();
        for (String uuid : uuids) {
            installedPlugins.add(getInstalledPluginByUUID(uuid));
        }
        db.close();
        return installedPlugins;
    }


    public boolean updatePlugin(String UUID, String partKey, ContentValues contentValues) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        db.beginTransaction();
        int row = 0;
        try {
            row = db.update(InstalledPluginDBHelper.TABLE_NAME_MANAGER, contentValues,
                    InstalledPluginDBHelper.COLUMN_UUID + " = ? and " + InstalledPluginDBHelper.COLUMN_PARTKEY + " = ?",
                    new String[]{UUID, partKey});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return row > 0;
    }

    public boolean updatePlugin(String UUID, int type, ContentValues contentValues) {
        if (type != InstalledType.TYPE_PLUGIN_LOADER && type != InstalledType.TYPE_PLUGIN_RUNTIME) {
            throw new RuntimeException("不支持更新 type:" + type);
        }
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        db.beginTransaction();
        int row = 0;
        try {
            row = db.update(InstalledPluginDBHelper.TABLE_NAME_MANAGER, contentValues,
                    InstalledPluginDBHelper.COLUMN_UUID + " = ? and " + InstalledPluginDBHelper.COLUMN_TYPE + " = ?",
                    new String[]{UUID, String.valueOf(type)});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return row > 0;
    }

    private Pair<InstalledPlugin, List<ContentValues>> parseConfig(PluginConfig pluginConfig) {
        List<InstalledRow> installedRows = new ArrayList<>();
        InstalledPlugin installedPlugin = new InstalledPlugin();
        if (pluginConfig.pluginLoader != null) {
            installedPlugin.pluginLoaderFile = new InstalledPlugin.Part(InstalledType.TYPE_PLUGIN_LOADER, pluginConfig.pluginLoader.file, null, null);
            installedRows.add(new InstalledRow(pluginConfig.pluginLoader.hash, null, pluginConfig.pluginLoader.file.getAbsolutePath(), InstalledType.TYPE_PLUGIN_LOADER));
        }
        if (pluginConfig.runTime != null) {
            installedPlugin.runtimeFile = new InstalledPlugin.Part(InstalledType.TYPE_PLUGIN_RUNTIME, pluginConfig.runTime.file, null, null);
            installedRows.add(new InstalledRow(pluginConfig.runTime.hash, null, pluginConfig.runTime.file.getAbsolutePath(), InstalledType.TYPE_PLUGIN_RUNTIME));
        }
        if (pluginConfig.plugins != null) {
            Set<Map.Entry<String, PluginConfig.PluginFileInfo>> plugins = pluginConfig.plugins.entrySet();
            Map<String, InstalledPlugin.PluginPart> map = new HashMap<>();
            for (Map.Entry<String, PluginConfig.PluginFileInfo> plugin : plugins) {
                PluginConfig.PluginFileInfo fileInfo = plugin.getValue();
                map.put(plugin.getKey(),
                        new InstalledPlugin.PluginPart(
                                InstalledType.TYPE_PLUGIN,
                                fileInfo.businessName,
                                fileInfo.file,
                                null,
                                null,
                                fileInfo.dependsOn,
                                fileInfo.hostWhiteList
                        )
                );
                installedRows.add(
                        new InstalledRow(
                                fileInfo.hash,
                                fileInfo.businessName,
                                plugin.getKey(),
                                fileInfo.dependsOn,
                                fileInfo.file.getAbsolutePath(),
                                InstalledType.TYPE_PLUGIN,
                                fileInfo.hostWhiteList
                        )
                );
            }
            installedPlugin.plugins = map;
        }
        InstalledRow uuidRow = new InstalledRow();
        uuidRow.type = InstalledType.TYPE_UUID;
        uuidRow.filePath = pluginConfig.UUID;
        installedRows.add(uuidRow);
        List<ContentValues> contentValues = new ArrayList<>();
        for (InstalledRow row : installedRows) {
            row.installedTime = pluginConfig.storageDir.lastModified();
            row.UUID = pluginConfig.UUID;
            row.version = pluginConfig.UUID_NickName;
            contentValues.add(row.toContentValues());
        }
        installedPlugin.UUID = pluginConfig.UUID;
        installedPlugin.UUID_NickName = pluginConfig.UUID_NickName;
        return new Pair<>(installedPlugin, contentValues);
    }


}
