package com.tencent.cubershi.mock_interface;

import android.app.Fragment;
import android.app.FragmentManager;

public class PluginFragmentManager {
    FragmentManager mBase;

    PluginFragmentManager(FragmentManager mBase) {
        this.mBase = mBase;
    }

    public PluginFragmentTransaction beginTransaction() {
        return new PluginFragmentTransaction(mBase.beginTransaction());
    }

    public MockFragment findFragmentByTag(String tag) {
        Fragment fragmentByTag = mBase.findFragmentByTag(tag);
        if (fragmentByTag instanceof ContainerFragment) {
            return ((ContainerFragment) fragmentByTag).getPluginFragment();
        } else {
            return null;
        }
    }

    public boolean executePendingTransactions() {
        return mBase.executePendingTransactions();
    }
}
