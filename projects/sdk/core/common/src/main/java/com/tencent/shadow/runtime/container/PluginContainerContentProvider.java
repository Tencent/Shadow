package com.tencent.shadow.runtime.container;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class PluginContainerContentProvider extends ContentProvider {

    private final static String TAG = "ContentProvider_";

    private HostContentProviderDelegate hostContentProviderDelegate;


    public PluginContainerContentProvider() {
        ContentProviderDelegateProviderHolder.setDelegateProviderHolderPrepareListener(new ContentProviderDelegateProviderHolder.DelegateProviderHolderPrepareListener() {
            @Override
            public void onPrepare() {
                HostContentProviderDelegate delegate;
                if (ContentProviderDelegateProviderHolder.contentProviderDelegateProvider != null) {
                    delegate = ContentProviderDelegateProviderHolder.contentProviderDelegateProvider.getHostContentProviderDelegate();
                    delegate.onCreate();
                } else {
                    Log.e(TAG, "PluginContainerContentProvider: DelegateProviderHolder没有初始化");
                    delegate = null;
                }
                hostContentProviderDelegate = delegate;
            }
        });
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (hostContentProviderDelegate != null) {
            return hostContentProviderDelegate.query(uri, projection, selection, selectionArgs, sortOrder);
        }
        return null;
    }

    @Override
    public String getType(Uri uri) {
        if (hostContentProviderDelegate != null) {
            return hostContentProviderDelegate.getType(uri);
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (hostContentProviderDelegate != null) {
            return hostContentProviderDelegate.insert(uri, values);
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (hostContentProviderDelegate != null) {
            return hostContentProviderDelegate.delete(uri, selection, selectionArgs);
        }
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (hostContentProviderDelegate != null) {
            return hostContentProviderDelegate.update(uri, values, selection, selectionArgs);
        }
        return 0;
    }


    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        if (hostContentProviderDelegate != null) {
            return hostContentProviderDelegate.bulkInsert(uri, values);
        }
        return 0;
    }

    @Override
    public Bundle call(String method, String arg, Bundle extras) {
        if (hostContentProviderDelegate != null) {
            return hostContentProviderDelegate.call(method, arg, extras);
        }
        return null;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (hostContentProviderDelegate != null) {
            hostContentProviderDelegate.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onLowMemory() {
        if (hostContentProviderDelegate != null) {
            hostContentProviderDelegate.onLowMemory();
        }
    }

    @Override
    public void onTrimMemory(int level) {
        if (hostContentProviderDelegate != null) {
            hostContentProviderDelegate.onTrimMemory(level);
        }
    }

}
