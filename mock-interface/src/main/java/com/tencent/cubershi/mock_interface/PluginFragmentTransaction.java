package com.tencent.cubershi.mock_interface;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Build;
import android.view.View;

public class PluginFragmentTransaction extends FragmentTransaction {
    final FragmentTransaction mBase;

    PluginFragmentTransaction(FragmentTransaction mBase) {
        this.mBase = mBase;
    }

    private ContainerFragment getContainerFragment(MockFragment mockFragment) {
        return mockFragment.getContainerFragment();
    }


    public PluginFragmentTransaction add(MockFragment fragment, String tag) {
        add(getContainerFragment(fragment), tag);
        return this;
    }


    public PluginFragmentTransaction add(int containerViewId, MockFragment fragment) {
        add(containerViewId, getContainerFragment(fragment));
        return this;
    }


    public PluginFragmentTransaction add(int containerViewId, MockFragment fragment, String tag) {
        add(containerViewId, getContainerFragment(fragment), tag);
        return this;
    }


    public PluginFragmentTransaction replace(int containerViewId, MockFragment fragment) {
        replace(containerViewId, getContainerFragment(fragment));
        return this;
    }


    public PluginFragmentTransaction replace(int containerViewId, MockFragment fragment, String tag) {
        replace(containerViewId, getContainerFragment(fragment), tag);
        return this;
    }


    public PluginFragmentTransaction remove(MockFragment fragment) {
        remove(getContainerFragment(fragment));
        return this;
    }


    public PluginFragmentTransaction hide(MockFragment fragment) {
        hide(getContainerFragment(fragment));
        return this;
    }


    public PluginFragmentTransaction show(MockFragment fragment) {
        show(getContainerFragment(fragment));
        return this;
    }


    public PluginFragmentTransaction detach(MockFragment fragment) {
        detach(getContainerFragment(fragment));
        return this;
    }


    public PluginFragmentTransaction attach(MockFragment fragment) {
        attach(getContainerFragment(fragment));
        return this;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public PluginFragmentTransaction setPrimaryNavigationFragment(MockFragment fragment) {
        setPrimaryNavigationFragment(getContainerFragment(fragment));
        return this;
    }

    @Override
    public FragmentTransaction add(Fragment fragment, String tag) {
        return mBase.add(fragment, tag);
    }

    @Override
    public FragmentTransaction add(int containerViewId, Fragment fragment) {
        return mBase.add(containerViewId, fragment);
    }

    @Override
    public FragmentTransaction add(int containerViewId, Fragment fragment, String tag) {
        return mBase.add(containerViewId, fragment, tag);
    }

    @Override
    public FragmentTransaction replace(int containerViewId, Fragment fragment) {
        return mBase.replace(containerViewId, fragment);
    }

    @Override
    public FragmentTransaction replace(int containerViewId, Fragment fragment, String tag) {
        return mBase.replace(containerViewId, fragment, tag);
    }

    @Override
    public FragmentTransaction remove(Fragment fragment) {
        return mBase.remove(fragment);
    }

    @Override
    public FragmentTransaction hide(Fragment fragment) {
        return mBase.hide(fragment);
    }

    @Override
    public FragmentTransaction show(Fragment fragment) {
        return mBase.show(fragment);
    }

    @Override
    public FragmentTransaction detach(Fragment fragment) {
        return mBase.detach(fragment);
    }

    @Override
    public FragmentTransaction attach(Fragment fragment) {
        return mBase.attach(fragment);
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public FragmentTransaction setPrimaryNavigationFragment(Fragment fragment) {
        return mBase.setPrimaryNavigationFragment(fragment);
    }

    @Override
    public boolean isEmpty() {
        return mBase.isEmpty();
    }

    @Override
    public FragmentTransaction setCustomAnimations(int enter, int exit) {
        return mBase.setCustomAnimations(enter, exit);
    }

    @Override
    public FragmentTransaction setCustomAnimations(int enter, int exit, int popEnter, int popExit) {
        return mBase.setCustomAnimations(enter, exit, popEnter, popExit);
    }

    @Override
    public FragmentTransaction setTransition(int transit) {
        return mBase.setTransition(transit);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public FragmentTransaction addSharedElement(View sharedElement, String name) {
        return mBase.addSharedElement(sharedElement, name);
    }

    @Override
    public FragmentTransaction setTransitionStyle(int styleRes) {
        return mBase.setTransitionStyle(styleRes);
    }

    @Override
    public FragmentTransaction addToBackStack(String name) {
        return mBase.addToBackStack(name);
    }

    @Override
    public boolean isAddToBackStackAllowed() {
        return mBase.isAddToBackStackAllowed();
    }

    @Override
    public FragmentTransaction disallowAddToBackStack() {
        return mBase.disallowAddToBackStack();
    }

    @Override
    public FragmentTransaction setBreadCrumbTitle(int res) {
        return mBase.setBreadCrumbTitle(res);
    }

    @Override
    public FragmentTransaction setBreadCrumbTitle(CharSequence text) {
        return mBase.setBreadCrumbTitle(text);
    }

    @Override
    public FragmentTransaction setBreadCrumbShortTitle(int res) {
        return mBase.setBreadCrumbShortTitle(res);
    }

    @Override
    public FragmentTransaction setBreadCrumbShortTitle(CharSequence text) {
        return mBase.setBreadCrumbShortTitle(text);
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public FragmentTransaction setReorderingAllowed(boolean reorderingAllowed) {
        return mBase.setReorderingAllowed(reorderingAllowed);
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public FragmentTransaction runOnCommit(Runnable runnable) {
        return mBase.runOnCommit(runnable);
    }

    @Override
    public int commit() {
        return mBase.commit();
    }

    @Override
    public int commitAllowingStateLoss() {
        return mBase.commitAllowingStateLoss();
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void commitNow() {
        mBase.commitNow();
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void commitNowAllowingStateLoss() {
        mBase.commitNowAllowingStateLoss();
    }
}
