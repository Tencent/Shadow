package com.tencent.cubershi.mock_interface;

import android.app.FragmentManager;

public class PluginFragmentManager {
    FragmentManager mBase;

    PluginFragmentManager(FragmentManager mBase) {
        this.mBase = mBase;
    }

    public PluginFragmentTransaction beginTransaction() {
        return new PluginFragmentTransaction(mBase.beginTransaction());
    }

}
