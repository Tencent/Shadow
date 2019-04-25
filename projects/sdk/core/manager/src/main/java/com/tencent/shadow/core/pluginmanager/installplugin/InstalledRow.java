package com.tencent.shadow.core.pluginmanager.installplugin;

import android.content.ContentValues;

import org.json.JSONArray;

import java.util.Arrays;

public class InstalledRow {

    public String hash;

    public long installedTime;

    public String partKey;

    public String[] dependsOn;

    public String[] hostWhiteList;

    public String filePath;

    public int type;

    public String UUID;

    public String version;

    public InstalledRow() {
    }

    public InstalledRow(String hash, String partKey, String filePath, int type) {
        this.hash = hash;
        this.partKey = partKey;
        this.filePath = filePath;
        this.type = type;
    }

    public InstalledRow(String hash, String partKey, String[] dependsOn, String filePath, int type, String[] hostWhiteList) {
        this(hash, partKey, filePath, type);
        this.dependsOn = dependsOn;
        this.hostWhiteList = hostWhiteList;
    }

    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(InstalledPluginDBHelper.COLUMN_HASH, hash);
        contentValues.put(InstalledPluginDBHelper.COLUMN_INSTALL_TIME, installedTime);
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
        return contentValues;
    }
}
