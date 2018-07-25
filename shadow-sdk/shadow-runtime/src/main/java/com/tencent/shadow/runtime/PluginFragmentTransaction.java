package com.tencent.shadow.runtime;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Build;
import android.view.View;

public class PluginFragmentTransaction {
    final FragmentTransaction mBase;
    final PluginFragmentManager mPluginFragmentManager;

    PluginFragmentTransaction(PluginFragmentManager pluginFragmentManager, FragmentTransaction mBase) {
        this.mPluginFragmentManager = pluginFragmentManager;
        this.mBase = mBase;
    }

    private Fragment getContainerFragment(ShadowFragment shadowFragment) {
        shadowFragment.mPluginFragmentManager = mPluginFragmentManager;
        return shadowFragment.getContainerFragment().asFragment();
    }

    public PluginFragmentTransaction add(ShadowFragment fragment, String tag) {
        mBase.add(getContainerFragment(fragment), tag);
        return this;
    }

    public PluginFragmentTransaction add(int containerViewId, ShadowFragment fragment) {
        mBase.add(containerViewId, getContainerFragment(fragment));
        return this;
    }

    public PluginFragmentTransaction add(int containerViewId, ShadowFragment fragment, String tag) {
        mBase.add(containerViewId, getContainerFragment(fragment), tag);
        return this;
    }

    public PluginFragmentTransaction replace(int containerViewId, ShadowFragment fragment) {
        mBase.replace(containerViewId, getContainerFragment(fragment));
        return this;
    }

    public PluginFragmentTransaction replace(int containerViewId, ShadowFragment fragment, String tag) {
        mBase.replace(containerViewId, getContainerFragment(fragment), tag);
        return this;
    }

    public PluginFragmentTransaction remove(ShadowFragment fragment) {
        mBase.remove(getContainerFragment(fragment));
        return this;
    }

    public PluginFragmentTransaction hide(ShadowFragment fragment) {
        mBase.hide(getContainerFragment(fragment));
        return this;
    }

    public PluginFragmentTransaction show(ShadowFragment fragment) {
        mBase.show(getContainerFragment(fragment));
        return this;
    }

    public PluginFragmentTransaction detach(ShadowFragment fragment) {
        mBase.detach(getContainerFragment(fragment));
        return this;
    }

    public PluginFragmentTransaction attach(ShadowFragment fragment) {
        mBase.attach(getContainerFragment(fragment));
        return this;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public PluginFragmentTransaction setPrimaryNavigationFragment(ShadowFragment fragment) {
        mBase.setPrimaryNavigationFragment(getContainerFragment(fragment));
        return this;
    }

    public boolean isEmpty() {
        return mBase.isEmpty();
    }

    public PluginFragmentTransaction setCustomAnimations(int enter, int exit) {
        mBase.setCustomAnimations(enter, exit);
        return this;
    }

    public PluginFragmentTransaction setCustomAnimations(int enter, int exit, int popEnter, int popExit) {
        mBase.setCustomAnimations(enter, exit, popEnter, popExit);
        return this;
    }

    public PluginFragmentTransaction setTransition(int transit) {
        mBase.setTransition(transit);
        return this;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PluginFragmentTransaction addSharedElement(View sharedElement, String name) {
        mBase.addSharedElement(sharedElement, name);
        return this;
    }

    public PluginFragmentTransaction setTransitionStyle(int styleRes) {
        mBase.setTransitionStyle(styleRes);
        return this;
    }

    public PluginFragmentTransaction addToBackStack(String name) {
        mBase.addToBackStack(name);
        return this;
    }

    public boolean isAddToBackStackAllowed() {
        return mBase.isAddToBackStackAllowed();

    }

    public PluginFragmentTransaction disallowAddToBackStack() {
        mBase.disallowAddToBackStack();
        return this;
    }

    public PluginFragmentTransaction setBreadCrumbTitle(int res) {
        mBase.setBreadCrumbTitle(res);
        return this;
    }

    public PluginFragmentTransaction setBreadCrumbTitle(CharSequence text) {
        mBase.setBreadCrumbTitle(text);
        return this;
    }

    public PluginFragmentTransaction setBreadCrumbShortTitle(int res) {
        mBase.setBreadCrumbShortTitle(res);
        return this;
    }

    public PluginFragmentTransaction setBreadCrumbShortTitle(CharSequence text) {
        mBase.setBreadCrumbShortTitle(text);
        return this;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public PluginFragmentTransaction setReorderingAllowed(boolean reorderingAllowed) {
        mBase.setReorderingAllowed(reorderingAllowed);
        return this;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public PluginFragmentTransaction runOnCommit(Runnable runnable) {
        mBase.runOnCommit(runnable);
        return this;
    }

    public int commit() {
        return mBase.commit();
    }

    public int commitAllowingStateLoss() {
        return mBase.commitAllowingStateLoss();
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void commitNow() {
        mBase.commitNow();
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void commitNowAllowingStateLoss() {
        mBase.commitNowAllowingStateLoss();
    }
}
