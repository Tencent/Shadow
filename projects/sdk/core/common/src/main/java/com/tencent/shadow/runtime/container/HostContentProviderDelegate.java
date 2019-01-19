package com.tencent.shadow.runtime.container;

import android.content.ContentValues;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;

/**
 * PluginContainerContentProvider的被委托者接口
 * <p>
 * 被委托者通过实现这个接口中声明的方法达到替代委托者实现的目的，从而将PluginContainerContentProvider的行为动态化。
 *
 * @author owenguo
 */
public interface HostContentProviderDelegate {

    boolean onCreate();

    void onConfigurationChanged(Configuration newConfig);

    void onLowMemory();

    void onTrimMemory(int level);

    void setDelegator(HostContentProviderDelegator delegator);

    Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder);

    String getType(Uri uri);

    Uri insert(Uri uri, ContentValues values);

    int delete(Uri uri, String selection, String[] selectionArgs);

    int update(Uri uri, ContentValues values, String selection, String[] selectionArgs);

    int bulkInsert( Uri uri,  ContentValues[] values);

}
