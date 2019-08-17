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

import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.SharedElementCallback;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.session.MediaController;
import android.net.Uri;
import android.os.Bundle;
import android.view.DragAndDropPermissions;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

public abstract class ShadowActivity extends PluginActivity {

    private int mFragmentManagerHash;

    private PluginFragmentManager mPluginFragmentManager;

    public void setContentView(int layoutResID) {
        if ("merge".equals(XmlPullParserUtil.getLayoutStartTagName(getResources(), layoutResID))) {
            //如果传进来的xml文件的根tag是merge时，需要特殊处理
            View decorView = mHostActivityDelegator.getWindow().getDecorView();
            ViewGroup viewGroup = decorView.findViewById(android.R.id.content);
            LayoutInflater.from(this).inflate(layoutResID, viewGroup);
        } else {
            View inflate = LayoutInflater.from(this).inflate(layoutResID, null);
            mHostActivityDelegator.setContentView(inflate);
        }
    }

    public Intent getIntent() {
        return mHostActivityDelegator.getIntent();
    }

    public void setContentView(View view) {
        mHostActivityDelegator.setContentView(view);
    }

    public void setContentView(View view, ViewGroup.LayoutParams params) {
        mHostActivityDelegator.setContentView(view, params);
    }

    public void addContentView(View view, ViewGroup.LayoutParams params) {
        mHostActivityDelegator.superAddContentView(view, params);
    }

    public final ShadowApplication getApplication() {
        return mPluginApplication;
    }

    public PluginFragmentManager getFragmentManager() {
        FragmentManager fragmentManager = mHostActivityDelegator.getFragmentManager();
        int hash = System.identityHashCode(fragmentManager);
        if (hash != mFragmentManagerHash) {
            mFragmentManagerHash = hash;
            mPluginFragmentManager = new PluginFragmentManager(fragmentManager);
        }
        return mPluginFragmentManager;
    }

    public Object getLastNonConfigurationInstance() {
        return mHostActivityDelegator.getLastNonConfigurationInstance();
    }

    public Window getWindow() {
        return mHostActivityDelegator.getWindow();
    }

    public <T extends View> T findViewById(int id) {
        return mHostActivityDelegator.findViewById(id);
    }

    public WindowManager getWindowManager() {
        return mHostActivityDelegator.getWindowManager();
    }


    public final ShadowActivity getParent() {
        return null;
    }

    public void overridePendingTransition(int enterAnim, int exitAnim) {
        //如果使用的资源不是系统资源，我们无法支持这个特性。
        if ((enterAnim & 0xFF000000) != 0x01000000) {
            enterAnim = 0;
        }
        if ((exitAnim & 0xFF000000) != 0x01000000) {
            exitAnim = 0;
        }
        mHostActivityDelegator.overridePendingTransition(enterAnim, exitAnim);
    }

    public void setTitle(CharSequence title) {
        mHostActivityDelegator.setTitle(title);
    }

    public boolean isFinishing() {
        return mHostActivityDelegator.isFinishing();
    }

    public boolean isDestroyed() {
        return mHostActivityDelegator.isDestroyed();
    }

    public final boolean requestWindowFeature(int featureId) {
        return mHostActivityDelegator.requestWindowFeature(featureId);
    }

    public void startActivityForResult(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode, null);
    }

    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        final Intent pluginIntent = new Intent(intent);
        pluginIntent.setExtrasClassLoader(mPluginClassLoader);
        ComponentName callingActivity = new ComponentName(getPackageName(), getClass().getName());
        final boolean success = mPluginComponentLauncher.startActivityForResult(mHostActivityDelegator, pluginIntent, requestCode, options, callingActivity);
        if (!success) {
            mHostActivityDelegator.startActivityForResult(intent, requestCode, options);
        }
    }


    public final void setResult(int resultCode) {
        mHostActivityDelegator.setResult(resultCode);
    }

    public final void setResult(int resultCode, Intent data) {
        mHostActivityDelegator.setResult(resultCode, data);
    }

    public SharedPreferences getPreferences(int mode) {
        return super.getSharedPreferences(getLocalClassName(), mode);
    }

    public String getLocalClassName() {
        return this.getClass().getName();
    }

    public void recreate() {
        mHostActivityDelegator.recreate();
    }

    public void runOnUiThread(Runnable action) {
        mHostActivityDelegator.runOnUiThread(action);
    }

    public void setTitleColor(int textColor) {
        mHostActivityDelegator.setTitleColor(textColor);
    }

    public final int getTitleColor() {
        return mHostActivityDelegator.getTitleColor();
    }

    public void setTitle(int var1) {
        mHostActivityDelegator.setTitle(var1);
    }

    public CharSequence getTitle() {
        return mHostActivityDelegator.getTitle();
    }

    public void setRequestedOrientation(int requestedOrientation) {
        mHostActivityDelegator.setRequestedOrientation(requestedOrientation);
    }

    public int getRequestedOrientation() {
        return mHostActivityDelegator.getRequestedOrientation();
    }

    public MenuInflater getMenuInflater() {
        return mHostActivityDelegator.getMenuInflater();
    }


    public final void requestPermissions(String[] permissions, int requestCode) {
        mHostActivityDelegator.requestPermissions(permissions, requestCode);
    }

    public ActionBar getActionBar() {
        return mHostActivityDelegator.getActionBar();
    }

    public void setVisible(boolean visible) {
        mHostActivityDelegator.setVisible(visible);
    }

    public void setIntent(Intent newIntent) {
        mHostActivityDelegator.setIntent(newIntent);
    }

    public View getCurrentFocus() {
        return mHostActivityDelegator.getCurrentFocus();
    }

    @Deprecated
    public final Cursor managedQuery(Uri uri, String[] projection, String selection,
                                     String[] selectionArgs, String sortOrder) {
        return mHostActivityDelegator.managedQuery(uri, projection, selection, selectionArgs, sortOrder);
    }

    public ComponentName getComponentName() {
        return mHostActivityDelegator.getComponentName();
    }

    public boolean shouldShowRequestPermissionRationale(String permission) {
        return mHostActivityDelegator.shouldShowRequestPermissionRationale(permission);
    }

    public final void setMediaController(MediaController controller) {
        mHostActivityDelegator.setMediaController(controller);
    }

    public final MediaController getMediaController() {
        return mHostActivityDelegator.getMediaController();
    }

    public boolean shouldUpRecreateTask(Intent targetIntent) {
        Intent intent = mPluginComponentLauncher.convertPluginActivityIntent(targetIntent);
        return mHostActivityDelegator.shouldUpRecreateTask(intent);
    }

    public boolean navigateUpTo(Intent upIntent) {
        Intent intent = mPluginComponentLauncher.convertPluginActivityIntent(upIntent);
        return mHostActivityDelegator.navigateUpTo(intent);
    }

    public Intent getParentActivityIntent() {
        return mHostActivityDelegator.getParentActivityIntent();
    }

    public DragAndDropPermissions requestDragAndDropPermissions(DragEvent event) {
        return mHostActivityDelegator.requestDragAndDropPermissions(event);
    }

    public void invalidateOptionsMenu() {
        mHostActivityDelegator.invalidateOptionsMenu();
    }

    public void startIntentSenderForResult(IntentSender intent, int requestCode,
                                           Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags)
            throws IntentSender.SendIntentException {
        mHostActivityDelegator.startIntentSenderForResult(intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags);
    }

    public void startIntentSenderForResult(IntentSender intent, int requestCode,
                                           Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags,
                                           Bundle options) throws IntentSender.SendIntentException {
        mHostActivityDelegator.startIntentSenderForResult(intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags, options);
    }

    public void finishAffinity() {
        mHostActivityDelegator.finishAffinity();
    }

    public void finishAfterTransition() {
        mHostActivityDelegator.finishAfterTransition();
    }

    public Uri getReferrer() {
        return mHostActivityDelegator.getReferrer();
    }

    public void setEnterSharedElementCallback(SharedElementCallback callback) {
        mHostActivityDelegator.setEnterSharedElementCallback(callback);
    }

    public void setExitSharedElementCallback(SharedElementCallback callback) {
        mHostActivityDelegator.setExitSharedElementCallback(callback);
    }

    public void postponeEnterTransition() {
        mHostActivityDelegator.postponeEnterTransition();
    }

    public void startPostponedEnterTransition() {
        mHostActivityDelegator.startPostponedEnterTransition();
    }

    public String getCallingPackage() {
        return mHostActivityDelegator.getCallingPackage();
    }

    public ComponentName getCallingActivity() {
        return mHostActivityDelegator.getCallingActivity();
    }

    public final void setVolumeControlStream(int streamType) {
        mHostActivityDelegator.setVolumeControlStream(streamType);
    }

    public final int getVolumeControlStream() {
        return mHostActivityDelegator.getVolumeControlStream();
    }

    public boolean isInMultiWindowMode() {
        return mHostActivityDelegator.isInMultiWindowMode();
    }

    @Override
    public void setTheme(int resid) {
        mHostActivityDelegator.setTheme(resid);
    }
}
