package com.tencent.shadow.core.loader.provider;

import android.net.Uri;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

class SimplePathStrategy implements PathStrategy {

    private final String mAuthority;
    private final String mContainerAuthority;
    private final HashMap<String, File> mRoots = new HashMap<>();

    SimplePathStrategy(String containerAuthority, String authority) {
        mAuthority = authority;
        mContainerAuthority = containerAuthority;
    }

    /**
     * Add a mapping from a name to a filesystem root. The provider only offers
     * access to files that live under configured roots.
     */
    void addRoot(String name, File root) {
        if (TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Name must not be empty");
        }

        try {
            // Resolve to canonical path to keep path checking fast
            root = root.getCanonicalFile();
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "Failed to resolve canonical path for " + root, e);
        }

        mRoots.put(name, root);
    }

    public Uri getUriForFile(File file) {
        String path;
        try {
            path = file.getCanonicalPath();
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to resolve canonical path for " + file);
        }

        // Find the most-specific root path
        Map.Entry<String, File> mostSpecific = null;
        for (Map.Entry<String, File> root : mRoots.entrySet()) {
            final String rootPath = root.getValue().getPath();
            if (path.startsWith(rootPath) && (mostSpecific == null
                    || rootPath.length() > mostSpecific.getValue().getPath().length())) {
                mostSpecific = root;
            }
        }

        if (mostSpecific == null) {
            throw new IllegalArgumentException(
                    "Failed to find configured root that contains " + path);
        }

        // Start at first char of path under root
        final String rootPath = mostSpecific.getValue().getPath();
        if (rootPath.endsWith("/")) {
            path = path.substring(rootPath.length());
        } else {
            path = path.substring(rootPath.length() + 1);
        }

        // Encode the tag and path separately
        path = Uri.encode(mostSpecific.getKey()) + '/' + Uri.encode(path, "/");
        StringBuilder sb = new StringBuilder();
        sb.append("content://")
                .append(mContainerAuthority)
                .append("/")
                .append(mAuthority)
                .append("/")
                .append(path);
        return Uri.parse(sb.toString());
    }

    public File getFileForUri(Uri uri) {
        String path = uri.getEncodedPath();

        final int splitIndex = path.indexOf('/', 1);
        final String tag = Uri.decode(path.substring(1, splitIndex));
        path = Uri.decode(path.substring(splitIndex + 1));

        final File root = mRoots.get(tag);
        if (root == null) {
            throw new IllegalArgumentException("Unable to find configured root for " + uri);
        }

        File file = new File(root, path);
        try {
            file = file.getCanonicalFile();
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to resolve canonical path for " + file);
        }

        if (!file.getPath().startsWith(root.getPath())) {
            throw new SecurityException("Resolved path jumped beyond configured root");
        }

        return file;
    }
}
