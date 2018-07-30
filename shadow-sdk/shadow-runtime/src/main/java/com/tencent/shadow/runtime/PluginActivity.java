package com.tencent.shadow.runtime;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.tencent.hydevteam.pluginframework.plugincontainer.HostActivityDelegator;

public abstract class PluginActivity extends ShadowContext {
    HostActivityDelegator mHostActivityDelegator;

    ShadowApplication mPluginApplication;

    PackageManager mPluginPackageManager;

    protected Pair<String,Bundle> mPluginLoaderBundle;

    public final void setHostContextAsBase(Context context) {
        attachBaseContext(context);
    }

    public void setContainerActivity(HostActivityDelegator delegator) {
        mHostActivityDelegator = delegator;
    }

    public void setPluginApplication(ShadowApplication pluginApplication) {
        mPluginApplication = pluginApplication;
    }

    public void setPluginPackageManager(PackageManager packageManager) {
        mPluginPackageManager = packageManager;
    }

    public void setPluginLoaderBundle(Pair<String,Bundle> pluginLoaderBundle) {
        this.mPluginLoaderBundle = pluginLoaderBundle;
    }

    public void onCreate(Bundle savedInstanceState) {
        mHostActivityDelegator.superOnCreate(savedInstanceState);
    }

    public void onResume() {

    }

    public void onNewIntent(Intent intent) {
    }

    public void onSaveInstanceState(Bundle outState) {
    }

    public void onPause() {
    }

    public void onStart() {
    }

    public void onStop() {
    }

    public void onDestroy() {
    }

    public void onConfigurationChanged(Configuration newConfig) {
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        return false;
    }

    public void finish() {
        mHostActivityDelegator.superFinish();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    public void onChildTitleChanged(Activity childActivity, CharSequence title) {
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
    }

    public void onPostCreate(Bundle savedInstanceState) {
    }

    public void onRestart() {
    }

    public void onUserLeaveHint() {
    }

    public boolean onCreateThumbnail(Bitmap outBitmap, Canvas canvas) {
        return false;
    }

    public CharSequence onCreateDescription() {
        return null;
    }

    public Object onRetainNonConfigurationInstance() {
        return null;
    }

    public void onLowMemory() {
    }

    public boolean onTrackballEvent(MotionEvent event) {
        return false;
    }

    public void onUserInteraction() {
    }

    public void onWindowAttributesChanged(WindowManager.LayoutParams params) {
    }

    public void onContentChanged() {
    }

    public void onWindowFocusChanged(boolean hasFocus) {
    }

    public View onCreatePanelView(int featureId) {
        return null;
    }

    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        return false;
    }

    public boolean onPreparePanel(int featureId, View view, Menu menu) {
        return false;
    }

    public void onPanelClosed(int featureId, Menu menu) {
    }

    public Dialog onCreateDialog(int id) {
        return null;
    }

    public void onPrepareDialog(int id, Dialog dialog) {
    }

    public void onApplyThemeResource(Resources.Theme theme, int resid, boolean first) {
    }

    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return null;
    }

    public void startActivityFromChild(Activity child, Intent intent, int requestCode) {
    }

    public LayoutInflater getLayoutInflater() {
        LayoutInflater inflater = (LayoutInflater) mHostActivityDelegator.getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        inflater.cloneInContext(this);
        return new FixedContextLayoutInflater(inflater, this);
    }

    public void onBackPressed() {
        mHostActivityDelegator.superOnBackPressed();
    }
}
