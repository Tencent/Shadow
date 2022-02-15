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
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

public class UriConverter {

    private static UriParseDelegate sUriParseDelegate;

    public static void setUriParseDelegate(UriParseDelegate sUriParseDelegate) {
        UriConverter.sUriParseDelegate = sUriParseDelegate;
    }

    public interface UriParseDelegate {

        Uri parse(String uriString);

        Uri parseCall(String uriString, Bundle extra);
    }

    public static Uri parse(String uriString) {
        if (sUriParseDelegate != null) {
            return sUriParseDelegate.parse(uriString);
        } else {
            return Uri.parse(uriString);
        }
    }

    public static Uri parseCall(String uriString, Bundle bundle) {
        if (sUriParseDelegate != null) {
            return sUriParseDelegate.parseCall(uriString, bundle);
        } else {
            return Uri.parse(uriString);
        }
    }

    public static Uri build(Uri.Builder builder) {
        String uri = builder.build().toString();
        return parse(uri);
    }

    public static Bundle call(ContentResolver resolver, Uri uri, String method, String arg, Bundle extras) {
        if (extras == null) {
            extras = new Bundle();
        }
        Uri containerUri = UriConverter.parseCall(uri.toString(), extras);
        return resolver.call(containerUri, method, arg, extras);
    }

    public static void notifyChange(ContentResolver resolver, Uri uri, ContentObserver observer) {
        Uri containerUri = UriConverter.parse(uri.toString());
        resolver.notifyChange(containerUri, observer);
    }

    public static void notifyChange(ContentResolver resolver, Uri uri, ContentObserver observer,
                                    boolean syncToNetwork) {
        Uri containerUri = UriConverter.parse(uri.toString());
        resolver.notifyChange(containerUri, observer, syncToNetwork);
    }

    @TargetApi(Build.VERSION_CODES.O)
    public static void notifyChange(ContentResolver resolver, Uri uri, ContentObserver observer,
                                    int flags) {
        Uri containerUri = UriConverter.parse(uri.toString());
        resolver.notifyChange(containerUri, observer, flags);
    }
}
