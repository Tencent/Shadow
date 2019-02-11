package com.tencent.shadow.runtime;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.tencent.shadow.runtime.container.DelegateProviderHolder;

public class UriConverter {

    public static Uri parse(String uriString) {
        if (DelegateProviderHolder.delegateProvider != null && DelegateProviderHolder.delegateProvider.getUriParseDelegate() != null) {
            return DelegateProviderHolder.delegateProvider.getUriParseDelegate().parse(uriString);
        } else {
            return Uri.parse(uriString);
        }
    }

    public static Uri parseCall(String uriString, Bundle bundle) {
        if (DelegateProviderHolder.delegateProvider != null && DelegateProviderHolder.delegateProvider.getUriParseDelegate() != null) {
            return DelegateProviderHolder.delegateProvider.getUriParseDelegate().parseCall(uriString, bundle);
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
