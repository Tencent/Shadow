package com.tencent.cubershi.mock_interface;

import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public abstract class MockActivity extends PluginActivity {

    private LayoutInflater mLayoutInflater;

    private MixPackageManager mMixPackageManager;

    public void setContentView(int layoutResID) {
        final View inflate = LayoutInflater.from(this).inflate(layoutResID, null);
        mHostActivityDelegator.setContentView(inflate);
    }

    public Intent getIntent() {
        return mHostActivityDelegator.getIntent();
    }

    public void setContentView(View view) {
        mHostActivityDelegator.setContentView(view);
    }

    @Override
    public Resources getResources() {
        return mPluginResources;
    }

    @Override
    public Object getSystemService(String name) {
        if (LAYOUT_INFLATER_SERVICE.equals(name)) {
            if (mLayoutInflater == null) {
                LayoutInflater inflater = (LayoutInflater) super.getSystemService(name);
                assert inflater != null;
                mLayoutInflater = inflater.cloneInContext(this);
            }
            return mLayoutInflater;
        }
        return super.getSystemService(name);
    }

    @Override
    public ClassLoader getClassLoader() {
        return mPluginClassLoader;
    }

    public final MockApplication getApplication() {
        return mPluginApplication;
    }

    @Override
    public void startActivity(Intent intent) {
        final Intent pluginIntent = new Intent(intent);
        pluginIntent.setExtrasClassLoader(mPluginClassLoader);
        final boolean success = mPluginActivityLauncher.startActivity(this, pluginIntent);
        if (!success) {
            super.startActivity(intent);
        }
    }

    public FragmentManager getFragmentManager() {
        return mHostActivityDelegator.getFragmentManager();
    }

    public Object getLastNonConfigurationInstance() {
        return mHostActivityDelegator.getLastNonConfigurationInstance();
    }

    public Window getWindow() {
        return mHostActivityDelegator.getWindow();
    }

    public <T extends View> T findViewById(int id) {
        return mHostActivityDelegator.findViewById(id);
    }

    public WindowManager getWindowManager() {
        return mHostActivityDelegator.getWindowManager();
    }

    @Override
    public void setPluginPackageManager(PackageManager pluginPackageManager) {
        super.setPluginPackageManager(pluginPackageManager);
        mMixPackageManager = new MixPackageManager(super.getPackageManager(), pluginPackageManager);
    }

    @Override
    public PackageManager getPackageManager() {
        return mMixPackageManager;
    }
    @Override
    public void unbindService(ServiceConnection conn) {
        super.unbindService(conn);
    }

    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        return super.bindService(service, conn, flags);
    }

    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }

    @Override
    public ComponentName startService(Intent service) {
        return super.startService(service);
    }
}
