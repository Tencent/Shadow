package com.tencent.shadow.runtime;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import java.io.File;

public interface UriParseDelegate {

    Uri parse(String uriString);

    Uri parseCall(String uriString, Bundle extra);

    Uri getUriForFile(Context cxt, String authority, File file);
}