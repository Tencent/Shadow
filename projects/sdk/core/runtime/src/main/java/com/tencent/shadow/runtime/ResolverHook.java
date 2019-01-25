package com.tencent.shadow.runtime;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;

public class ResolverHook {

    private static final String TAG = "ResolverHook";

    public static Uri insert(ContentResolver resolver, Uri url, ContentValues values) {
        Log.d(TAG, "insert url = " + url);
        Uri pluginUri = UriConverter.parse(url.toString());
        return resolver.insert(pluginUri, values);
    }

    public static int delete(ContentResolver resolver, Uri url, String where, String[] selectionArgs) {
        Log.d(TAG, "delete url = " + url);
        Uri pluginUri = UriConverter.parse(url.toString());
        return resolver.delete(pluginUri, where, selectionArgs);
    }

    public static int update(ContentResolver resolver, Uri uri, ContentValues values, String where, String[] selectionArgs) {
        Log.d(TAG, "update url = " + uri);
        Uri pluginUri = UriConverter.parse(uri.toString());
        return resolver.update(pluginUri, values, where, selectionArgs);
    }

    @TargetApi(Build.VERSION_CODES.O)
    public static Cursor query(ContentResolver resolver, Uri uri, String[] projection, Bundle queryArgs,
            CancellationSignal cancellationSignal) {
        Log.d(TAG, "update url = " + uri);
        Uri pluginUri = UriConverter.parse(uri.toString());
        return resolver.query(pluginUri, projection, queryArgs, cancellationSignal);
    }

    public static Cursor query(ContentResolver resolver, Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "update url = " + uri);
        Uri pluginUri = UriConverter.parse(uri.toString());
        return resolver.query(pluginUri, projection, selection, selectionArgs, sortOrder);
    }

    public static Cursor query(ContentResolver resolver, Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder,
            CancellationSignal cancellationSignal) {
        Log.d(TAG, "update url = " + uri);
        Uri pluginUri = UriConverter.parse(uri.toString());
        return resolver.query(pluginUri, projection, selection, selectionArgs, sortOrder, cancellationSignal);
    }

    public static Bundle call(ContentResolver resolver, Uri uri, String method, String arg, Bundle extras) {
        Log.d(TAG, "call url = " + uri);
        Uri pluginUri = UriConverter.parse(uri.toString());
        return resolver.call(pluginUri, method, arg, extras);
    }

    public static int bulkInsert(ContentResolver resolver, Uri url, ContentValues[] values) {
        Log.d(TAG, "bulkInsert url = " + url);
        Uri pluginUri = UriConverter.parse(url.toString());
        return resolver.bulkInsert(pluginUri, values);
    }

    public static void registerContentObserver(ContentResolver resolver, Uri uri, boolean notifyForDescendants,
            ContentObserver observer) {
        Log.d(TAG, "registerContentObserver url = " + uri);
        Uri pluginUri = UriConverter.parse(uri.toString());
        resolver.registerContentObserver(pluginUri, notifyForDescendants, observer);
    }

    public static void notifyChange(ContentResolver resolver, Uri uri, ContentObserver observer) {
        Log.d(TAG, "notifyChange url = " + uri);
        Uri pluginUri = UriConverter.parse(uri.toString());
        resolver.notifyChange(pluginUri, observer);
    }

    public static void notifyChange(ContentResolver resolver, Uri uri, ContentObserver observer,
            boolean syncToNetwork) {
        Log.d(TAG, "notifyChange url = " + uri);
        Uri pluginUri = UriConverter.parse(uri.toString());
        resolver.notifyChange(pluginUri, observer, syncToNetwork);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static void notifyChange(ContentResolver resolver, Uri uri, ContentObserver observer, int flags) {
        Log.d(TAG, "notifyChange url = " + uri);
        Uri pluginUri = UriConverter.parse(uri.toString());
        resolver.notifyChange(pluginUri, observer, flags);
    }
}