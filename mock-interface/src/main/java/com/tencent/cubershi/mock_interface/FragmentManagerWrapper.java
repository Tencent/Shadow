package com.tencent.cubershi.mock_interface;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;

@SuppressLint("CommitTransaction")
public class FragmentManagerWrapper extends FragmentManager {
    private final FragmentManager mBase;

    public FragmentManagerWrapper(FragmentManager mBase) {
        this.mBase = mBase;
    }

    @Override
    public FragmentTransaction beginTransaction() {
        return mBase.beginTransaction();
    }

    @Override
    public boolean executePendingTransactions() {
        return mBase.executePendingTransactions();
    }

    @Override
    public Fragment findFragmentById(int id) {
        return mBase.findFragmentById(id);
    }

    @Override
    public Fragment findFragmentByTag(String tag) {
        return mBase.findFragmentByTag(tag);
    }

    @Override
    public void popBackStack() {
        mBase.popBackStack();
    }

    @Override
    public boolean popBackStackImmediate() {
        return mBase.popBackStackImmediate();
    }

    @Override
    public void popBackStack(String name, int flags) {
        mBase.popBackStack(name, flags);
    }

    @Override
    public boolean popBackStackImmediate(String name, int flags) {
        return mBase.popBackStackImmediate(name, flags);
    }

    @Override
    public void popBackStack(int id, int flags) {
        mBase.popBackStack(id, flags);
    }

    @Override
    public boolean popBackStackImmediate(int id, int flags) {
        return mBase.popBackStackImmediate(id, flags);
    }

    @Override
    public int getBackStackEntryCount() {
        return mBase.getBackStackEntryCount();
    }

    @Override
    public BackStackEntry getBackStackEntryAt(int index) {
        return mBase.getBackStackEntryAt(index);
    }

    @Override
    public void addOnBackStackChangedListener(OnBackStackChangedListener listener) {
        mBase.addOnBackStackChangedListener(listener);
    }

    @Override
    public void removeOnBackStackChangedListener(OnBackStackChangedListener listener) {
        mBase.removeOnBackStackChangedListener(listener);
    }

    @Override
    public void putFragment(Bundle bundle, String key, Fragment fragment) {
        mBase.putFragment(bundle, key, fragment);
    }

    @Override
    public Fragment getFragment(Bundle bundle, String key) {
        return mBase.getFragment(bundle, key);
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public List<Fragment> getFragments() {
        return mBase.getFragments();
    }

    @Override
    public Fragment.SavedState saveFragmentInstanceState(Fragment f) {
        return mBase.saveFragmentInstanceState(f);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public boolean isDestroyed() {
        return mBase.isDestroyed();
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public void registerFragmentLifecycleCallbacks(FragmentLifecycleCallbacks cb, boolean recursive) {
        mBase.registerFragmentLifecycleCallbacks(cb, recursive);
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public void unregisterFragmentLifecycleCallbacks(FragmentLifecycleCallbacks cb) {
        mBase.unregisterFragmentLifecycleCallbacks(cb);
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public Fragment getPrimaryNavigationFragment() {
        return mBase.getPrimaryNavigationFragment();
    }

    @Override
    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        mBase.dump(prefix, fd, writer, args);
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public boolean isStateSaved() {
        return mBase.isStateSaved();
    }
}
