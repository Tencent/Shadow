package com.tencent.shadow.core.runtime;

import android.net.Uri;
import android.os.Bundle;

public interface UriParseDelegate {

    Uri parse(String uriString);

    Uri parseCall(String uriString, Bundle extra);
}