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

package com.tencent.shadow.core.runtime.container;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.app.PictureInPictureParams;
import android.app.SharedElementCallback;
import android.app.TaskStackBuilder;
import android.app.VoiceInteractor;
import android.app.assist.AssistContent;
import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.session.MediaController;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.os.UserHandle;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Display;
import android.view.DragAndDropPermissions;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.KeyboardShortcutGroup;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SearchEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toolbar;

import com.tencent.shadow.core.runtime.BuildConfig;
import com.tencent.shadow.core.runtime.ShadowActivity;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;

import static com.tencent.shadow.core.runtime.container.DelegateProvider.LOADER_VERSION_KEY;
import static com.tencent.shadow.core.runtime.container.DelegateProvider.PROCESS_ID_KEY;

/**
 * 插件的容器Activity。PluginLoader将把插件的Activity放在其中。
 * PluginContainerActivity以委托模式将Activity的所有回调方法委托给DelegateProviderHolder提供的Delegate。
 *
 * @author cubershi
 */
public class PluginContainerActivity extends Activity implements HostActivity, HostActivityDelegator {
    private static final String TAG = "PluginContainerActivity";

    HostActivityDelegate hostActivityDelegate;

    private boolean isBeforeOnCreate = true;

    public PluginContainerActivity() {
        HostActivityDelegate delegate;
        DelegateProvider delegateProvider = DelegateProviderHolder.getDelegateProvider();
        if (delegateProvider != null) {
            delegate = delegateProvider.getHostActivityDelegate(this.getClass());
            delegate.setDelegator(this);
        } else {
            Log.e(TAG, "PluginContainerActivity: DelegateProviderHolder没有初始化");
            delegate = null;
        }
        hostActivityDelegate = delegate;
    }

    final public Object getPluginActivity() {
        if (hostActivityDelegate != null) {
            return hostActivityDelegate.getPluginActivity();
        } else {
            //在遇到IllegalIntent时hostActivityDelegate==null。需要返回一个空的Activity避免Crash。
            return new ShadowActivity() {
            };
        }
    }

    @Override
    final protected void onCreate(Bundle savedInstanceState) {
        isBeforeOnCreate = false;
        mHostTheme = null;//释放资源

        boolean illegalIntent = isIllegalIntent(savedInstanceState);
        if (illegalIntent) {
            hostActivityDelegate = null;
            Log.e(TAG, "illegalIntent savedInstanceState==" + savedInstanceState + " getIntent().getExtras()==" + getIntent().getExtras());
        }

        if (hostActivityDelegate != null) {
            hostActivityDelegate.onCreate(savedInstanceState);
        } else {
            //这里是进程被杀后重启后走到，当需要恢复fragment状态的时候，由于系统保留了TAG，会因为找不到fragment引起crash
            super.onCreate(null);
            Log.e(TAG, "onCreate: hostActivityDelegate==null finish activity");
            finish();
            System.exit(0);
        }
    }

    /**
     * IllegalIntent指的是这些情况下的启动：
     * 1.插件版本变化之后，残留于系统中的PendingIntent或系统因回收内存杀死进程残留的任务栈而启动。
     * 由于插件版本变化，PluginLoader逻辑可能不一致，Intent中的参数可能不能满足新代码的启动条件。
     * 2.外部的非法启动，无法确定一个插件的Activity。
     *
     *
     * 3.不支持进程重启后莫名其妙的原因loader也加载了，但是可能要启动的plugin没有load，出现异常
     *
     * @param savedInstanceState onCreate时系统还回来的savedInstanceState
     * @return <code>true</code>表示这次启动不是我们预料的，需要尽早finish并退出进程。
     */
    private boolean isIllegalIntent(Bundle savedInstanceState) {
        Bundle extras = getIntent().getExtras();
        if (extras == null && savedInstanceState == null) {
            return true;
        }
        Bundle bundle;
        bundle = savedInstanceState == null ? extras : savedInstanceState;
        try {
            String loaderVersion = bundle.getString(LOADER_VERSION_KEY);
            long processVersion = bundle.getLong(PROCESS_ID_KEY);
            return !BuildConfig.VERSION_NAME.equals(loaderVersion) || processVersion != DelegateProviderHolder.sCustomPid;
        } catch (Throwable ignored) {
            //捕获可能的非法Intent中包含我们根本反序列化不了的数据
            return true;
        }
    }

    @Override
    protected void onResume() {
        if (hostActivityDelegate != null) {
            hostActivityDelegate.onResume();
        } else {
            super.onResume();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (hostActivityDelegate != null) {
            hostActivityDelegate.onNewIntent(intent);
        } else {
            super.onNewIntent(intent);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (hostActivityDelegate != null) {
            hostActivityDelegate.onSaveInstanceState(outState);
        } else {
            super.onSaveInstanceState(outState);
        }
        //避免插件setIntent清空掉LOADER_VERSION_KEY
        outState.putString(LOADER_VERSION_KEY, BuildConfig.VERSION_NAME);
        outState.putLong(PROCESS_ID_KEY, DelegateProviderHolder.sCustomPid);
    }

    @Override
    protected void onPause() {
        if (hostActivityDelegate != null) {
            hostActivityDelegate.onPause();
        } else {
            super.onPause();
        }
    }

    @Override
    protected void onStart() {
        if (hostActivityDelegate != null) {
            hostActivityDelegate.onStart();
        } else {
            super.onStart();
        }
    }

    @Override
    protected void onStop() {
        if (hostActivityDelegate != null) {
            hostActivityDelegate.onStop();
        } else {
            super.onStop();
        }
    }

    @Override
    protected void onDestroy() {
        if (hostActivityDelegate != null) {
            hostActivityDelegate.onDestroy();
        } else {
            super.onDestroy();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (hostActivityDelegate != null) {
            hostActivityDelegate.onConfigurationChanged(newConfig);
        } else {
            super.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public boolean isChangingConfigurations() {
        if (hostActivityDelegate != null) {
            return hostActivityDelegate.isChangingConfigurations();
        } else {
            return super.isChangingConfigurations();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (hostActivityDelegate != null) {
            return hostActivityDelegate.dispatchKeyEvent(event);
        } else {
            return super.dispatchKeyEvent(event);
        }
    }

    @Override
    public void finish() {
        if (hostActivityDelegate != null) {
            hostActivityDelegate.finish();
        } else {
            super.finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (hostActivityDelegate != null) {
            hostActivityDelegate.onActivityResult(requestCode, resultCode, data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onChildTitleChanged(Activity childActivity, CharSequence title) {
        if (hostActivityDelegate != null) {
            hostActivityDelegate.onChildTitleChanged(childActivity, title);
        } else {
            super.onChildTitleChanged(childActivity, title);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (hostActivityDelegate != null) {
            hostActivityDelegate.onRestoreInstanceState(savedInstanceState);
        } else {
            super.onRestoreInstanceState(savedInstanceState);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        if (hostActivityDelegate != null) {
            hostActivityDelegate.onPostCreate(savedInstanceState);
        } else {
            super.onPostCreate(savedInstanceState);
        }
    }

    @Override
    protected void onRestart() {
        if (hostActivityDelegate != null) {
            hostActivityDelegate.onRestart();
        } else {
            super.onRestart();
        }
    }

    @Override
    protected void onUserLeaveHint() {
        if (hostActivityDelegate != null) {
            hostActivityDelegate.onUserLeaveHint();
        } else {
            super.onUserLeaveHint();
        }
    }

    @Override
    public boolean onCreateThumbnail(Bitmap outBitmap, Canvas canvas) {
        if (hostActivityDelegate != null) {
            return hostActivityDelegate.onCreateThumbnail(outBitmap, canvas);
        } else {
            return super.onCreateThumbnail(outBitmap, canvas);
        }
    }

    @Override
    public CharSequence onCreateDescription() {
        if (hostActivityDelegate != null) {
            return hostActivityDelegate.onCreateDescription();
        } else {
            return super.onCreateDescription();
        }
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        if (hostActivityDelegate != null) {
            return hostActivityDelegate.onRetainNonConfigurationInstance();
        } else {
            return super.onRetainNonConfigurationInstance();
        }
    }

    @Override
    public void onLowMemory() {
        if (hostActivityDelegate != null) {
            hostActivityDelegate.onLowMemory();
        } else {
            super.onLowMemory();
        }
    }

    @Override
    public boolean onTrackballEvent(MotionEvent event) {
        if (hostActivityDelegate != null) {
            return hostActivityDelegate.onTrackballEvent(event);
        } else {
            return super.onTrackballEvent(event);
        }
    }

    @Override
    public void onUserInteraction() {
        if (hostActivityDelegate != null) {
            hostActivityDelegate.onUserInteraction();
        } else {
            super.onUserInteraction();
        }
    }

    @Override
    public void onWindowAttributesChanged(WindowManager.LayoutParams params) {
        if (hostActivityDelegate != null) {
            hostActivityDelegate.onWindowAttributesChanged(params);
        } else {
            super.onWindowAttributesChanged(params);
        }
    }

    @Override
    public void onContentChanged() {
        if (hostActivityDelegate != null) {
            hostActivityDelegate.onContentChanged();
        } else {
            super.onContentChanged();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hostActivityDelegate != null) {
            hostActivityDelegate.onWindowFocusChanged(hasFocus);
        } else {
            super.onWindowFocusChanged(hasFocus);
        }
    }

    @Override
    public View onCreatePanelView(int featureId) {
        if (hostActivityDelegate != null) {
            return hostActivityDelegate.onCreatePanelView(featureId);
        } else {
            return super.onCreatePanelView(featureId);
        }
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        if (hostActivityDelegate != null) {
            return hostActivityDelegate.onCreatePanelMenu(featureId, menu);
        } else {
            return super.onCreatePanelMenu(featureId, menu);
        }
    }

    @Override
    public boolean onPreparePanel(int featureId, View view, Menu menu) {
        if (hostActivityDelegate != null) {
            return hostActivityDelegate.onPreparePanel(featureId, view, menu);
        } else {
            return super.onPreparePanel(featureId, view, menu);
        }
    }

    @Override
    public void onPanelClosed(int featureId, Menu menu) {
        if (hostActivityDelegate != null) {
            hostActivityDelegate.onPanelClosed(featureId, menu);
        } else {
            super.onPanelClosed(featureId, menu);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (hostActivityDelegate != null) {
            return hostActivityDelegate.onCreateDialog(id);
        } else {
            return super.onCreateDialog(id);
        }
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        if (hostActivityDelegate != null) {
            hostActivityDelegate.onPrepareDialog(id, dialog);
        } else {
            super.onPrepareDialog(id, dialog);
        }
    }

    @Override
    protected void onApplyThemeResource(Resources.Theme theme, int resid, boolean first) {
        if (hostActivityDelegate != null) {
            hostActivityDelegate.onApplyThemeResource(theme, resid, first);
        } else {
            super.onApplyThemeResource(theme, resid, first);
        }
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        if (hostActivityDelegate != null) {
            return hostActivityDelegate.onCreateView(name, context, attrs);
        } else {
            return super.onCreateView(name, context, attrs);
        }
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        if (hostActivityDelegate != null) {
            return hostActivityDelegate.onCreateView(parent, name, context, attrs);
        } else {
            return super.onCreateView(parent, name, context, attrs);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (hostActivityDelegate != null) {
            hostActivityDelegate.onRequestPermissionsResult(requestCode, permissions, grantResults);
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void startActivityFromChild(Activity child, Intent intent, int requestCode) {
        if (hostActivityDelegate != null) {
            hostActivityDelegate.startActivityFromChild(child, intent, requestCode);
        } else {
            super.startActivityFromChild(child, intent, requestCode);
        }
    }

    @Override
    public ClassLoader getClassLoader() {
        if (hostActivityDelegate != null) {
            return hostActivityDelegate.getClassLoader();
        } else {
            return super.getClassLoader();
        }
    }

    @Override
    public HostActivity getHostActivity() {
        return this;
    }

    @Override
    public Activity getImplementActivity() {
        return this;
    }

    @Override
    public Window getImplementWindow() {
        return getWindow();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public LayoutInflater getLayoutInflater() {
        if (hostActivityDelegate != null) {
            return hostActivityDelegate.getLayoutInflater();
        } else {
            return super.getLayoutInflater();
        }
    }

    @Override
    public Resources getResources() {
        if (hostActivityDelegate != null) {
            return hostActivityDelegate.getResources();
        } else {
            return super.getResources();
        }
    }

    @Override
    public void onBackPressed() {
        if (hostActivityDelegate != null) {
            hostActivityDelegate.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    public Application superGetApplication() {
        return super.getApplication();
    }

    public boolean superIsChild() {
        return super.isChild();
    }

    public Activity superGetParent() {
        return super.getParent();
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void superRequestShowKeyboardShortcuts() {
        super.requestShowKeyboardShortcuts();
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void superDismissKeyboardShortcutsHelper() {
        super.dismissKeyboardShortcutsHelper();
    }

    public void superSetDefaultKeyMode(int mode) {
        super.setDefaultKeyMode(mode);
    }

    public void superShowDialog(int id) {
        super.showDialog(id);
    }

    public boolean superShowDialog(int id, Bundle args) {
        return super.showDialog(id, args);
    }

    public void superDismissDialog(int id) {
        super.dismissDialog(id);
    }

    public void superRemoveDialog(int id) {
        super.removeDialog(id);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public SearchEvent superGetSearchEvent() {
        return super.getSearchEvent();
    }

    public boolean superRequestWindowFeature(int featureId) {
        return super.requestWindowFeature(featureId);
    }

    public void superSetFeatureDrawableResource(int featureId, int resId) {
        super.setFeatureDrawableResource(featureId, resId);
    }

    public void superSetFeatureDrawableUri(int featureId, Uri uri) {
        super.setFeatureDrawableUri(featureId, uri);
    }

    public void superSetFeatureDrawable(int featureId, Drawable drawable) {
        super.setFeatureDrawable(featureId, drawable);
    }

    public void superSetFeatureDrawableAlpha(int featureId, int alpha) {
        super.setFeatureDrawableAlpha(featureId, alpha);
    }

    public void superRequestPermissions(String[] permissions, int requestCode) {
    }

    public void superSetResult(int resultCode) {
        super.setResult(resultCode);
    }

    public void superSetResult(int resultCode, Intent data) {
        super.setResult(resultCode, data);
    }

    public CharSequence superGetTitle() {
        return super.getTitle();
    }

    public int superGetTitleColor() {
        return super.getTitleColor();
    }

    public void superSetProgressBarVisibility(boolean visible) {
        super.setProgressBarVisibility(visible);
    }

    public void superSetProgressBarIndeterminateVisibility(boolean visible) {
        super.setProgressBarIndeterminateVisibility(visible);
    }

    public void superSetProgressBarIndeterminate(boolean indeterminate) {
        super.setProgressBarIndeterminate(indeterminate);
    }

    public void superSetProgress(int progress) {
        super.setProgress(progress);
    }

    public void superSetSecondaryProgress(int secondaryProgress) {
        super.setSecondaryProgress(secondaryProgress);
    }

    public void superSetVolumeControlStream(int streamType) {
        super.setVolumeControlStream(streamType);
    }

    public int superGetVolumeControlStream() {
        return super.getVolumeControlStream();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void superSetMediaController(MediaController controller) {
        super.setMediaController(controller);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MediaController superGetMediaController() {
        return super.getMediaController();
    }

    public void superRunOnUiThread(Runnable action) {
        super.runOnUiThread(action);
    }

    public Intent superGetIntent() {
        return super.getIntent();
    }

    public void superSetIntent(Intent newIntent) {
        super.setIntent(newIntent);
    }

    public WindowManager superGetWindowManager() {
        return super.getWindowManager();
    }

    public Window superGetWindow() {
        return super.getWindow();
    }

    public LoaderManager superGetLoaderManager() {
        return super.getLoaderManager();
    }

    public View superGetCurrentFocus() {
        return super.getCurrentFocus();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean superIsVoiceInteraction() {
        return super.isVoiceInteraction();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean superIsVoiceInteractionRoot() {
        return super.isVoiceInteractionRoot();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public VoiceInteractor superGetVoiceInteractor() {
        return super.getVoiceInteractor();
    }

    @TargetApi(Build.VERSION_CODES.N)
    public boolean superIsLocalVoiceInteractionSupported() {
        return super.isLocalVoiceInteractionSupported();
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void superStartLocalVoiceInteraction(Bundle privateOptions) {
        super.startLocalVoiceInteraction(privateOptions);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void superStopLocalVoiceInteraction() {
        super.stopLocalVoiceInteraction();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean superShowAssist(Bundle args) {
        return super.showAssist(args);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void superReportFullyDrawn() {
        super.reportFullyDrawn();
    }

    @TargetApi(Build.VERSION_CODES.N)
    public boolean superIsInMultiWindowMode() {
        return super.isInMultiWindowMode();
    }

    @TargetApi(Build.VERSION_CODES.N)
    public boolean superIsInPictureInPictureMode() {
        return super.isInPictureInPictureMode();
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void superEnterPictureInPictureMode() {
        super.enterPictureInPictureMode();
    }

    @TargetApi(Build.VERSION_CODES.O)
    public boolean superEnterPictureInPictureMode(PictureInPictureParams params) {
        return super.enterPictureInPictureMode(params);
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void superSetPictureInPictureParams(PictureInPictureParams params) {
        super.setPictureInPictureParams(params);
    }

    @TargetApi(Build.VERSION_CODES.O)
    public int superGetMaxNumPictureInPictureActions() {
        return super.getMaxNumPictureInPictureActions();
    }

    public int superGetChangingConfigurations() {
        return super.getChangingConfigurations();
    }

    public Object superGetLastNonConfigurationInstance() {
        return super.getLastNonConfigurationInstance();
    }

    public FragmentManager superGetFragmentManager() {
        return super.getFragmentManager();
    }

    public void superStartManagingCursor(Cursor c) {
        super.startManagingCursor(c);
    }

    public void superStopManagingCursor(Cursor c) {
        super.stopManagingCursor(c);
    }

    public <T extends View> T superFindViewById(int id) {
        return super.findViewById(id);
    }

    public ActionBar superGetActionBar() {
        return super.getActionBar();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void superSetActionBar(Toolbar toolbar) {
        super.setActionBar(toolbar);
    }

    public void superSetContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }

    public void superSetContentView(View view) {
        super.setContentView(view);
    }

    public void superSetContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
    }

    public void superAddContentView(View view, ViewGroup.LayoutParams params) {
        super.addContentView(view, params);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TransitionManager superGetContentTransitionManager() {
        return super.getContentTransitionManager();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void superSetContentTransitionManager(TransitionManager tm) {
        super.setContentTransitionManager(tm);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Scene superGetContentScene() {
        return super.getContentScene();
    }

    public void superSetFinishOnTouchOutside(boolean finish) {
        super.setFinishOnTouchOutside(finish);
    }

    public boolean superHasWindowFocus() {
        return super.hasWindowFocus();
    }

    public boolean superDispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }

    public boolean superDispatchKeyShortcutEvent(KeyEvent event) {
        return super.dispatchKeyShortcutEvent(event);
    }

    public boolean superDispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    public boolean superDispatchTrackballEvent(MotionEvent ev) {
        return super.dispatchTrackballEvent(ev);
    }

    public boolean superDispatchGenericMotionEvent(MotionEvent ev) {
        return super.dispatchGenericMotionEvent(ev);
    }

    public boolean superDispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        return super.dispatchPopulateAccessibilityEvent(event);
    }

    public void superInvalidateOptionsMenu() {
        super.invalidateOptionsMenu();
    }

    public void superOpenOptionsMenu() {
        super.openOptionsMenu();
    }

    public void superCloseOptionsMenu() {
        super.closeOptionsMenu();
    }

    public void superRegisterForContextMenu(View view) {
        super.registerForContextMenu(view);
    }

    public void superUnregisterForContextMenu(View view) {
        super.unregisterForContextMenu(view);
    }

    public void superOpenContextMenu(View view) {
        super.openContextMenu(view);
    }

    public void superCloseContextMenu() {
        super.closeContextMenu();
    }

    public void superStartSearch(String initialQuery, boolean selectInitialQuery, Bundle appSearchData, boolean globalSearch) {
        super.startSearch(initialQuery, selectInitialQuery, appSearchData, globalSearch);
    }

    public void superTriggerSearch(String query, Bundle appSearchData) {
        super.triggerSearch(query, appSearchData);
    }

    public void superTakeKeyEvents(boolean get) {
        super.takeKeyEvents(get);
    }

    public LayoutInflater superGetLayoutInflater() {
        return super.getLayoutInflater();
    }

    public MenuInflater superGetMenuInflater() {
        return super.getMenuInflater();
    }

    public void superSetTheme(int resid) {
        super.setTheme(resid);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean superShouldShowRequestPermissionRationale(String permission) {
        return super.shouldShowRequestPermissionRationale(permission);
    }

    public void superStartActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void superStartActivityForResult(Intent intent, int requestCode, Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
    }

    @TargetApi(Build.VERSION_CODES.O)
    public boolean superIsActivityTransitionRunning() {
        return super.isActivityTransitionRunning();
    }

    public void superStartIntentSenderForResult(IntentSender intent, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags) throws IntentSender.SendIntentException {
        super.startIntentSenderForResult(intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void superStartIntentSenderForResult(IntentSender intent, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, Bundle options) throws IntentSender.SendIntentException {
        super.startIntentSenderForResult(intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags, options);
    }

    public void superStartActivity(Intent intent) {
        super.startActivity(intent);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void superStartActivity(Intent intent, Bundle options) {
        super.startActivity(intent, options);
    }

    public void superStartActivities(Intent[] intents) {
        super.startActivities(intents);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void superStartActivities(Intent[] intents, Bundle options) {
        super.startActivities(intents, options);
    }

    public void superStartIntentSender(IntentSender intent, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags) throws IntentSender.SendIntentException {
        super.startIntentSender(intent, fillInIntent, flagsMask, flagsValues, extraFlags);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void superStartIntentSender(IntentSender intent, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, Bundle options) throws IntentSender.SendIntentException {
        super.startIntentSender(intent, fillInIntent, flagsMask, flagsValues, extraFlags, options);
    }

    public boolean superStartActivityIfNeeded(Intent intent, int requestCode) {
        return super.startActivityIfNeeded(intent, requestCode);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean superStartActivityIfNeeded(Intent intent, int requestCode, Bundle options) {
        return super.startActivityIfNeeded(intent, requestCode, options);
    }

    public boolean superStartNextMatchingActivity(Intent intent) {
        return super.startNextMatchingActivity(intent);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean superStartNextMatchingActivity(Intent intent, Bundle options) {
        return super.startNextMatchingActivity(intent, options);
    }

    public void superStartActivityFromChild(Activity child, Intent intent, int requestCode) {
        super.startActivityFromChild(child, intent, requestCode);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void superStartActivityFromChild(Activity child, Intent intent, int requestCode, Bundle options) {
        super.startActivityFromChild(child, intent, requestCode, options);
    }

    public void superStartActivityFromFragment(Fragment fragment, Intent intent, int requestCode) {
        super.startActivityFromFragment(fragment, intent, requestCode);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void superStartActivityFromFragment(Fragment fragment, Intent intent, int requestCode, Bundle options) {
        super.startActivityFromFragment(fragment, intent, requestCode, options);
    }

    public void superStartIntentSenderFromChild(Activity child, IntentSender intent, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags) throws IntentSender.SendIntentException {
        super.startIntentSenderFromChild(child, intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void superStartIntentSenderFromChild(Activity child, IntentSender intent, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, Bundle options) throws IntentSender.SendIntentException {
        super.startIntentSenderFromChild(child, intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags, options);
    }

    public void superOverridePendingTransition(int enterAnim, int exitAnim) {
        super.overridePendingTransition(enterAnim, exitAnim);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    public Uri superGetReferrer() {
        return super.getReferrer();
    }

    public String superGetCallingPackage() {
        return super.getCallingPackage();
    }

    public ComponentName superGetCallingActivity() {
        return super.getCallingActivity();
    }

    public void superSetVisible(boolean visible) {
        super.setVisible(visible);
    }

    public boolean superIsFinishing() {
        return super.isFinishing();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public boolean superIsDestroyed() {
        return super.isDestroyed();
    }

    @Override
    public boolean superIsChangingConfigurations() {
        return super.isChangingConfigurations();
    }

    public void superRecreate() {
        super.recreate();
    }

    public void superFinish() {
        super.finish();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void superFinishAffinity() {
        super.finishAffinity();
    }

    public void superFinishFromChild(Activity child) {
        super.finishFromChild(child);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void superFinishAfterTransition() {
        super.finishAfterTransition();
    }

    public void superFinishActivity(int requestCode) {
        super.finishActivity(requestCode);
    }

    public void superFinishActivityFromChild(Activity child, int requestCode) {
        super.finishActivityFromChild(child, requestCode);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void superFinishAndRemoveTask() {
        super.finishAndRemoveTask();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean superReleaseInstance() {
        return super.releaseInstance();
    }

    public PendingIntent superCreatePendingResult(int requestCode, Intent data, int flags) {
        return super.createPendingResult(requestCode, data, flags);
    }

    public void superSetRequestedOrientation(int requestedOrientation) {
        super.setRequestedOrientation(requestedOrientation);
    }

    public int superGetRequestedOrientation() {
        return super.getRequestedOrientation();
    }

    public int superGetTaskId() {
        return super.getTaskId();
    }

    public boolean superIsTaskRoot() {
        return super.isTaskRoot();
    }

    public boolean superMoveTaskToBack(boolean nonRoot) {
        return super.moveTaskToBack(nonRoot);
    }

    public String superGetLocalClassName() {
        return super.getLocalClassName();
    }

    public ComponentName superGetComponentName() {
        return super.getComponentName();
    }

    public SharedPreferences superGetPreferences(int mode) {
        return super.getPreferences(mode);
    }

    public Object superGetSystemService(String name) {
        return super.getSystemService(name);
    }

    public void superSetTitle(CharSequence title) {
        super.setTitle(title);
    }

    public void superSetTitle(int titleId) {
        super.setTitle(titleId);
    }

    public void superSetTitleColor(int textColor) {
        super.setTitleColor(textColor);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void superSetTaskDescription(ActivityManager.TaskDescription taskDescription) {
        super.setTaskDescription(taskDescription);
    }

    public void superDump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        super.dump(prefix, fd, writer, args);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean superIsImmersive() {
        return super.isImmersive();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean superRequestVisibleBehind(boolean visible) {
        return super.requestVisibleBehind(visible);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void superSetImmersive(boolean i) {
        super.setImmersive(i);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void superSetVrModeEnabled(boolean enabled, ComponentName requestedComponent) throws PackageManager.NameNotFoundException {
        super.setVrModeEnabled(enabled, requestedComponent);
    }

    public ActionMode superStartActionMode(ActionMode.Callback callback) {
        return super.startActionMode(callback);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public ActionMode superStartActionMode(ActionMode.Callback callback, int type) {
        return super.startActionMode(callback, type);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean superShouldUpRecreateTask(Intent targetIntent) {
        return super.shouldUpRecreateTask(targetIntent);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean superNavigateUpTo(Intent upIntent) {
        return super.navigateUpTo(upIntent);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean superNavigateUpToFromChild(Activity child, Intent upIntent) {
        return super.navigateUpToFromChild(child, upIntent);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public Intent superGetParentActivityIntent() {
        return super.getParentActivityIntent();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void superSetEnterSharedElementCallback(SharedElementCallback callback) {
        super.setEnterSharedElementCallback(callback);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void superSetExitSharedElementCallback(SharedElementCallback callback) {
        super.setExitSharedElementCallback(callback);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void superPostponeEnterTransition() {
        super.postponeEnterTransition();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void superStartPostponedEnterTransition() {
        super.startPostponedEnterTransition();
    }

    @TargetApi(Build.VERSION_CODES.N)
    public DragAndDropPermissions superRequestDragAndDropPermissions(DragEvent event) {
        return super.requestDragAndDropPermissions(event);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void superStartLockTask() {
        super.startLockTask();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void superStopLockTask() {
        super.stopLockTask();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void superShowLockTaskEscapeMessage() {
        super.showLockTaskEscapeMessage();
    }

    @TargetApi(Build.VERSION_CODES.O_MR1)
    public void superSetShowWhenLocked(boolean showWhenLocked) {
        super.setShowWhenLocked(showWhenLocked);
    }

    @TargetApi(Build.VERSION_CODES.O_MR1)
    public void superSetTurnScreenOn(boolean turnScreenOn) {
        super.setTurnScreenOn(turnScreenOn);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void superApplyOverrideConfiguration(Configuration overrideConfiguration) {
        super.applyOverrideConfiguration(overrideConfiguration);
    }

    public AssetManager superGetAssets() {
        return super.getAssets();
    }

    public Resources superGetResources() {
        return super.getResources();
    }

    public Resources.Theme superGetTheme() {
        return super.getTheme();
    }

    public Context superGetBaseContext() {
        return super.getBaseContext();
    }

    public PackageManager superGetPackageManager() {
        return super.getPackageManager();
    }

    public ContentResolver superGetContentResolver() {
        return super.getContentResolver();
    }

    public Looper superGetMainLooper() {
        return super.getMainLooper();
    }

    public Context superGetApplicationContext() {
        return super.getApplicationContext();
    }

    public ClassLoader superGetClassLoader() {
        return super.getClassLoader();
    }

    public String superGetPackageName() {
        return super.getPackageName();
    }

    public ApplicationInfo superGetApplicationInfo() {
        return super.getApplicationInfo();
    }

    public String superGetPackageResourcePath() {
        return super.getPackageResourcePath();
    }

    public String superGetPackageCodePath() {
        return super.getPackageCodePath();
    }

    public SharedPreferences superGetSharedPreferences(String name, int mode) {
        return super.getSharedPreferences(name, mode);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public boolean superMoveSharedPreferencesFrom(Context sourceContext, String name) {
        return super.moveSharedPreferencesFrom(sourceContext, name);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public boolean superDeleteSharedPreferences(String name) {
        return super.deleteSharedPreferences(name);
    }

    public FileInputStream superOpenFileInput(String name) throws FileNotFoundException {
        return super.openFileInput(name);
    }

    public FileOutputStream superOpenFileOutput(String name, int mode) throws FileNotFoundException {
        return super.openFileOutput(name, mode);
    }

    public boolean superDeleteFile(String name) {
        return super.deleteFile(name);
    }

    public File superGetFileStreamPath(String name) {
        return super.getFileStreamPath(name);
    }

    public String[] superFileList() {
        return super.fileList();
    }

    @TargetApi(Build.VERSION_CODES.N)
    public File superGetDataDir() {
        return super.getDataDir();
    }

    public File superGetFilesDir() {
        return super.getFilesDir();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public File superGetNoBackupFilesDir() {
        return super.getNoBackupFilesDir();
    }

    public File superGetExternalFilesDir(String type) {
        return super.getExternalFilesDir(type);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public File[] superGetExternalFilesDirs(String type) {
        return super.getExternalFilesDirs(type);
    }

    public File superGetObbDir() {
        return super.getObbDir();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public File[] superGetObbDirs() {
        return super.getObbDirs();
    }

    public File superGetCacheDir() {
        return super.getCacheDir();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public File superGetCodeCacheDir() {
        return super.getCodeCacheDir();
    }

    public File superGetExternalCacheDir() {
        return super.getExternalCacheDir();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public File[] superGetExternalCacheDirs() {
        return super.getExternalCacheDirs();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public File[] superGetExternalMediaDirs() {
        return super.getExternalMediaDirs();
    }

    public File superGetDir(String name, int mode) {
        return super.getDir(name, mode);
    }

    public SQLiteDatabase superOpenOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
        return super.openOrCreateDatabase(name, mode, factory);
    }

    public SQLiteDatabase superOpenOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        return super.openOrCreateDatabase(name, mode, factory, errorHandler);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public boolean superMoveDatabaseFrom(Context sourceContext, String name) {
        return super.moveDatabaseFrom(sourceContext, name);
    }

    public boolean superDeleteDatabase(String name) {
        return super.deleteDatabase(name);
    }

    public File superGetDatabasePath(String name) {
        return super.getDatabasePath(name);
    }

    public String[] superDatabaseList() {
        return super.databaseList();
    }

    public Drawable superGetWallpaper() {
        return super.getWallpaper();
    }

    public Drawable superPeekWallpaper() {
        return super.peekWallpaper();
    }

    public int superGetWallpaperDesiredMinimumWidth() {
        return super.getWallpaperDesiredMinimumWidth();
    }

    public int superGetWallpaperDesiredMinimumHeight() {
        return super.getWallpaperDesiredMinimumHeight();
    }

    public void superSetWallpaper(Bitmap bitmap) throws IOException {
    }

    public void superSetWallpaper(InputStream data) throws IOException {
    }

    public void superClearWallpaper() throws IOException {
    }

    public void superSendBroadcast(Intent intent) {
        super.sendBroadcast(intent);
    }

    public void superSendBroadcast(Intent intent, String receiverPermission) {
        super.sendBroadcast(intent, receiverPermission);
    }

    public void superSendOrderedBroadcast(Intent intent, String receiverPermission) {
        super.sendOrderedBroadcast(intent, receiverPermission);
    }

    public void superSendOrderedBroadcast(Intent intent, String receiverPermission, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras) {
        super.sendOrderedBroadcast(intent, receiverPermission, resultReceiver, scheduler, initialCode, initialData, initialExtras);
    }

    @SuppressLint("MissingPermission")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void superSendBroadcastAsUser(Intent intent, UserHandle user) {
        super.sendBroadcastAsUser(intent, user);
    }

    @SuppressLint("MissingPermission")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void superSendBroadcastAsUser(Intent intent, UserHandle user, String receiverPermission) {
        super.sendBroadcastAsUser(intent, user, receiverPermission);
    }

    public void superSendOrderedBroadcastAsUser(Intent intent, UserHandle user, String receiverPermission, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras) {
    }

    @SuppressLint("MissingPermission")
    public void superSendStickyBroadcast(Intent intent) {
        super.sendStickyBroadcast(intent);
    }

    @SuppressLint("MissingPermission")
    public void superSendStickyOrderedBroadcast(Intent intent, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras) {
        super.sendStickyOrderedBroadcast(intent, resultReceiver, scheduler, initialCode, initialData, initialExtras);
    }

    @SuppressLint("MissingPermission")
    public void superRemoveStickyBroadcast(Intent intent) {
        super.removeStickyBroadcast(intent);
    }

    @SuppressLint("MissingPermission")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void superSendStickyBroadcastAsUser(Intent intent, UserHandle user) {
        super.sendStickyBroadcastAsUser(intent, user);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint("MissingPermission")
    public void superSendStickyOrderedBroadcastAsUser(Intent intent, UserHandle user, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras) {
        super.sendStickyOrderedBroadcastAsUser(intent, user, resultReceiver, scheduler, initialCode, initialData, initialExtras);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint("MissingPermission")
    public void superRemoveStickyBroadcastAsUser(Intent intent, UserHandle user) {
        super.removeStickyBroadcastAsUser(intent, user);
    }

    public Intent superRegisterReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        return super.registerReceiver(receiver, filter);
    }

    @TargetApi(Build.VERSION_CODES.O)
    public Intent superRegisterReceiver(BroadcastReceiver receiver, IntentFilter filter, int flags) {
        return super.registerReceiver(receiver, filter, flags);
    }

    public Intent superRegisterReceiver(BroadcastReceiver receiver, IntentFilter filter, String broadcastPermission, Handler scheduler) {
        return super.registerReceiver(receiver, filter, broadcastPermission, scheduler);
    }

    @TargetApi(Build.VERSION_CODES.O)
    public Intent superRegisterReceiver(BroadcastReceiver receiver, IntentFilter filter, String broadcastPermission, Handler scheduler, int flags) {
        return super.registerReceiver(receiver, filter, broadcastPermission, scheduler, flags);
    }

    public void superUnregisterReceiver(BroadcastReceiver receiver) {
        super.unregisterReceiver(receiver);
    }

    public ComponentName superStartService(Intent service) {
        return super.startService(service);
    }

    @TargetApi(Build.VERSION_CODES.O)
    public ComponentName superStartForegroundService(Intent service) {
        return super.startForegroundService(service);
    }

    public boolean superStopService(Intent name) {
        return super.stopService(name);
    }

    public boolean superBindService(Intent service, ServiceConnection conn, int flags) {
        return super.bindService(service, conn, flags);
    }

    public void superUnbindService(ServiceConnection conn) {
        super.unbindService(conn);
    }

    public boolean superStartInstrumentation(ComponentName className, String profileFile, Bundle arguments) {
        return super.startInstrumentation(className, profileFile, arguments);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public String superGetSystemServiceName(Class<?> serviceClass) {
        return super.getSystemServiceName(serviceClass);
    }

    public int superCheckPermission(String permission, int pid, int uid) {
        return super.checkPermission(permission, pid, uid);
    }

    public int superCheckCallingPermission(String permission) {
        return super.checkCallingPermission(permission);
    }

    public int superCheckCallingOrSelfPermission(String permission) {
        return super.checkCallingOrSelfPermission(permission);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public int superCheckSelfPermission(String permission) {
        return super.checkSelfPermission(permission);
    }

    public void superEnforcePermission(String permission, int pid, int uid, String message) {
        super.enforcePermission(permission, pid, uid, message);
    }

    public void superEnforceCallingPermission(String permission, String message) {
        super.enforceCallingPermission(permission, message);
    }

    public void superEnforceCallingOrSelfPermission(String permission, String message) {
        super.enforceCallingOrSelfPermission(permission, message);
    }

    public void superGrantUriPermission(String toPackage, Uri uri, int modeFlags) {
        super.grantUriPermission(toPackage, uri, modeFlags);
    }

    public void superRevokeUriPermission(Uri uri, int modeFlags) {
        super.revokeUriPermission(uri, modeFlags);
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void superRevokeUriPermission(String targetPackage, Uri uri, int modeFlags) {
        super.revokeUriPermission(targetPackage, uri, modeFlags);
    }

    public int superCheckUriPermission(Uri uri, int pid, int uid, int modeFlags) {
        return super.checkUriPermission(uri, pid, uid, modeFlags);
    }

    public int superCheckCallingUriPermission(Uri uri, int modeFlags) {
        return super.checkCallingUriPermission(uri, modeFlags);
    }

    public int superCheckCallingOrSelfUriPermission(Uri uri, int modeFlags) {
        return super.checkCallingOrSelfUriPermission(uri, modeFlags);
    }

    public int superCheckUriPermission(Uri uri, String readPermission, String writePermission, int pid, int uid, int modeFlags) {
        return super.checkUriPermission(uri, readPermission, writePermission, pid, uid, modeFlags);
    }

    public void superEnforceUriPermission(Uri uri, int pid, int uid, int modeFlags, String message) {
        super.enforceUriPermission(uri, pid, uid, modeFlags, message);
    }

    public void superEnforceCallingUriPermission(Uri uri, int modeFlags, String message) {
        super.enforceCallingUriPermission(uri, modeFlags, message);
    }

    public void superEnforceCallingOrSelfUriPermission(Uri uri, int modeFlags, String message) {
        super.enforceCallingOrSelfUriPermission(uri, modeFlags, message);
    }

    public void superEnforceUriPermission(Uri uri, String readPermission, String writePermission, int pid, int uid, int modeFlags, String message) {
        super.enforceUriPermission(uri, readPermission, writePermission, pid, uid, modeFlags, message);
    }

    public Context superCreatePackageContext(String packageName, int flags) throws PackageManager.NameNotFoundException {
        return super.createPackageContext(packageName, flags);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public Context superCreateConfigurationContext(Configuration overrideConfiguration) {
        return super.createConfigurationContext(overrideConfiguration);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public Context superCreateDisplayContext(Display display) {
        return super.createDisplayContext(display);
    }

    public boolean superIsRestricted() {
        return super.isRestricted();
    }

    @TargetApi(Build.VERSION_CODES.N)
    public Context superCreateDeviceProtectedStorageContext() {
        return super.createDeviceProtectedStorageContext();
    }

    @TargetApi(Build.VERSION_CODES.N)
    public boolean superIsDeviceProtectedStorage() {
        return super.isDeviceProtectedStorage();
    }

    @TargetApi(Build.VERSION_CODES.O)
    public Context superCreateContextForSplit(String splitName) throws PackageManager.NameNotFoundException {
        return super.createContextForSplit(splitName);
    }

    public void superRegisterComponentCallbacks(ComponentCallbacks callback) {
        super.registerComponentCallbacks(callback);
    }

    public void superUnregisterComponentCallbacks(ComponentCallbacks callback) {
        super.unregisterComponentCallbacks(callback);
    }

    public void superAttachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }

    public void superOnCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void superOnCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    public void superOnRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void superOnRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
    }

    public void superOnPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void superOnPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
    }

    public void superOnStart() {
        super.onStart();
    }

    public void superOnRestart() {
        super.onRestart();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void superOnStateNotSaved() {
        super.onStateNotSaved();
    }

    public void superOnResume() {
        super.onResume();
    }

    public void superOnPostResume() {
        super.onPostResume();
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void superOnLocalVoiceInteractionStarted() {
        super.onLocalVoiceInteractionStarted();
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void superOnLocalVoiceInteractionStopped() {
        super.onLocalVoiceInteractionStopped();
    }

    public void superOnNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    public void superOnSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void superOnSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    public void superOnPause() {
        super.onPause();
    }

    public void superOnUserLeaveHint() {
        super.onUserLeaveHint();
    }

    public boolean superOnCreateThumbnail(Bitmap outBitmap, Canvas canvas) {
        return super.onCreateThumbnail(outBitmap, canvas);
    }

    public CharSequence superOnCreateDescription() {
        return super.onCreateDescription();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void superOnProvideAssistData(Bundle data) {
        super.onProvideAssistData(data);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void superOnProvideAssistContent(AssistContent outContent) {
        super.onProvideAssistContent(outContent);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void superOnProvideKeyboardShortcuts(List<KeyboardShortcutGroup> data, Menu menu, int deviceId) {
        super.onProvideKeyboardShortcuts(data, menu, deviceId);
    }

    public void superOnStop() {
        super.onStop();
    }

    public void superOnDestroy() {
        super.onDestroy();
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void superOnMultiWindowModeChanged(boolean isInMultiWindowMode, Configuration newConfig) {
        super.onMultiWindowModeChanged(isInMultiWindowMode, newConfig);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void superOnMultiWindowModeChanged(boolean isInMultiWindowMode) {
        super.onMultiWindowModeChanged(isInMultiWindowMode);
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void superOnPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void superOnPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode);
    }

    public void superOnConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public Object superOnRetainNonConfigurationInstance() {
        return super.onRetainNonConfigurationInstance();
    }

    public void superOnLowMemory() {
        super.onLowMemory();
    }

    public void superOnTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    public void superOnAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
    }

    public boolean superOnKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    public boolean superOnKeyLongPress(int keyCode, KeyEvent event) {
        return super.onKeyLongPress(keyCode, event);
    }

    public boolean superOnKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }

    public boolean superOnKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        return super.onKeyMultiple(keyCode, repeatCount, event);
    }

    public void superOnBackPressed() {
        super.onBackPressed();
    }

    public boolean superOnKeyShortcut(int keyCode, KeyEvent event) {
        return super.onKeyShortcut(keyCode, event);
    }

    public boolean superOnTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public boolean superOnTrackballEvent(MotionEvent event) {
        return super.onTrackballEvent(event);
    }

    public boolean superOnGenericMotionEvent(MotionEvent event) {
        return super.onGenericMotionEvent(event);
    }

    public void superOnUserInteraction() {
        super.onUserInteraction();
    }

    public void superOnWindowAttributesChanged(WindowManager.LayoutParams params) {
        super.onWindowAttributesChanged(params);
    }

    public void superOnContentChanged() {
        super.onContentChanged();
    }

    public void superOnWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    public void superOnAttachedToWindow() {
        super.onAttachedToWindow();
    }

    public void superOnDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public View superOnCreatePanelView(int featureId) {
        return super.onCreatePanelView(featureId);
    }

    public boolean superOnCreatePanelMenu(int featureId, Menu menu) {
        return super.onCreatePanelMenu(featureId, menu);
    }

    public boolean superOnPreparePanel(int featureId, View view, Menu menu) {
        return super.onPreparePanel(featureId, view, menu);
    }

    public boolean superOnMenuOpened(int featureId, Menu menu) {
        return super.onMenuOpened(featureId, menu);
    }

    public boolean superOnMenuItemSelected(int featureId, MenuItem item) {
        return super.onMenuItemSelected(featureId, item);
    }

    public void superOnPanelClosed(int featureId, Menu menu) {
        super.onPanelClosed(featureId, menu);
    }

    public boolean superOnCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    public boolean superOnPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    public boolean superOnOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean superOnNavigateUp() {
        return super.onNavigateUp();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean superOnNavigateUpFromChild(Activity child) {
        return super.onNavigateUpFromChild(child);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void superOnCreateNavigateUpTaskStack(TaskStackBuilder builder) {
        super.onCreateNavigateUpTaskStack(builder);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void superOnPrepareNavigateUpTaskStack(TaskStackBuilder builder) {
        super.onPrepareNavigateUpTaskStack(builder);
    }

    public void superOnOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
    }

    public void superOnCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    public boolean superOnContextItemSelected(MenuItem item) {
        return super.onContextItemSelected(item);
    }

    public void superOnContextMenuClosed(Menu menu) {
        super.onContextMenuClosed(menu);
    }

    public Dialog superOnCreateDialog(int id) {
        return super.onCreateDialog(id);
    }

    public Dialog superOnCreateDialog(int id, Bundle args) {
        return super.onCreateDialog(id, args);
    }

    public void superOnPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
    }

    public void superOnPrepareDialog(int id, Dialog dialog, Bundle args) {
        super.onPrepareDialog(id, dialog, args);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean superOnSearchRequested(SearchEvent searchEvent) {
        return super.onSearchRequested(searchEvent);
    }

    public boolean superOnSearchRequested() {
        return super.onSearchRequested();
    }

    public void superOnApplyThemeResource(Resources.Theme theme, int resid, boolean first) {
        super.onApplyThemeResource(theme, resid, first);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void superOnRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public Uri superOnProvideReferrer() {
        return super.onProvideReferrer();
    }

    public void superOnActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void superOnActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
    }

    public void superOnTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
    }

    public void superOnChildTitleChanged(Activity childActivity, CharSequence title) {
        super.onChildTitleChanged(childActivity, title);
    }

    public View superOnCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    public View superOnCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void superOnVisibleBehindCanceled() {
        super.onVisibleBehindCanceled();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void superOnEnterAnimationComplete() {
        super.onEnterAnimationComplete();
    }

    public ActionMode superOnWindowStartingActionMode(ActionMode.Callback callback) {
        return super.onWindowStartingActionMode(callback);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public ActionMode superOnWindowStartingActionMode(ActionMode.Callback callback, int type) {
        return super.onWindowStartingActionMode(callback, type);
    }

    public void superOnActionModeStarted(ActionMode mode) {
        super.onActionModeStarted(mode);
    }

    public void superOnActionModeFinished(ActionMode mode) {
        super.onActionModeFinished(mode);
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void superOnPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (hostActivityDelegate != null) {
            hostActivityDelegate.onAttachFragment(fragment);
        }
    }

    @Override
    public void onAttachedToWindow() {
        if (hostActivityDelegate != null) {
            hostActivityDelegate.onAttachedToWindow();
        }
    }

    @Override
    public void onDetachedFromWindow() {
        if (hostActivityDelegate != null) {
            hostActivityDelegate.onDetachedFromWindow();
        }
    }

    /**
     * Theme一旦设置了就不能更换Theme所在的Resouces了，见{@link Resources.Theme#setTo(Resources.Theme)}
     * 而Activity在OnCreate之前需要设置Theme和使用Theme。我们需要在Activity OnCreate之后才能注入插件资源。
     * 这就需要在Activity OnCreate之前不要调用Activity的setTheme方法，同时在getTheme时返回宿主的Theme资源。
     * 注：{@link Activity#setTheme(int)}会触发初始化Theme，因此不能调用。
     */
    private Resources.Theme mHostTheme;

    @Override
    public Resources.Theme getTheme() {
        if (isBeforeOnCreate) {
            if (mHostTheme == null) {
                mHostTheme = super.getResources().newTheme();
            }
            return mHostTheme;
        } else {
            return super.getTheme();
        }
    }

    @Override
    public void setTheme(int resid) {
        if (!isBeforeOnCreate) {
            super.setTheme(resid);
        }
    }

    @Override
    public void recreate() {
        if (hostActivityDelegate != null) {
            hostActivityDelegate.recreate();
        } else {
            super.recreate();
        }
    }

    @Override
    public ComponentName getCallingActivity() {
        if (hostActivityDelegate != null) {
            return hostActivityDelegate.getCallingActivity();
        } else {
            return super.getCallingActivity();
        }
    }

    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        if (hostActivityDelegate != null) {
            hostActivityDelegate.onMultiWindowModeChanged(isInMultiWindowMode);
        } else {
            super.onMultiWindowModeChanged(isInMultiWindowMode);
        }
    }

    public void onMultiWindowModeChanged(boolean isInMultiWindowMode, Configuration newConfig) {
        if (hostActivityDelegate != null) {
            hostActivityDelegate.onMultiWindowModeChanged(isInMultiWindowMode, newConfig);
        } else {
            super.onMultiWindowModeChanged(isInMultiWindowMode, newConfig);
        }
    }
}
