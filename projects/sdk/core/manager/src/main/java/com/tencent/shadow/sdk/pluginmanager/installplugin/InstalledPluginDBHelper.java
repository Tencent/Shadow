package com.tencent.shadow.sdk.pluginmanager.installplugin;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class InstalledPluginDBHelper extends SQLiteOpenHelper {

    private static InstalledPluginDBHelper sDBHelper;

    /**
     * 数据库名称
     */
    private final static String DB_NAME = "shadow_installed_plugin_db";
    /**
     * 表名称
     */
    public final static String TABLE_NAME_MANAGER = "shadowPluginManager";

    /**
     * 插件的hash
     */
    public final static String COLUMN_HASH = "hash";
    /**
     * 插件的类型
     */
    public final static String COLUMN_TYPE = "type";
    /**
     * 插件的路径
     */
    public final static String COLUMN_PATH = "filePath";
    /**
     * 插件的名称
     */
    public final static String COLUMN_PARTKEY = "partKey";
    /**
     * 插件的uuid
     */
    public final static String COLUMN_UUID = "uuid";
    /**
     * 插件的版本号
     */
    public final static String COLUMN_VERSION = "version";
    /**
     * 插件的APPID
     */
    public final static String COLUMN_APPID = "appId";
    /**
     * 插件的安装时间
     */
    public final static String COLUMN_INSTALL_TIME = "installedTime";
    /**
     * 数据库的版本号
     */
    private final static int VERSION = 1;

    public static InstalledPluginDBHelper getInstance(Context context) {
        if (sDBHelper == null) {
            Context applicationContext = context.getApplicationContext();
            sDBHelper = new InstalledPluginDBHelper(applicationContext);
        }
        return sDBHelper;
    }

    public InstalledPluginDBHelper(Context context) {
        this(context, DB_NAME, null, VERSION);
    }

    public InstalledPluginDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_MANAGER + " ( "
                + COLUMN_HASH + " VARCHAR , "
                + COLUMN_PATH + " VARCHAR, "
                + COLUMN_TYPE + " INTEGER, "
                + COLUMN_PARTKEY + " VARCHAR, "
                + COLUMN_UUID + " VARCHAR, "
                + COLUMN_VERSION + " VARCHAR, "
                + COLUMN_APPID + " VARCHAR, "
                + COLUMN_INSTALL_TIME + " INTEGER ,"
                + " PRIMARY KEY (" + COLUMN_UUID + "," + COLUMN_APPID + "," + COLUMN_PATH + ")"
                + ");";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
