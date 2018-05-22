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
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * 插件的容器Activity。PluginLoader将把插件的Activity放在其中。
 * PluginContainerActivity以委托模式将Activity的所有回调方法委托给DelegateProviderHolder提供的Delegate。
 *
 * @author cubershi
 */
public class PluginContainerActivity extends Activity implements HostActivity, HostActivityDelegator {
    private static final String TAG = "PluginContainerActivity";

    final HostActivityDelegate hostActivityDelegate;

    public PluginContainerActivity() {
        HostActivityDelegate delegate;
        if (DelegateProviderHolder.delegateProvider != null) {
            delegate = DelegateProviderHolder.delegateProvider.getHostActivityDelegate(this.getClass());
            delegate.setDelegator(this);
        } else {
            Log.e(TAG, "PluginContainerActivity: DelegateProviderHolder没有初始化");
            delegate = null;
        }
        hostActivityDelegate = delegate;
    }

    @Override
    final protected void onCreate(Bundle savedInstanceState) {
        if (hostActivityDelegate != null) {
            hostActivityDelegate.onCreate(savedInstanceState);
        } else {
            //这里是进程被杀后重启后走到，当需要恢复fragment状态的时候，由于系统保留了TAG，会因为找不到fragment引起crash
            super.onCreate(null);
            Log.e(TAG, "onCreate: hostActivityDelegate==null finish activity");
            finish();
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
    final public void superOnCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void superOnResume() {
        super.onResume();
    }

    @Override
    public void superOnSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void superOnPause() {
        super.onPause();
    }

    @Override
    public void superOnStop() {
        super.onStop();
    }

    @Override
    public void superOnDestroy() {
        super.onDestroy();
    }

    @Override
    public void superOnConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean superDispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void superSetResult(Integer resultCode, Intent resultData) {
        super.setResult(resultCode, resultData);
    }

    @Override
    public void superFinish() {
        super.finish();
    }

    @Override
    public void superOnActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void superSetTitle(CharSequence title) {
        super.setTitle(title);
    }

    @Override
    public void superOnRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void superOnPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void superOnRestart() {
        super.onRestart();
    }

    @Override
    public void superOnUserLeaveHint() {
        super.onUserLeaveHint();
    }

    @Override
    public boolean superOnCreateThumbnail(Bitmap outBitmap, Canvas canvas) {
        return super.onCreateThumbnail(outBitmap, canvas);
    }

    @Override
    public CharSequence superOnCreateDescription() {
        return super.onCreateDescription();
    }

    @Override
    public Object superOnRetainNonConfigurationInstance() {
        return super.onRetainNonConfigurationInstance();
    }

    @Override
    public void superOnLowMemory() {
        super.onLowMemory();
    }

    @Override
    public boolean superOnTrackballEvent(MotionEvent event) {
        return super.onTrackballEvent(event);
    }

    @Override
    public void superOnUserInteraction() {
        super.onUserInteraction();
    }

    @Override
    public void superOnWindowAttributesChanged(WindowManager.LayoutParams params) {
        super.onWindowAttributesChanged(params);
    }

    @Override
    public void superOnContentChanged() {
        super.onContentChanged();
    }

    @Override
    public void superOnWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public View superOnCreatePanelView(int featureId) {
        return super.onCreatePanelView(featureId);
    }

    @Override
    public boolean superOnCreatePanelMenu(int featureId, Menu menu) {
        return super.onCreatePanelMenu(featureId, menu);
    }

    @Override
    public boolean superOnPreparePanel(int featureId, View view, Menu menu) {
        return super.onPreparePanel(featureId, view, menu);
    }

    @Override
    public void superOnPanelClosed(int featureId, Menu menu) {
        super.onPanelClosed(featureId, menu);
    }

    @Override
    public Dialog superOnCreateDialog(int id) {
        return super.onCreateDialog(id);
    }

    @Override
    public void superOnPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
    }

    @Override
    public void superOnApplyThemeResource(Resources.Theme theme, int resid, boolean first) {
        super.onApplyThemeResource(theme, resid, first);
    }

    @Override
    public View superOnCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    @Override
    public void superStartActivityFromChild(Activity child, Intent newIntent, int requestCode) {
        super.startActivityFromChild(child, newIntent, requestCode);
    }

    @Override
    public ClassLoader superGetClassLoader() {
        return super.getClassLoader();
    }

    @Override
    public Intent superGetIntent() {
        return super.getIntent();
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
}
