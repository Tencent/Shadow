package com.tencent.cubershi.mock_interface;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.os.Bundle;

import com.tencent.hydevteam.pluginframework.plugincontainer.HostActivityDelegator;

public abstract class MockActivity extends ContextWrapper {
    public MockActivity() {
        super(null);
    }

    protected abstract void onCreate(Bundle savedInstanceState);

    public abstract void setContentView(int layoutResID);

    public abstract void setContainerActivity(HostActivityDelegator delegator);

    public abstract void performOnCreate(Bundle bundle);

    public abstract void setPluginResources(Resources resources);

    public final void setHostContextAsBase(Context context) {
        attachBaseContext(context);
    }
}
