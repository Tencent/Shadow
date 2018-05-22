package com.tencent.cubershi.mock_interface;

import android.content.res.Resources;
import android.os.Bundle;

import com.tencent.hydevteam.pluginframework.plugincontainer.HostActivityDelegator;

public abstract class MockActivity {
    protected abstract void onCreate(Bundle savedInstanceState);

    public abstract void setContentView(int layoutResID);

    public abstract void setContainerActivity(HostActivityDelegator delegator);

    public abstract void performOnCreate(Bundle bundle);

    public abstract void setPluginResources(Resources resources);
}
