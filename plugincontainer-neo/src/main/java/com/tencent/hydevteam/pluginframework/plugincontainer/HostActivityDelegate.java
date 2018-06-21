package com.tencent.hydevteam.pluginframework.plugincontainer;

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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * HostActivity的被委托者接口
 * <p>
 * 被委托者通过实现这个接口中声明的方法达到替代委托者实现的目的，从而将HostActivity的行为动态化。
 *
 * @author cubershi
 */
public interface HostActivityDelegate {
    void setDelegator(HostActivityDelegator delegator);

    Object getPluginActivity();

    void onCreate(Bundle savedInstanceState);

    void onResume();

    void onNewIntent(Intent intent);

    void onSaveInstanceState(Bundle outState);

    void onPause();

    void onStop();

    void onDestroy();

    void onConfigurationChanged(Configuration newConfig);

    boolean dispatchKeyEvent(KeyEvent event);

    void finish();

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onChildTitleChanged(Activity childActivity, CharSequence title);

    void onRestoreInstanceState(Bundle savedInstanceState);

    void onPostCreate(Bundle savedInstanceState);

    void onRestart();

    void onUserLeaveHint();

    boolean onCreateThumbnail(Bitmap outBitmap, Canvas canvas);

    CharSequence onCreateDescription();

    Object onRetainNonConfigurationInstance();

    void onLowMemory();

    boolean onTrackballEvent(MotionEvent event);

    void onUserInteraction();

    void onWindowAttributesChanged(WindowManager.LayoutParams params);

    void onContentChanged();

    void onWindowFocusChanged(boolean hasFocus);

    View onCreatePanelView(int featureId);

    boolean onCreatePanelMenu(int featureId, Menu menu);

    boolean onPreparePanel(int featureId, View view, Menu menu);

    void onPanelClosed(int featureId, Menu menu);

    Dialog onCreateDialog(int id);

    void onPrepareDialog(int id, Dialog dialog);

    void onApplyThemeResource(Resources.Theme theme, int resid, boolean first);

    View onCreateView(String name, Context context, AttributeSet attrs);

    void startActivityFromChild(Activity child, Intent intent, int requestCode);

    ClassLoader getClassLoader();

    LayoutInflater getLayoutInflater();

    Resources getResources();

    void onBackPressed();

    void onStart();

    void startActivityForResult(Intent intent, int requestCode) ;

    void runOnUiThread(Runnable action) ;
}
