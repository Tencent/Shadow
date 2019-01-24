package com.tencent.shadow.runtime;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;

public class ContentResolverWrapper extends ContentResolver {

    private static final String TAG = "ContentResolverWrapper";

    private ContentResolver mBase;

    public ContentResolverWrapper(Context cxt, ContentResolver resolver) {
        super(cxt);
        mBase = resolver;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public Cursor query_(Uri uri, String[] projection, Bundle queryArgs,
            CancellationSignal cancellationSignal) {
        return mBase.query(uri, projection, queryArgs, cancellationSignal);
    }

    public Cursor query_(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        return mBase.query(uri, projection, selection, selectionArgs, sortOrder);
    }

    public final Cursor query_(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder,
            CancellationSignal cancellationSignal) {
        return mBase.query(uri, projection, selection, selectionArgs, sortOrder, cancellationSignal);
    }

    public Uri insert_(Uri url, ContentValues values) {
        return mBase.insert(url, values);
    }

    public int delete_(Uri url, String where, String[] selectionArgs) {
        return mBase.delete(url, where, selectionArgs);
    }

    public int update_(Uri uri, ContentValues values, String where, String[] selectionArgs) {
        return mBase.update(uri, values, where, selectionArgs);
    }

    public Bundle call_(Uri uri, String method, String arg, Bundle extras) {
        //TODO
        Log.d(TAG, "call_ uri = " + uri);
        return mBase.call(uri, method, arg, extras);
    }

    public void notifyChange_(Uri uri, ContentObserver observer) {
        //TODO
        Log.d(TAG, "notifyChange_ uri = " + uri);
        mBase.notifyChange(uri, observer);
    }

    public void notifyChange_(Uri uri, ContentObserver observer, boolean syncToNetwork) {
        //TODO
        Log.d(TAG, "notifyChange_ uri = " + uri);

    }

    @TargetApi(Build.VERSION_CODES.N)
    public void notifyChange_(Uri uri, ContentObserver observer, int flags) {
        //TODO
        Log.d(TAG, "notifyChange_ uri = " + uri);
        mBase.notifyChange(uri, observer, flags);
    }

    public int bulkInsert_(Uri url, ContentValues[] values) {
        return mBase.bulkInsert(url, values);
    }
}