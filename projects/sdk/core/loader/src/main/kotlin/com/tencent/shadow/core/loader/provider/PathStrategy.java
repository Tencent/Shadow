package com.tencent.shadow.core.loader.provider;

import android.net.Uri;

import java.io.File;

public interface PathStrategy {
    Uri getUriForFile(File file);

    File getFileForUri(Uri uri);
}
