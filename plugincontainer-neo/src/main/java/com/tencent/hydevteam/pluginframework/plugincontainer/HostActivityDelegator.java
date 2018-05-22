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
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * HostActivity作为委托者的接口。主要提供它的委托方法的super方法，
 * 以便Delegate可以通过这个接口调用到Activity的super方法。
 *
 * @author cubershi
 */
public interface HostActivityDelegator {
    void superOnCreate(Bundle savedInstanceState);

    void superOnResume();

    void superOnSaveInstanceState(Bundle outState);

    void superOnPause();

    void superOnStop();

    void superOnDestroy();

    void superOnConfigurationChanged(Configuration newConfig);

    boolean superDispatchKeyEvent(KeyEvent event);

    void superSetResult(Integer resultCode, Intent resultData);

    void superFinish();

    void superOnActivityResult(int requestCode, int resultCode, Intent data);

    void superSetTitle(CharSequence title);

    void superOnRestoreInstanceState(Bundle savedInstanceState);

    void superOnPostCreate(Bundle savedInstanceState);

    void superOnRestart();

    void superOnUserLeaveHint();

    boolean superOnCreateThumbnail(Bitmap outBitmap, Canvas canvas);

    CharSequence superOnCreateDescription();

    Object superOnRetainNonConfigurationInstance();

    void superOnLowMemory();

    boolean superOnTrackballEvent(MotionEvent event);

    void superOnUserInteraction();

    void superOnWindowAttributesChanged(WindowManager.LayoutParams params);

    void superOnContentChanged();

    void superOnWindowFocusChanged(boolean hasFocus);

    View superOnCreatePanelView(int featureId);

    boolean superOnCreatePanelMenu(int featureId, Menu menu);

    boolean superOnPreparePanel(int featureId, View view, Menu menu);

    void superOnPanelClosed(int featureId, Menu menu);

    Dialog superOnCreateDialog(int id);

    void superOnPrepareDialog(int id, Dialog dialog);

    void superOnApplyThemeResource(Resources.Theme theme, int resid, boolean first);

    View superOnCreateView(String name, Context context, AttributeSet attrs);

    void superStartActivityFromChild(Activity child, Intent newIntent, int requestCode);

    ClassLoader superGetClassLoader();

    Intent superGetIntent();

    Intent getIntent();

    HostActivity getHostActivity();

    void overridePendingTransition(int enterAnim, int exitAnim);

    void startActivity(Intent intent);

    void setRequestedOrientation(int screenOrientation);

    void setContentView(View decorView);

    void setTheme(int theme_translucent_noTitleBar);

    Context getApplicationContext();

    void runOnUiThread(Runnable runnable);

    boolean requestWindowFeature(int featureId);
}
