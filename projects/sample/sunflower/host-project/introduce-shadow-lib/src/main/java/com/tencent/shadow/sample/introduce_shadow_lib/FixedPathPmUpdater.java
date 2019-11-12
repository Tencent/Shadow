package com.tencent.shadow.sample.introduce_shadow_lib;

import com.tencent.shadow.dynamic.host.PluginManagerUpdater;

import java.io.File;
import java.util.concurrent.Future;

/**
 * 这个Updater没有任何升级能力。直接将指定路径作为其升级结果。
 */
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