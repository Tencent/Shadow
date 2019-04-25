package com.tencent.shadow.core.pluginmanager.installplugin;

import android.content.ContentValues;

import org.json.JSONArray;

import java.util.Arrays;

public class InstalledRow {

    public String hash;

    public long installedTime;

    public String partKey;

    public String businessName;

    public String[] dependsOn;

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

    public InstalledRow(String hash, String businessName, String partKey, String[] dependsOn, String filePath, int type) {
        this(hash, partKey, filePath, type);
        this.businessName = businessName;
        this.dependsOn = dependsOn;
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
        contentValues.put(InstalledPluginDBHelper.COLUMN_TYPE, type);
        contentValues.put(InstalledPluginDBHelper.COLUMN_UUID, UUID);
        contentValues.put(InstalledPluginDBHelper.COLUMN_VERSION, version);
        contentValues.put(InstalledPluginDBHelper.COLUMN_PATH, filePath);
        return contentValues;
    }
}
