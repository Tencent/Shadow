package com.tencent.shadow.sdk.pluginmanager.installplugin;

import android.content.ContentValues;

public class InstalledRow {

    public final static int TYPE_PLUGIN = 1;

    public final static int TYPE_INTERFACE = 2;

    public final static int TYPE_PLUGIN_LOADER = 3;

    public final static int TYPE_PLUGIN_RUNTIME = 4;

    public final static int TYPE_UUID = 5;


    public String hash;

    public long installedTime;

    public String partKey;

    public String filePath;

    public int type;

    public String UUID;

    public String version;

    public String appId;

    public InstalledRow() {
    }

    public InstalledRow(String hash, String partKey, String filePath, int type) {
        this.hash = hash;
        this.partKey = partKey;
        this.filePath = filePath;
        this.type = type;
    }

    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(InstalledPluginDBHelper.COLUMN_APPID, appId);
        contentValues.put(InstalledPluginDBHelper.COLUMN_HASH, hash);
        contentValues.put(InstalledPluginDBHelper.COLUMN_INSTALL_TIME, installedTime);
        if (partKey != null) {
            contentValues.put(InstalledPluginDBHelper.COLUMN_PARTKEY, partKey);
        }
        contentValues.put(InstalledPluginDBHelper.COLUMN_TYPE, type);
        contentValues.put(InstalledPluginDBHelper.COLUMN_UUID, UUID);
        contentValues.put(InstalledPluginDBHelper.COLUMN_VERSION, version);
        contentValues.put(InstalledPluginDBHelper.COLUMN_PATH, filePath);
        return contentValues;
    }
}
