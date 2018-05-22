package com.tencent.cubershi.plugin_loader.blocs;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;

import static android.content.pm.PackageManager.GET_META_DATA;

public class CreateResourceBloc {
    public static Resources create(String archiveFilePath, Context hostAppContext) {
        final PackageManager packageManager = hostAppContext.getPackageManager();
        final PackageInfo packageArchiveInfo = packageManager.getPackageArchiveInfo(archiveFilePath, GET_META_DATA);
        packageArchiveInfo.applicationInfo.publicSourceDir = archiveFilePath;
        try {
            return packageManager.getResourcesForApplication(packageArchiveInfo.applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
