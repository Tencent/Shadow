package com.tencent.shadow.runtime;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

public class PluginFragmentManager {
    FragmentManager mBase;

    PluginFragmentManager(FragmentManager mBase) {
        this.mBase = mBase;
    }

    @SuppressLint("CommitTransaction")
    public PluginFragmentTransaction beginTransaction() {
        return new PluginFragmentTransaction(this, mBase.beginTransaction());
    }

    public ShadowFragment findFragmentByTag(String tag) {
        Fragment fragmentByTag = mBase.findFragmentByTag(tag);
        if (fragmentByTag instanceof IContainerFragment) {
            return ((IContainerFragment) fragmentByTag).getPluginFragment();
        } else {
            return null;
        }
    }

    public boolean executePendingTransactions() {
        return mBase.executePendingTransactions();
    }

    @TargetApi(Build.VERSION_CODES.O)
    public List<ShadowFragment> getFragments() {
        List<Fragment> containerFragments = mBase.getFragments();
        if (containerFragments != null && containerFragments.size() > 0) {
            List<ShadowFragment> pluginFragments = new ArrayList<>();
            for (Fragment containerFragment : containerFragments) {
                if (containerFragment instanceof IContainerFragment) {
                    pluginFragments.add(((IContainerFragment) containerFragment).getPluginFragment());
                }
            }
            return pluginFragments;
        }
        return null;
    }
}
