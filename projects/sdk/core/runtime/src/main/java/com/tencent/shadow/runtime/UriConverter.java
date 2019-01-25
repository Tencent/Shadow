package com.tencent.shadow.runtime;

import android.net.Uri;
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


}
