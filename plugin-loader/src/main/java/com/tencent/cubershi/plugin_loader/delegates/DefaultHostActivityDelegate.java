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
    private HostActivityDelegator mHostActivityDelegator;
    final private DexClassLoader mPluginClassLoader;

    public DefaultHostActivityDelegate(DexClassLoader pluginClassLoader) {
        mPluginClassLoader = pluginClassLoader;
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
            mockActivity.performOnCreate(bundle);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onNewIntent(Intent intent) {

    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        return false;
    }

    @Override
    public void finish() {

    }

    @Override
    public void onActivityResult(int i, int i1, Intent intent) {

    }

    @Override
    public void onChildTitleChanged(Activity activity, CharSequence charSequence) {

    }

    @Override
    public void onRestoreInstanceState(Bundle bundle) {

    }

    @Override
    public void onPostCreate(Bundle bundle) {

    }

    @Override
    public void onRestart() {

    }

    @Override
    public void onUserLeaveHint() {

    }

    @Override
    public boolean onCreateThumbnail(Bitmap bitmap, Canvas canvas) {
        return false;
    }

    @Override
    public CharSequence onCreateDescription() {
        return null;
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        return null;
    }

    @Override
    public void onLowMemory() {

    }

    @Override
    public boolean onTrackballEvent(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onUserInteraction() {

    }

    @Override
    public void onWindowAttributesChanged(WindowManager.LayoutParams layoutParams) {

    }

    @Override
    public void onContentChanged() {

    }

    @Override
    public void onWindowFocusChanged(boolean b) {

    }

    @Override
    public View onCreatePanelView(int i) {
        return null;
    }

    @Override
    public boolean onCreatePanelMenu(int i, Menu menu) {
        return false;
    }

    @Override
    public boolean onPreparePanel(int i, View view, Menu menu) {
        return false;
    }

    @Override
    public void onPanelClosed(int i, Menu menu) {

    }

    @Override
    public Dialog onCreateDialog(int i) {
        return null;
    }

    @Override
    public void onPrepareDialog(int i, Dialog dialog) {

    }

    @Override
    public void onApplyThemeResource(Resources.Theme theme, int i, boolean b) {

    }

    @Override
    public View onCreateView(String s, Context context, AttributeSet attributeSet) {
        return null;
    }

    @Override
    public void startActivityFromChild(Activity activity, Intent intent, int i) {

    }

    @Override
    public ClassLoader getClassLoader() {
        return null;
    }
}
