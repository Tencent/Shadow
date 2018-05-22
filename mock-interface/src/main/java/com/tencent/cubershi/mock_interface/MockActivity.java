package com.tencent.cubershi.mock_interface;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.tencent.hydevteam.pluginframework.plugincontainer.HostActivityDelegator;

public abstract class MockActivity extends ContextWrapper {
    protected Resources mPluginResources;

    private LayoutInflater mLayoutInflater;

    private ClassLoader mPluginClassLoader;

    public MockActivity() {
        super(null);
    }

    protected abstract void onCreate(Bundle savedInstanceState);

    public abstract void setContentView(int layoutResID);

    public abstract void setContainerActivity(HostActivityDelegator delegator);

    public abstract void performOnCreate(Bundle bundle);

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
