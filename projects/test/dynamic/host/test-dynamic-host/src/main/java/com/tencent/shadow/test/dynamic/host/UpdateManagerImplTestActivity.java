package com.tencent.shadow.test.dynamic.host;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.tencent.shadow.dynamic.host.DynamicPluginManager;
import com.tencent.shadow.dynamic.host.PluginManagerUpdater;
import com.tencent.shadow.test.lib.constant.Constant;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class UpdateManagerImplTestActivity extends Activity {

    private DynamicPluginManager dynamicPluginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final PluginManagerUpdater pluginManagerUpdater = new DumbUpdater(
                PluginHelper.getInstance().dumbManagerFile,
                PluginHelper.getInstance().pluginManagerFile
        );

        dynamicPluginManager = new DynamicPluginManager(pluginManagerUpdater);
        dynamicPluginManager.enter(this, Constant.FROM_ID_NOOP, null, null);
        String firstMangerImpl = dynamicPluginManager.getManagerImpl().getClass().getName();
        dynamicPluginManager.enter(this, Constant.FROM_ID_NOOP, null, null);
        String secondMangerImpl = dynamicPluginManager.getManagerImpl().getClass().getName();

        TextView textView = new TextView(this);
        textView.setText(firstMangerImpl + "/" + secondMangerImpl);
        textView.setTag("ImplName");
        setContentView(textView);
    }

    @Override
    protected void onDestroy() {
        dynamicPluginManager.release();
        super.onDestroy();
    }
}

class DumbUpdater implements PluginManagerUpdater {

    final private File dumbManagerApk;
    final private File workedManagerApk;
    private boolean updated = false;

    DumbUpdater(File dumbManagerApk, File workedManagerApk) {
        this.dumbManagerApk = dumbManagerApk;
        this.workedManagerApk = workedManagerApk;
    }


    @Override
    public boolean wasUpdating() {
        return false;
    }

    @Override
    public Future<File> update() {
        updated = true;
        return new Future<File>() {
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
            public File get() throws ExecutionException, InterruptedException {
                return workedManagerApk;
            }

            @Override
            public File get(long timeout, TimeUnit unit) throws ExecutionException, InterruptedException, TimeoutException {
                return workedManagerApk;
            }
        };
    }

    @Override
    public File getLatest() {
        if (!updated) {
            return dumbManagerApk;
        } else {
            return workedManagerApk;
        }
    }

    @Override
    public Future<Boolean> isAvailable(final File file) {
        return null;
    }
}