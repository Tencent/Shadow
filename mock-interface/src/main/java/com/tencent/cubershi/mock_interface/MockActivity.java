package com.tencent.cubershi.mock_interface;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.tencent.hydevteam.pluginframework.plugincontainer.HostActivityDelegator;

public abstract class MockActivity extends ContextWrapper {
    private HostActivityDelegator mHostActivityDelegator;

    private Resources mPluginResources;

    private LayoutInflater mLayoutInflater;

    private ClassLoader mPluginClassLoader;

    private PluginActivityLauncher mPluginActivityLauncher;

    private Application mPluginApplication;

    public MockActivity() {
        super(null);
    }

    public void setContainerActivity(HostActivityDelegator delegator) {
        mHostActivityDelegator = delegator;
    }

    public void setPluginActivityLauncher(PluginActivityLauncher pluginActivityLauncher) {
        mPluginActivityLauncher = pluginActivityLauncher;
    }

    public void setPluginApplication(MockApplication pluginApplication) {
        mPluginApplication = (Application) pluginApplication;
    }

    protected void onCreate(Bundle savedInstanceState) {
        //do nothing.
    }

    public void performOnCreate(Bundle bundle) {
        onCreate(bundle);
    }

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

    public final void setPluginResources(Resources resources) {
        mPluginResources = resources;
    }

    public final void setHostContextAsBase(Context context) {
        attachBaseContext(context);
    }

    public final void setPluginClassLoader(ClassLoader classLoader) {
        mPluginClassLoader = classLoader;
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

    public final Application getApplication() {
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

    public interface PluginActivityLauncher {
        /**
         * 启动Actvity
         *
         * @param context 启动context
         * @param intent  插件内传来的Intent.
         * @return <code>true</code>表示该Intent是为了启动插件内Activity的,已经被正确消费了.
         * <code>false</code>表示该Intent不是插件内的Activity.
         */
        boolean startActivity(Context context, Intent intent);

    }
}
