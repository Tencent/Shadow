package com.tencent.shadow.test.host.dynamic.app.manager;

import com.tencent.shadow.dynamic.host.PluginManagerUpdater;

import java.io.File;
import java.util.concurrent.Future;

public class FixedPathPmUpdater implements PluginManagerUpdater {

    final private File apk;

    FixedPathPmUpdater(File apk) {
        this.apk = apk;
    }


    @Override
    public boolean wasUpdating() {
        return false;
    }

    @Override
    public Future<File> update() {
        return null;
    }

    @Override
    public File getLatest() {
        return apk;
    }

    @Override
    public Future<Boolean> isAvailable(final File file) {
        return null;
    }
}