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

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.ComponentName;
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

    boolean isChangingConfigurations();

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

    View onCreateView(View parent, String name, Context context, AttributeSet attrs);

    void startActivityFromChild(Activity child, Intent intent, int requestCode);

    ClassLoader getClassLoader();

    LayoutInflater getLayoutInflater();

    Resources getResources();

    void onBackPressed();

    void onStart();

    void onAttachedToWindow();

    void onDetachedFromWindow();

    void onAttachFragment(Fragment fragment);

    void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);

    void recreate();

    ComponentName getCallingActivity();

    void onMultiWindowModeChanged(boolean isInMultiWindowMode);

    void onMultiWindowModeChanged(boolean isInMultiWindowMode, Configuration newConfig);
}
