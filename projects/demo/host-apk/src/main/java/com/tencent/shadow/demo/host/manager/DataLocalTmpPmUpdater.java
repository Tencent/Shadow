package com.tencent.shadow.demo.host.manager;

import com.tencent.shadow.dynamic.host.PluginManagerUpdater;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DataLocalTmpPmUpdater implements PluginManagerUpdater {

    final private ExecutorService normalExecutor = Executors.newSingleThreadExecutor();
    final private File apk;

    DataLocalTmpPmUpdater(File apk) {
        this.apk = apk;
    }


    @Override
    public boolean wasUpdating() {
        return false;
    }

    @Override
    public Future<File> update() {
        return normalExecutor.submit(new Callable<File>() {
            @Override
            public File call() throws Exception {
                if (apk.exists()) {
                    return apk;
                } else {
                    return null;
                }
            }
        });
    }

    @Override
    public File getLatest() {
        if (apk.exists()) {
            return apk;
        } else {
            return null;
        }
    }

    @Override
    public Future<Boolean> isAvailable(final File file) {
        return normalExecutor.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return apk.exists() && file == apk;
            }
        });
    }
}