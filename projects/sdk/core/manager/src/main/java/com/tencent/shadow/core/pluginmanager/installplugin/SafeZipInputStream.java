package com.tencent.shadow.core.pluginmanager.installplugin;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

final class SafeZipInputStream extends ZipInputStream {
    SafeZipInputStream(InputStream stream) {
        super(stream);
    }

    public final ZipEntry getNextEntry() throws IOException {
        ZipEntry entry = super.getNextEntry();
        if (null != entry) {
            String name = entry.getName();
            if (null != name && (name.contains("../") || name.contains("..\\"))) {
                throw new SecurityException("非法entry路径:" + entry.getName());
            }
        }
        return entry;
    }

}