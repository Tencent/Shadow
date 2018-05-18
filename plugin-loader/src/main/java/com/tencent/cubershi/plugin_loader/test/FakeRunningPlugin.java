package com.tencent.cubershi.plugin_loader.test;

import android.app.Application;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.tencent.hydevteam.common.progress.ProgressFuture;
import com.tencent.hydevteam.pluginframework.installedplugin.InstalledPlugin;
import com.tencent.hydevteam.pluginframework.pluginloader.RunningPlugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FakeRunningPlugin implements RunningPlugin {
    private static final Logger mLogger = LoggerFactory.getLogger(FakeRunningPlugin.class);
    InstalledPlugin installedPlugin;
    Application mockApplication;

    public FakeRunningPlugin(Application mockApplication, InstalledPlugin installedPlugin) {
        this.mockApplication = mockApplication;
        this.installedPlugin = installedPlugin;
    }

    @Override
    public ProgressFuture startLauncherActivity(Intent intent) {
        return new ProgressFuture() {
            @Override
            public double getProgress() {
                return 1;
            }

            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return false;
            }

            @Override
            public boolean isCancelled() {
                return false;
            }

            @Override
            public boolean isDone() {
                return true;
            }

            @Override
            public Object get() throws InterruptedException, ExecutionException {
                if (mLogger.isInfoEnabled()) {
                    mLogger.info("startLauncherActivity path=={}", installedPlugin.pluginFile.getAbsolutePath());
                }
                return null;
            }

            @Override
            public Object get(long timeout, @NonNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                return null;
            }
        };
    }

    @Override
    public ProgressFuture startInitActivity(Intent intent) {
        return null;
    }

    @Override
    public void unload() {

    }
}
