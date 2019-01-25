package com.tencent.shadow.runtime;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;

public class ResolverHook {

    public static Uri insert(ContentResolver resolver, Uri url, ContentValues values) {
        Uri pluginUri = UriConverter.parse(url.toString());
        return resolver.insert(pluginUri, values);
    }

    public static int delete(ContentResolver resolver, Uri url, String where, String[] selectionArgs) {
        Uri pluginUri = UriConverter.parse(url.toString());
        return resolver.delete(pluginUri, where, selectionArgs);
    }

    public static int update(ContentResolver resolver, Uri uri, ContentValues values, String where, String[] selectionArgs) {
        Uri pluginUri = UriConverter.parse(uri.toString());
        return resolver.update(pluginUri, values, where, selectionArgs);
    }

    @TargetApi(Build.VERSION_CODES.O)
    public static Cursor query(ContentResolver resolver, Uri uri, String[] projection, Bundle queryArgs,
            CancellationSignal cancellationSignal) {
        Uri pluginUri = UriConverter.parse(uri.toString());
        return resolver.query(pluginUri, projection, queryArgs, cancellationSignal);
    }

    public static Cursor query(ContentResolver resolver, Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        Uri pluginUri = UriConverter.parse(uri.toString());
        return resolver.query(pluginUri, projection, selection, selectionArgs, sortOrder);
    }

    public static Cursor query(ContentResolver resolver, Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder,
            CancellationSignal cancellationSignal) {
        Uri pluginUri = UriConverter.parse(uri.toString());
        return resolver.query(pluginUri, projection, selection, selectionArgs, sortOrder, cancellationSignal);
    }

    public static Bundle call(ContentResolver resolver, Uri uri, String method, String arg, Bundle extras) {
        if (extras == null) {
            extras = new Bundle();
        }
        Uri pluginUri = UriConverter.parseCall(uri.toString(), extras);
        return resolver.call(pluginUri, method, arg, extras);
    }

    public static int bulkInsert(ContentResolver resolver, Uri url, ContentValues[] values) {
        Uri pluginUri = UriConverter.parse(url.toString());
        return resolver.bulkInsert(pluginUri, values);
    }
}