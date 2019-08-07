/*
 * Tencent is pleased to support the open source community by making Tencent Shadow available.
 * Copyright (C) 2019 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tencent.shadow.core.runtime;

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
        Uri containerUri = UriConverter.parse(url.toString());
        return resolver.insert(containerUri, values);
    }

    public static int delete(ContentResolver resolver, Uri url, String where, String[] selectionArgs) {
        Uri containerUri = UriConverter.parse(url.toString());
        return resolver.delete(containerUri, where, selectionArgs);
    }

    public static int update(ContentResolver resolver, Uri uri, ContentValues values, String where, String[] selectionArgs) {
        Uri containerUri = UriConverter.parse(uri.toString());
        return resolver.update(containerUri, values, where, selectionArgs);
    }

    @TargetApi(Build.VERSION_CODES.O)
    public static Cursor query(ContentResolver resolver, Uri uri, String[] projection, Bundle queryArgs,
            CancellationSignal cancellationSignal) {
        Uri containerUri = UriConverter.parse(uri.toString());
        return resolver.query(containerUri, projection, queryArgs, cancellationSignal);
    }

    public static Cursor query(ContentResolver resolver, Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        Uri containerUri = UriConverter.parse(uri.toString());
        return resolver.query(containerUri, projection, selection, selectionArgs, sortOrder);
    }

    public static Cursor query(ContentResolver resolver, Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder,
            CancellationSignal cancellationSignal) {
        Uri containerUri = UriConverter.parse(uri.toString());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return resolver.query(containerUri, projection, selection, selectionArgs, sortOrder, cancellationSignal);
        } else {
            return null;
        }
    }

    public static Bundle call(ContentResolver resolver, Uri uri, String method, String arg, Bundle extras) {
        if (extras == null) {
            extras = new Bundle();
        }
        Uri containerUri = UriConverter.parseCall(uri.toString(), extras);
        return resolver.call(containerUri, method, arg, extras);
    }

    public static int bulkInsert(ContentResolver resolver, Uri url, ContentValues[] values) {
        Uri containerUri = UriConverter.parse(url.toString());
        return resolver.bulkInsert(containerUri, values);
    }


}