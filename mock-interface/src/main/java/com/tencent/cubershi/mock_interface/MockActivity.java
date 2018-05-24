package com.tencent.cubershi.mock_interface;

import android.content.Context;
import android.content.ContextWrapper;
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

    public MockActivity() {
        super(null);
    }

    public void setContainerActivity(HostActivityDelegator delegator) {
        mHostActivityDelegator = delegator;
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
}
