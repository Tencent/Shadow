package com.tencent.shadow.core.pluginmanager.installplugin;

import android.content.ContentValues;

import org.json.JSONArray;

import java.util.Arrays;

public class InstalledRow {

    public final static int TYPE_PLUGIN = 1;

    public final static int TYPE_INTERFACE = 2;

    public final static int TYPE_PLUGIN_LOADER = 3;

    public final static int TYPE_PLUGIN_RUNTIME = 4;

    public final static int TYPE_UUID = 5;


    public String hash;

    public long installedTime;

    public String partKey;

    public String[] dependsOn;

    public String fileName;

    public String filePath;

    public int type;

    public String UUID;

    public String version;

    public String appId;

    public InstalledRow() {
    }

    public InstalledRow(String hash, String partKey, String fileName, int type) {
        this.hash = hash;
        this.partKey = partKey;
        this.fileName = fileName;
        this.type = type;
    }

    public InstalledRow(String hash, String partKey, String[] dependsOn, String filePath, int type) {
        this(hash, partKey, filePath, type);
        this.dependsOn = dependsOn;
    }

    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(InstalledPluginDBHelper.COLUMN_APPID, appId);
        contentValues.put(InstalledPluginDBHelper.COLUMN_HASH, hash);
        contentValues.put(InstalledPluginDBHelper.COLUMN_INSTALL_TIME, installedTime);
        if (partKey != null) {
            contentValues.put(InstalledPluginDBHelper.COLUMN_PARTKEY, partKey);
        }
        if (dependsOn != null) {
            JSONArray jsonArray = new JSONArray(Arrays.asList(dependsOn));
            contentValues.put(InstalledPluginDBHelper.COLUMN_DEPENDSON, jsonArray.toString());
        }
        contentValues.put(InstalledPluginDBHelper.COLUMN_TYPE, type);
        contentValues.put(InstalledPluginDBHelper.COLUMN_UUID, UUID);
        contentValues.put(InstalledPluginDBHelper.COLUMN_VERSION, version);
        contentValues.put(InstalledPluginDBHelper.COLUMN_PATH, filePath);
        contentValues.put(InstalledPluginDBHelper.COLUMN_FILE_NAME, fileName);
        return contentValues;
    }
}
