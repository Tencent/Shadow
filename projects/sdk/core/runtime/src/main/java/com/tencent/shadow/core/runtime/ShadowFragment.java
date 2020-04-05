/*
 * Tencent is pleased to support the open source community by making Tencent Shadow available.
 * Copyright (C) 2019 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tencent.shadow.core.runtime;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.app.SharedElementCallback;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.transition.Transition;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.tencent.shadow.core.runtime.container.PluginContainerActivity;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class ShadowFragment {
    private static Map<String, Constructor<?>> constructorMap = new HashMap<>();

    private static IContainerFragment instantiateContainerFragment(ShadowFragment shadowFragment) {
        String shadowFragmentClassname = shadowFragment.getClass().getName();
        String containerFragmentClassName = shadowFragmentClassname.substring(0, shadowFragmentClassname.length() - 1);
        Constructor<?> constructor = constructorMap.get(containerFragmentClassName);
        if (constructor == null) {
            ClassLoader pluginClassLoader = shadowFragment.getClass().getClassLoader();
            try {
                Class<?> aClass = pluginClassLoader.loadClass(containerFragmentClassName);
                constructor = aClass.getConstructor();
                constructorMap.put(containerFragmentClassName, constructor);
            } catch (Exception e) {
                throw new Fragment.InstantiationException("无法构造" + containerFragmentClassName, e);
            }
        }
        try {
            return IContainerFragment.class.cast(constructor.newInstance());
        } catch (Exception e) {
            throw new Fragment.InstantiationException("无法构造" + containerFragmentClassName, e);
        }
    }

    /**
     * 标志当前Fragment是否由app自己的代码创建的
     */
    protected boolean mIsAppCreateFragment;

    PluginFragmentManager mPluginFragmentManager;

    public ShadowFragment() {
        mContainerFragment = instantiateContainerFragment(this);
        mContainerFragment.bindPluginFragment(this);
        mIsAppCreateFragment = true;
    }

    private Context mAttachedContext;

    protected IContainerFragment mContainerFragment;

    private int mChildPluginFragmentManagerHash;

    private PluginFragmentManager mChildPluginFragmentManager;

    public void setContainerFragment(IContainerFragment containerFragment) {
        mIsAppCreateFragment = false;
        mContainerFragment.unbindPluginFragment();
        mContainerFragment = containerFragment;
    }

    public IContainerFragment getContainerFragment() {
        if (mContainerFragment == null) {
            throw new NullPointerException(this.getClass().getName() + " mContainerFragment == null");
        }
        return mContainerFragment;
    }

    final public ShadowActivity getActivity() {
        if (mAttachedContext == null) {
            return null;
        } else if (mAttachedContext instanceof ShadowActivity) {
            return (ShadowActivity) mAttachedContext;
        } else {
            final PluginContainerActivity activity = (PluginContainerActivity) mContainerFragment.getActivity();
            return (ShadowActivity) PluginActivity.get(activity);
        }
    }

    public void setArguments(Bundle args) {
        if (mIsAppCreateFragment) {
            mContainerFragment.setArguments(args);
        }
    }

    final public Bundle getArguments() {
        return mContainerFragment.getArguments();
    }

    public PluginFragmentManager getFragmentManager() {
        if (mPluginFragmentManager == null && getActivity() != null) {
            mPluginFragmentManager = getActivity().getFragmentManager();
        }
        return mPluginFragmentManager;
    }

    public PluginFragmentManager getChildFragmentManager() {
        FragmentManager fragmentManager = mContainerFragment.getChildFragmentManager();
        int hash = System.identityHashCode(fragmentManager);
        if (hash != mChildPluginFragmentManagerHash) {
            mChildPluginFragmentManagerHash = hash;
            mChildPluginFragmentManager = new PluginFragmentManager(fragmentManager);
        }
        return mChildPluginFragmentManager;
    }

    final public Resources getResources() {
        if (mAttachedContext == null) {
            throw new IllegalStateException("Fragment " + this + " not attached to Activity");
        }
        return mAttachedContext.getResources();
    }

    public void setInitialSavedState(Fragment.SavedState state) {

    }


    public void setTargetFragment(Fragment fragment, int requestCode) {

    }


    public Context getContext() {
        return mAttachedContext;
    }


    public void onHiddenChanged(boolean hidden) {
        mContainerFragment.superOnHiddenChanged(hidden);
    }


    public void setRetainInstance(boolean retain) {
        mContainerFragment.superSetRetainInstance(retain);
    }


    public void setHasOptionsMenu(boolean hasMenu) {
        mContainerFragment.superSetHasOptionsMenu(hasMenu);
    }


    public void setMenuVisibility(boolean menuVisible) {
        mContainerFragment.superSetMenuVisibility(menuVisible);
    }


    public void setUserVisibleHint(boolean isVisibleToUser) {
        mContainerFragment.superSetUserVisibleHint(isVisibleToUser);
    }


    public boolean getUserVisibleHint() {
        return mContainerFragment.getUserVisibleHint();
    }


    public LoaderManager getLoaderManager() {
        return null;
    }


    public void startActivity(Intent intent) {
        mAttachedContext.startActivity(intent);
    }


    @TargetApi(16)
    public void startActivity(Intent intent, Bundle options) {
        mAttachedContext.startActivity(intent, options);
    }


    public void startActivityForResult(Intent intent, int requestCode) {

    }


    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {

    }


    public void startIntentSenderForResult(IntentSender intent, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, Bundle options) throws IntentSender.SendIntentException {

    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }


    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

    }


    public boolean shouldShowRequestPermissionRationale(String permission) {
        return false;
    }


    public LayoutInflater onGetLayoutInflater(Bundle savedInstanceState) {
        return null;
    }


    public void onInflate(AttributeSet attrs, Bundle savedInstanceState) {

    }


    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {

    }


    public void onInflate(Activity activity, AttributeSet attrs, Bundle savedInstanceState) {

    }


    public void onAttachFragment(Fragment childFragment) {

    }


    public void onAttach(Context context) {
        mAttachedContext = context;
    }


    public void onAttach(ShadowActivity activity) {
        mAttachedContext = activity;
    }


    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
        return null;
    }


    public void onCreate(Bundle savedInstanceState) {

    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }


    public void onViewCreated(View view, Bundle savedInstanceState) {

    }


    public View getView() {
        return mContainerFragment.getView();
    }


    public void onActivityCreated(Bundle savedInstanceState) {

    }


    public void onViewStateRestored(Bundle savedInstanceState) {

    }


    public void onStart() {

    }


    public void onResume() {

    }


    public void onSaveInstanceState(Bundle outState) {

    }


    public void onMultiWindowModeChanged(boolean isInMultiWindowMode, Configuration newConfig) {

    }


    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {

    }


    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {

    }


    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {

    }


    public void onConfigurationChanged(Configuration newConfig) {

    }


    public void onPause() {

    }


    public void onStop() {

    }


    public void onLowMemory() {

    }


    public void onTrimMemory(int level) {

    }


    public void onDestroyView() {

    }


    public void onDestroy() {

    }


    public void onDetach() {

    }


    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

    }


    public void onPrepareOptionsMenu(Menu menu) {

    }


    public void onDestroyOptionsMenu() {

    }


    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }


    public void onOptionsMenuClosed(Menu menu) {

    }


    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

    }


    public void registerForContextMenu(View view) {

    }


    public void unregisterForContextMenu(View view) {

    }


    public boolean onContextItemSelected(MenuItem item) {
        return false;
    }


    public void setEnterSharedElementCallback(SharedElementCallback callback) {

    }


    public void setExitSharedElementCallback(SharedElementCallback callback) {

    }


    public void setEnterTransition(Transition transition) {

    }


    public Transition getEnterTransition() {
        return null;
    }


    public void setReturnTransition(Transition transition) {

    }


    public Transition getReturnTransition() {
        return null;
    }


    public void setExitTransition(Transition transition) {

    }


    public Transition getExitTransition() {
        return null;
    }


    public void setReenterTransition(Transition transition) {

    }


    public Transition getReenterTransition() {
        return null;
    }


    public void setSharedElementEnterTransition(Transition transition) {

    }


    public Transition getSharedElementEnterTransition() {
        return null;
    }


    public void setSharedElementReturnTransition(Transition transition) {

    }


    public Transition getSharedElementReturnTransition() {
        return null;
    }


    public void setAllowEnterTransitionOverlap(boolean allow) {

    }


    public boolean getAllowEnterTransitionOverlap() {
        return false;
    }


    public void setAllowReturnTransitionOverlap(boolean allow) {

    }


    public boolean getAllowReturnTransitionOverlap() {
        return false;
    }


    public void postponeEnterTransition() {

    }


    public void startPostponedEnterTransition() {

    }


    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {

    }

    final public boolean isAdded() {
        return mContainerFragment.isAdded();
    }

    final public boolean isDetached() {
        return mContainerFragment.isDetached();
    }

    final public boolean isRemoving() {
        return mContainerFragment.isRemoving();
    }

    final public boolean isInLayout() {
        return mContainerFragment.isInLayout();
    }

    final public boolean isResumed() {
        return mContainerFragment.isResumed();
    }

    final public boolean isVisible() {
        return mContainerFragment.isVisible();
    }

    final public boolean isHidden() {
        return mContainerFragment.isHidden();
    }

    final public int getId() {
        return mContainerFragment.getId();
    }

    final public String getTag() {
        return mContainerFragment.getTag();
    }

    public final CharSequence getText(int resId) {
        return getResources().getText(resId);
    }

    public final String getString(int resId) {
        return getResources().getString(resId);
    }

    public final String getString(int resId, Object... formatArgs) {
        return getResources().getString(resId, formatArgs);
    }

    public final void requestPermissions(String[] permissions, int requestCode) {
        mContainerFragment.requestPermissions(permissions, requestCode);
    }

    @SuppressLint("NewApi")
    final public ShadowFragment getParentFragment() {
        Fragment parentFragment = mContainerFragment.asFragment().getParentFragment();
        return ((IContainerFragment) parentFragment).getPluginFragment();
    }
}
