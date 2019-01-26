package com.tencent.shadow.core.loader.provider;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.res.XmlResourceParser;
import android.os.Build;
import android.os.Environment;

import org.jetbrains.annotations.Nullable;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;

import static org.xmlpull.v1.XmlPullParser.END_DOCUMENT;
import static org.xmlpull.v1.XmlPullParser.START_TAG;

public class ProviderUtil {

    private static final String
            META_DATA_FILE_PROVIDER_PATHS = "android.support.FILE_PROVIDER_PATHS";

    private static final String TAG_ROOT_PATH = "root-path";
    private static final String TAG_FILES_PATH = "files-path";
    private static final String TAG_CACHE_PATH = "cache-path";
    private static final String TAG_EXTERNAL = "external-path";
    private static final String TAG_EXTERNAL_FILES = "external-files-path";
    private static final String TAG_EXTERNAL_CACHE = "external-cache-path";

    private static final String ATTR_NAME = "name";
    private static final String ATTR_PATH = "path";

    private static final File DEVICE_ROOT = new File("/");

    @Nullable
    public static PathStrategy parsePathStrategy(Context context,
            String containerAuthority, String authority)
            throws IOException, XmlPullParserException {
        final SimplePathStrategy strat = new SimplePathStrategy(containerAuthority, authority);

        final ProviderInfo info = context.getPackageManager()
                .resolveContentProvider(authority, PackageManager.GET_META_DATA);
        final XmlResourceParser in = info.loadXmlMetaData(
                context.getPackageManager(), META_DATA_FILE_PROVIDER_PATHS);
        if (in == null) {
            return null;
        }

        int type;
        while ((type = in.next()) != END_DOCUMENT) {
            if (type == START_TAG) {
                final String tag = in.getName();

                final String name = in.getAttributeValue(null, ATTR_NAME);
                String path = in.getAttributeValue(null, ATTR_PATH);

                File target = null;
                if (TAG_ROOT_PATH.equals(tag)) {
                    target = DEVICE_ROOT;
                } else if (TAG_FILES_PATH.equals(tag)) {
                    target = context.getFilesDir();
                } else if (TAG_CACHE_PATH.equals(tag)) {
                    target = context.getCacheDir();
                } else if (TAG_EXTERNAL.equals(tag)) {
                    target = Environment.getExternalStorageDirectory();
                } else if (TAG_EXTERNAL_FILES.equals(tag)) {
                    File[] externalFilesDirs = getExternalFilesDirs(context, null);
                    if (externalFilesDirs.length > 0) {
                        target = externalFilesDirs[0];
                    }
                } else if (TAG_EXTERNAL_CACHE.equals(tag)) {
                    File[] externalCacheDirs = getExternalCacheDirs(context);
                    if (externalCacheDirs.length > 0) {
                        target = externalCacheDirs[0];
                    }
                }

                if (target != null) {
                    strat.addRoot(name, buildPath(target, path));
                }
            }
        }

        return strat;
    }

    private static File buildPath(File base, String... segments) {
        File cur = base;
        for (String segment : segments) {
            if (segment != null) {
                cur = new File(cur, segment);
            }
        }
        return cur;
    }

    public static File[] getExternalFilesDirs(Context context, String type) {
        if (Build.VERSION.SDK_INT >= 19) {
            return context.getExternalFilesDirs(type);
        } else {
            return new File[]{context.getExternalFilesDir(type)};
        }
    }

    public static File[] getExternalCacheDirs(Context context) {
        if (Build.VERSION.SDK_INT >= 19) {
            return context.getExternalCacheDirs();
        } else {
            return new File[]{context.getExternalCacheDir()};
        }
    }
}
