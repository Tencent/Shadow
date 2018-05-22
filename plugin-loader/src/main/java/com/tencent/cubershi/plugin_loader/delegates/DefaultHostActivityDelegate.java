package com.tencent.cubershi.plugin_loader.delegates;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.tencent.cubershi.mock_interface.MockActivity;
import com.tencent.cubershi.plugin_loader.test.FakeRunningPlugin;
import com.tencent.hydevteam.pluginframework.plugincontainer.HostActivityDelegate;
import com.tencent.hydevteam.pluginframework.plugincontainer.HostActivityDelegator;

import dalvik.system.DexClassLoader;

public class DefaultHostActivityDelegate implements HostActivityDelegate {
    @SuppressWarnings("NullableProblems")
    @NonNull//HostActivityDelegate被传递给HostActivity后应立刻被调用setDelegator()方法传入HostActivityDelegator.
    private HostActivityDelegator mHostActivityDelegator;
    final private DexClassLoader mPluginClassLoader;
    final private Resources mPluginResources;

    public DefaultHostActivityDelegate(DexClassLoader pluginClassLoader, Resources pluginResources) {
        mPluginClassLoader = pluginClassLoader;
        mPluginResources = pluginResources;
    }

    @Override
    public void setDelegator(HostActivityDelegator hostActivityDelegator) {
        mHostActivityDelegator = hostActivityDelegator;
    }

    @Override
    public void onCreate(Bundle bundle) {
        mHostActivityDelegator.superOnCreate(bundle);
        String pluginLauncherActivityName = mHostActivityDelegator.getIntent().getStringExtra(FakeRunningPlugin.ARG);
        try {
            Class<?> aClass = mPluginClassLoader.loadClass(pluginLauncherActivityName);
            MockActivity mockActivity = MockActivity.class.cast(aClass.newInstance());
            mockActivity.setContainerActivity(mHostActivityDelegator);
            mockActivity.setPluginResources(mPluginResources);
            mockActivity.performOnCreate(bundle);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onResume() {
        mHostActivityDelegator.superOnResume();
    }

    @Override
    public void onNewIntent(Intent intent) {

    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {

    }

    @Override
    public void onPause() {
        mHostActivityDelegator.superOnPause();
    }

    @Override
    public void onStop() {
        mHostActivityDelegator.superOnStop();
    }

    @Override
    public void onDestroy() {
        mHostActivityDelegator.superOnDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        mHostActivityDelegator.superOnConfigurationChanged(configuration);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        return mHostActivityDelegator.superDispatchKeyEvent(keyEvent);
    }

    @Override
    public void finish() {
        mHostActivityDelegator.superFinish();
    }

    @Override
    public void onActivityResult(int i, int i1, Intent intent) {
        mHostActivityDelegator.superOnActivityResult(i, i1, intent);
    }

    @Override
    public void onChildTitleChanged(Activity activity, CharSequence charSequence) {

    }

    @Override
    public void onRestoreInstanceState(Bundle bundle) {
        mHostActivityDelegator.superOnRestoreInstanceState(bundle);
    }

    @Override
    public void onPostCreate(Bundle bundle) {
        mHostActivityDelegator.superOnPostCreate(bundle);
    }

    @Override
    public void onRestart() {
        mHostActivityDelegator.superOnRestart();
    }

    @Override
    public void onUserLeaveHint() {
        mHostActivityDelegator.superOnUserLeaveHint();
    }

    @Override
    public boolean onCreateThumbnail(Bitmap bitmap, Canvas canvas) {
        return mHostActivityDelegator.superOnCreateThumbnail(bitmap, canvas);
    }

    @Override
    public CharSequence onCreateDescription() {
        return mHostActivityDelegator.superOnCreateDescription();
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        return mHostActivityDelegator.superOnRetainNonConfigurationInstance();
    }

    @Override
    public void onLowMemory() {
        mHostActivityDelegator.superOnLowMemory();
    }

    @Override
    public boolean onTrackballEvent(MotionEvent motionEvent) {
        return mHostActivityDelegator.superOnTrackballEvent(motionEvent);
    }

    @Override
    public void onUserInteraction() {
        mHostActivityDelegator.superOnUserInteraction();
    }

    @Override
    public void onWindowAttributesChanged(WindowManager.LayoutParams layoutParams) {
        mHostActivityDelegator.superOnWindowAttributesChanged(layoutParams);
    }

    @Override
    public void onContentChanged() {
        mHostActivityDelegator.superOnContentChanged();
    }

    @Override
    public void onWindowFocusChanged(boolean b) {
        mHostActivityDelegator.superOnWindowFocusChanged(b);
    }

    @Override
    public View onCreatePanelView(int i) {
        return mHostActivityDelegator.superOnCreatePanelView(i);
    }

    @Override
    public boolean onCreatePanelMenu(int i, Menu menu) {
        return mHostActivityDelegator.superOnCreatePanelMenu(i, menu);
    }

    @Override
    public boolean onPreparePanel(int i, View view, Menu menu) {
        return mHostActivityDelegator.superOnPreparePanel(i, view, menu);
    }

    @Override
    public void onPanelClosed(int i, Menu menu) {
        mHostActivityDelegator.superOnPanelClosed(i, menu);
    }

    @Override
    public Dialog onCreateDialog(int i) {
        return mHostActivityDelegator.superOnCreateDialog(i);
    }

    @Override
    public void onPrepareDialog(int i, Dialog dialog) {
        mHostActivityDelegator.superOnPrepareDialog(i, dialog);
    }

    @Override
    public void onApplyThemeResource(Resources.Theme theme, int i, boolean b) {
        mHostActivityDelegator.superOnApplyThemeResource(theme, i, b);
    }

    @Override
    public View onCreateView(String s, Context context, AttributeSet attributeSet) {
        return mHostActivityDelegator.superOnCreateView(s, context, attributeSet);
    }

    @Override
    public void startActivityFromChild(Activity activity, Intent intent, int i) {
        mHostActivityDelegator.superStartActivityFromChild(activity, intent, i);
    }

    @Override
    public ClassLoader getClassLoader() {
        return mHostActivityDelegator.superGetClassLoader();
    }
}
