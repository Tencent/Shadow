package com.tencent.cubershi.mock_interface;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Build;
import android.view.View;

public class PluginFragmentManager extends FragmentManagerWrapper {
    PluginFragmentManager(FragmentManager mBase) {
        super(mBase);
    }

    @SuppressLint("CommitTransaction")
    @Override
    public FragmentTransaction beginTransaction() {
        return new PluginFragmentTransaction(super.beginTransaction());
    }

    private static class PluginFragmentTransaction extends FragmentTransaction {
        final FragmentTransaction mBase;

        private PluginFragmentTransaction(FragmentTransaction mBase) {
            this.mBase = mBase;
        }

        private ContainerFragment getContainerFragment(MockFragment mockFragment) {
            return mockFragment.getContainerFragment();
        }


        public FragmentTransaction add(MockFragment fragment, String tag) {
            return add(getContainerFragment(fragment), tag);
        }


        public FragmentTransaction add(int containerViewId, MockFragment fragment) {
            return add(containerViewId, getContainerFragment(fragment));
        }


        public FragmentTransaction add(int containerViewId, MockFragment fragment, String tag) {
            return add(containerViewId, getContainerFragment(fragment), tag);
        }


        public FragmentTransaction replace(int containerViewId, MockFragment fragment) {
            return replace(containerViewId, getContainerFragment(fragment));
        }


        public FragmentTransaction replace(int containerViewId, MockFragment fragment, String tag) {
            return replace(containerViewId, getContainerFragment(fragment), tag);
        }


        public FragmentTransaction remove(MockFragment fragment) {
            return remove(getContainerFragment(fragment));
        }


        public FragmentTransaction hide(MockFragment fragment) {
            return hide(getContainerFragment(fragment));
        }


        public FragmentTransaction show(MockFragment fragment) {
            return show(getContainerFragment(fragment));
        }


        public FragmentTransaction detach(MockFragment fragment) {
            return detach(getContainerFragment(fragment));
        }


        public FragmentTransaction attach(MockFragment fragment) {
            return attach(getContainerFragment(fragment));
        }

        @TargetApi(Build.VERSION_CODES.O)
        public FragmentTransaction setPrimaryNavigationFragment(MockFragment fragment) {
            return setPrimaryNavigationFragment(getContainerFragment(fragment));
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
}
