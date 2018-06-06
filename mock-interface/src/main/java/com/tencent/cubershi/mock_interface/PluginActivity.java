package com.tencent.cubershi.mock_interface;

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
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.tencent.hydevteam.pluginframework.plugincontainer.HostActivityDelegator;

public abstract class PluginActivity extends ContextThemeWrapper {
    HostActivityDelegator mHostActivityDelegator;

    Resources mPluginResources;

    ClassLoader mPluginClassLoader;

    PluginActivityLauncher mPluginActivityLauncher;

    MockApplication mPluginApplication;

    PackageManager mPluginPackageManager;

    public final void setPluginResources(Resources resources) {
        mPluginResources = resources;
    }

    public final void setHostContextAsBase(Context context) {
        attachBaseContext(context);
    }

    public final void setPluginClassLoader(ClassLoader classLoader) {
        mPluginClassLoader = classLoader;
    }

    public void setContainerActivity(HostActivityDelegator delegator) {
        mHostActivityDelegator = delegator;
    }

    public void setPluginActivityLauncher(PluginActivityLauncher pluginActivityLauncher) {
        mPluginActivityLauncher = pluginActivityLauncher;
    }

    public void setPluginApplication(MockApplication pluginApplication) {
        mPluginApplication = pluginApplication;
    }

    public void setPluginPackageManager(PackageManager packageManager) {
        mPluginPackageManager = packageManager;
    }

    public interface PluginActivityLauncher {
        /**
         * 启动Actvity
         *
         * @param context 启动context
         * @param intent  插件内传来的Intent.
         * @return <code>true</code>表示该Intent是为了启动插件内Activity的,已经被正确消费了.
         * <code>false</code>表示该Intent不是插件内的Activity.
         */
        boolean startActivity(Context context, Intent intent);

    }

    public void onCreate(Bundle savedInstanceState) {
        //do nothing.
    }

    public void onResume() {

    }

    public void onNewIntent(Intent intent) {
    }

    public void onSaveInstanceState(Bundle outState) {
    }

    public void onPause() {
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

    public ClassLoader getClassLoader() {
        return null;
    }

    public LayoutInflater getLayoutInflater() {
        return null;
    }

    public Resources getResources() {
        return null;
    }

    public void onBackPressed() {

    }
}
