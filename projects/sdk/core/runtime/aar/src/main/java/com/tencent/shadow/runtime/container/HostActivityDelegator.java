package com.tencent.shadow.runtime.container;

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
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.os.UserHandle;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.util.AttributeSet;
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

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;

/**
 * HostActivity作为委托者的接口。主要提供它的委托方法的super方法，
 * 以便Delegate可以通过这个接口调用到Activity的super方法。
 * <p>
 * cubershi
 */
public interface HostActivityDelegator {
    HostActivity getHostActivity();

    Application getApplication();

    boolean isChild();

    Activity getParent();

    void requestShowKeyboardShortcuts();

    void dismissKeyboardShortcutsHelper();

    void setDefaultKeyMode(int mode);

    void showDialog(int id);

    boolean showDialog(int id, Bundle args);

    void dismissDialog(int id);

    void removeDialog(int id);

    SearchEvent getSearchEvent();

    boolean requestWindowFeature(int featureId);

    void setFeatureDrawableResource(int featureId, int resId);

    void setFeatureDrawableUri(int featureId, Uri uri);

    void setFeatureDrawable(int featureId, Drawable drawable);

    void setFeatureDrawableAlpha(int featureId, int alpha);

    void requestPermissions(String[] permissions, int requestCode);

    void setResult(int resultCode);

    void setResult(int resultCode, Intent data);

    CharSequence getTitle();

    int getTitleColor();

    void setProgressBarVisibility(boolean visible);

    void setProgressBarIndeterminateVisibility(boolean visible);

    void setProgressBarIndeterminate(boolean indeterminate);

    void setProgress(int progress);

    void setSecondaryProgress(int secondaryProgress);

    void setVolumeControlStream(int streamType);

    int getVolumeControlStream();

    void setMediaController(MediaController controller);

    MediaController getMediaController();

    void runOnUiThread(Runnable action);

    Intent getIntent();

    void setIntent(Intent newIntent);

    WindowManager getWindowManager();

    Window getWindow();

    LoaderManager getLoaderManager();

    View getCurrentFocus();

    boolean isVoiceInteraction();

    boolean isVoiceInteractionRoot();

    VoiceInteractor getVoiceInteractor();

    boolean isLocalVoiceInteractionSupported();

    void startLocalVoiceInteraction(Bundle privateOptions);

    void stopLocalVoiceInteraction();

    boolean showAssist(Bundle args);

    void reportFullyDrawn();

    boolean isInMultiWindowMode();

    boolean isInPictureInPictureMode();

    void enterPictureInPictureMode();

    boolean enterPictureInPictureMode(PictureInPictureParams params);

    void setPictureInPictureParams(PictureInPictureParams params);

    int getMaxNumPictureInPictureActions();

    int getChangingConfigurations();

    Object getLastNonConfigurationInstance();

    FragmentManager getFragmentManager();

    void startManagingCursor(Cursor c);

    void stopManagingCursor(Cursor c);

    <T extends View> T findViewById(int id);

    ActionBar getActionBar();

    void setActionBar(Toolbar toolbar);

    void setContentView(int layoutResID);

    void setContentView(View view);

    void setContentView(View view, ViewGroup.LayoutParams params);

    void addContentView(View view, ViewGroup.LayoutParams params);

    TransitionManager getContentTransitionManager();

    void setContentTransitionManager(TransitionManager tm);

    Scene getContentScene();

    void setFinishOnTouchOutside(boolean finish);

    boolean hasWindowFocus();

    boolean dispatchKeyEvent(KeyEvent event);

    boolean dispatchKeyShortcutEvent(KeyEvent event);

    boolean dispatchTouchEvent(MotionEvent ev);

    boolean dispatchTrackballEvent(MotionEvent ev);

    boolean dispatchGenericMotionEvent(MotionEvent ev);

    boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event);

    void invalidateOptionsMenu();

    void openOptionsMenu();

    void closeOptionsMenu();

    void registerForContextMenu(View view);

    void unregisterForContextMenu(View view);

    void openContextMenu(View view);

    void closeContextMenu();

    void startSearch(String initialQuery, boolean selectInitialQuery, Bundle appSearchData, boolean globalSearch);

    void triggerSearch(String query, Bundle appSearchData);

    void takeKeyEvents(boolean get);

    LayoutInflater getLayoutInflater();

    MenuInflater getMenuInflater();

    void setTheme(int resid);

    boolean shouldShowRequestPermissionRationale(String permission);

    void startActivityForResult(Intent intent, int requestCode);

    void startActivityForResult(Intent intent, int requestCode, Bundle options);

    boolean isActivityTransitionRunning();

    void startIntentSenderForResult(IntentSender intent, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags) throws IntentSender.SendIntentException;

    void startIntentSenderForResult(IntentSender intent, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, Bundle options) throws IntentSender.SendIntentException;

    void startActivity(Intent intent);

    void startActivity(Intent intent, Bundle options);

    void startActivities(Intent[] intents);

    void startActivities(Intent[] intents, Bundle options);

    void startIntentSender(IntentSender intent, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags) throws IntentSender.SendIntentException;

    void startIntentSender(IntentSender intent, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, Bundle options) throws IntentSender.SendIntentException;

    boolean startActivityIfNeeded(Intent intent, int requestCode);

    boolean startActivityIfNeeded(Intent intent, int requestCode, Bundle options);

    boolean startNextMatchingActivity(Intent intent);

    boolean startNextMatchingActivity(Intent intent, Bundle options);

    void startActivityFromChild(Activity child, Intent intent, int requestCode);

    void startActivityFromChild(Activity child, Intent intent, int requestCode, Bundle options);

    void startActivityFromFragment(Fragment fragment, Intent intent, int requestCode);

    void startActivityFromFragment(Fragment fragment, Intent intent, int requestCode, Bundle options);

    void startIntentSenderFromChild(Activity child, IntentSender intent, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags) throws IntentSender.SendIntentException;

    void startIntentSenderFromChild(Activity child, IntentSender intent, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, Bundle options) throws IntentSender.SendIntentException;

    void overridePendingTransition(int enterAnim, int exitAnim);

    Uri getReferrer();

    String getCallingPackage();

    ComponentName getCallingActivity();

    void setVisible(boolean visible);

    boolean isFinishing();

    boolean isDestroyed();

    boolean isChangingConfigurations();

    void recreate();

    void finish();

    void finishAffinity();

    void finishFromChild(Activity child);

    void finishAfterTransition();

    void finishActivity(int requestCode);

    void finishActivityFromChild(Activity child, int requestCode);

    void finishAndRemoveTask();

    boolean releaseInstance();

    PendingIntent createPendingResult(int requestCode, Intent data, int flags);

    void setRequestedOrientation(int requestedOrientation);

    int getRequestedOrientation();

    int getTaskId();

    boolean isTaskRoot();

    boolean moveTaskToBack(boolean nonRoot);

    String getLocalClassName();

    ComponentName getComponentName();

    SharedPreferences getPreferences(int mode);

    Object getSystemService(String name);

    void setTitle(CharSequence title);

    void setTitle(int titleId);

    void setTitleColor(int textColor);

    void setTaskDescription(ActivityManager.TaskDescription taskDescription);

    void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args);

    boolean isImmersive();

    boolean requestVisibleBehind(boolean visible);

    void setImmersive(boolean i);

    void setVrModeEnabled(boolean enabled, ComponentName requestedComponent) throws PackageManager.NameNotFoundException;

    ActionMode startActionMode(ActionMode.Callback callback);

    ActionMode startActionMode(ActionMode.Callback callback, int type);

    boolean shouldUpRecreateTask(Intent targetIntent);

    boolean navigateUpTo(Intent upIntent);

    boolean navigateUpToFromChild(Activity child, Intent upIntent);

    Intent getParentActivityIntent();

    void setEnterSharedElementCallback(SharedElementCallback callback);

    void setExitSharedElementCallback(SharedElementCallback callback);

    void postponeEnterTransition();

    void startPostponedEnterTransition();

    DragAndDropPermissions requestDragAndDropPermissions(DragEvent event);

    void startLockTask();

    void stopLockTask();

    void showLockTaskEscapeMessage();

    void setShowWhenLocked(boolean showWhenLocked);

    void setTurnScreenOn(boolean turnScreenOn);

    void applyOverrideConfiguration(Configuration overrideConfiguration);

    AssetManager getAssets();

    Resources getResources();

    Resources.Theme getTheme();

    Context getBaseContext();

    PackageManager getPackageManager();

    ContentResolver getContentResolver();

    Looper getMainLooper();

    Context getApplicationContext();

    ClassLoader getClassLoader();

    String getPackageName();

    ApplicationInfo getApplicationInfo();

    String getPackageResourcePath();

    String getPackageCodePath();

    SharedPreferences getSharedPreferences(String name, int mode);

    boolean moveSharedPreferencesFrom(Context sourceContext, String name);

    boolean deleteSharedPreferences(String name);

    FileInputStream openFileInput(String name) throws FileNotFoundException;

    FileOutputStream openFileOutput(String name, int mode) throws FileNotFoundException;

    boolean deleteFile(String name);

    File getFileStreamPath(String name);

    String[] fileList();

    File getDataDir();

    File getFilesDir();

    File getNoBackupFilesDir();

    File getExternalFilesDir(String type);

    File[] getExternalFilesDirs(String type);

    File getObbDir();

    File[] getObbDirs();

    File getCacheDir();

    File getCodeCacheDir();

    File getExternalCacheDir();

    File[] getExternalCacheDirs();

    File[] getExternalMediaDirs();

    File getDir(String name, int mode);

    SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory);

    SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler);

    boolean moveDatabaseFrom(Context sourceContext, String name);

    boolean deleteDatabase(String name);

    File getDatabasePath(String name);

    String[] databaseList();

    Drawable getWallpaper();

    Drawable peekWallpaper();

    int getWallpaperDesiredMinimumWidth();

    int getWallpaperDesiredMinimumHeight();

    void setWallpaper(Bitmap bitmap) throws IOException;

    void setWallpaper(InputStream data) throws IOException;

    void clearWallpaper() throws IOException;

    void sendBroadcast(Intent intent);

    void sendBroadcast(Intent intent, String receiverPermission);

    void sendOrderedBroadcast(Intent intent, String receiverPermission);

    void sendOrderedBroadcast(Intent intent, String receiverPermission, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras);

    void sendBroadcastAsUser(Intent intent, UserHandle user);

    void sendBroadcastAsUser(Intent intent, UserHandle user, String receiverPermission);

    void sendOrderedBroadcastAsUser(Intent intent, UserHandle user, String receiverPermission, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras);

    void sendStickyBroadcast(Intent intent);

    void sendStickyOrderedBroadcast(Intent intent, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras);

    void removeStickyBroadcast(Intent intent);

    void sendStickyBroadcastAsUser(Intent intent, UserHandle user);

    void sendStickyOrderedBroadcastAsUser(Intent intent, UserHandle user, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras);

    void removeStickyBroadcastAsUser(Intent intent, UserHandle user);

    Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter);

    Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, int flags);

    Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, String broadcastPermission, Handler scheduler);

    Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, String broadcastPermission, Handler scheduler, int flags);

    void unregisterReceiver(BroadcastReceiver receiver);

    ComponentName startService(Intent service);

    ComponentName startForegroundService(Intent service);

    boolean stopService(Intent name);

    boolean bindService(Intent service, ServiceConnection conn, int flags);

    void unbindService(ServiceConnection conn);

    boolean startInstrumentation(ComponentName className, String profileFile, Bundle arguments);

    String getSystemServiceName(Class<?> serviceClass);

    int checkPermission(String permission, int pid, int uid);

    int checkCallingPermission(String permission);

    int checkCallingOrSelfPermission(String permission);

    int checkSelfPermission(String permission);

    void enforcePermission(String permission, int pid, int uid, String message);

    void enforceCallingPermission(String permission, String message);

    void enforceCallingOrSelfPermission(String permission, String message);

    void grantUriPermission(String toPackage, Uri uri, int modeFlags);

    void revokeUriPermission(Uri uri, int modeFlags);

    void revokeUriPermission(String targetPackage, Uri uri, int modeFlags);

    int checkUriPermission(Uri uri, int pid, int uid, int modeFlags);

    int checkCallingUriPermission(Uri uri, int modeFlags);

    int checkCallingOrSelfUriPermission(Uri uri, int modeFlags);

    int checkUriPermission(Uri uri, String readPermission, String writePermission, int pid, int uid, int modeFlags);

    void enforceUriPermission(Uri uri, int pid, int uid, int modeFlags, String message);

    void enforceCallingUriPermission(Uri uri, int modeFlags, String message);

    void enforceCallingOrSelfUriPermission(Uri uri, int modeFlags, String message);

    void enforceUriPermission(Uri uri, String readPermission, String writePermission, int pid, int uid, int modeFlags, String message);

    Context createPackageContext(String packageName, int flags) throws PackageManager.NameNotFoundException;

    Context createConfigurationContext(Configuration overrideConfiguration);

    Context createDisplayContext(Display display);

    boolean isRestricted();

    Context createDeviceProtectedStorageContext();

    boolean isDeviceProtectedStorage();

    Context createContextForSplit(String splitName) throws PackageManager.NameNotFoundException;

    void registerComponentCallbacks(ComponentCallbacks callback);

    void unregisterComponentCallbacks(ComponentCallbacks callback);

    Cursor managedQuery(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder);

    Application superGetApplication();

    boolean superIsChild();

    Activity superGetParent();

    void superRequestShowKeyboardShortcuts();

    void superDismissKeyboardShortcutsHelper();

    void superSetDefaultKeyMode(int mode);

    void superShowDialog(int id);

    boolean superShowDialog(int id, Bundle args);

    void superDismissDialog(int id);

    void superRemoveDialog(int id);

    SearchEvent superGetSearchEvent();

    boolean superRequestWindowFeature(int featureId);

    void superSetFeatureDrawableResource(int featureId, int resId);

    void superSetFeatureDrawableUri(int featureId, Uri uri);

    void superSetFeatureDrawable(int featureId, Drawable drawable);

    void superSetFeatureDrawableAlpha(int featureId, int alpha);

    void superRequestPermissions(String[] permissions, int requestCode);

    void superSetResult(int resultCode);

    void superSetResult(int resultCode, Intent data);

    CharSequence superGetTitle();

    int superGetTitleColor();

    void superSetProgressBarVisibility(boolean visible);

    void superSetProgressBarIndeterminateVisibility(boolean visible);

    void superSetProgressBarIndeterminate(boolean indeterminate);

    void superSetProgress(int progress);

    void superSetSecondaryProgress(int secondaryProgress);

    void superSetVolumeControlStream(int streamType);

    int superGetVolumeControlStream();

    void superSetMediaController(MediaController controller);

    MediaController superGetMediaController();

    void superRunOnUiThread(Runnable action);

    Intent superGetIntent();

    void superSetIntent(Intent newIntent);

    WindowManager superGetWindowManager();

    Window superGetWindow();

    LoaderManager superGetLoaderManager();

    View superGetCurrentFocus();

    boolean superIsVoiceInteraction();

    boolean superIsVoiceInteractionRoot();

    VoiceInteractor superGetVoiceInteractor();

    boolean superIsLocalVoiceInteractionSupported();

    void superStartLocalVoiceInteraction(Bundle privateOptions);

    void superStopLocalVoiceInteraction();

    boolean superShowAssist(Bundle args);

    void superReportFullyDrawn();

    boolean superIsInMultiWindowMode();

    boolean superIsInPictureInPictureMode();

    void superEnterPictureInPictureMode();

    boolean superEnterPictureInPictureMode(PictureInPictureParams params);

    void superSetPictureInPictureParams(PictureInPictureParams params);

    int superGetMaxNumPictureInPictureActions();

    int superGetChangingConfigurations();

    Object superGetLastNonConfigurationInstance();

    FragmentManager superGetFragmentManager();

    void superStartManagingCursor(Cursor c);

    void superStopManagingCursor(Cursor c);

    <T extends View> T superFindViewById(int id);

    ActionBar superGetActionBar();

    void superSetActionBar(Toolbar toolbar);

    void superSetContentView(int layoutResID);

    void superSetContentView(View view);

    void superSetContentView(View view, ViewGroup.LayoutParams params);

    void superAddContentView(View view, ViewGroup.LayoutParams params);

    TransitionManager superGetContentTransitionManager();

    void superSetContentTransitionManager(TransitionManager tm);

    Scene superGetContentScene();

    void superSetFinishOnTouchOutside(boolean finish);

    boolean superHasWindowFocus();

    boolean superDispatchKeyEvent(KeyEvent event);

    boolean superDispatchKeyShortcutEvent(KeyEvent event);

    boolean superDispatchTouchEvent(MotionEvent ev);

    boolean superDispatchTrackballEvent(MotionEvent ev);

    boolean superDispatchGenericMotionEvent(MotionEvent ev);

    boolean superDispatchPopulateAccessibilityEvent(AccessibilityEvent event);

    void superInvalidateOptionsMenu();

    void superOpenOptionsMenu();

    void superCloseOptionsMenu();

    void superRegisterForContextMenu(View view);

    void superUnregisterForContextMenu(View view);

    void superOpenContextMenu(View view);

    void superCloseContextMenu();

    void superStartSearch(String initialQuery, boolean selectInitialQuery, Bundle appSearchData, boolean globalSearch);

    void superTriggerSearch(String query, Bundle appSearchData);

    void superTakeKeyEvents(boolean get);

    LayoutInflater superGetLayoutInflater();

    MenuInflater superGetMenuInflater();

    void superSetTheme(int resid);

    boolean superShouldShowRequestPermissionRationale(String permission);

    void superStartActivityForResult(Intent intent, int requestCode);

    void superStartActivityForResult(Intent intent, int requestCode, Bundle options);

    boolean superIsActivityTransitionRunning();

    void superStartIntentSenderForResult(IntentSender intent, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags) throws IntentSender.SendIntentException;

    void superStartIntentSenderForResult(IntentSender intent, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, Bundle options) throws IntentSender.SendIntentException;

    void superStartActivity(Intent intent);

    void superStartActivity(Intent intent, Bundle options);

    void superStartActivities(Intent[] intents);

    void superStartActivities(Intent[] intents, Bundle options);

    void superStartIntentSender(IntentSender intent, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags) throws IntentSender.SendIntentException;

    void superStartIntentSender(IntentSender intent, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, Bundle options) throws IntentSender.SendIntentException;

    boolean superStartActivityIfNeeded(Intent intent, int requestCode);

    boolean superStartActivityIfNeeded(Intent intent, int requestCode, Bundle options);

    boolean superStartNextMatchingActivity(Intent intent);

    boolean superStartNextMatchingActivity(Intent intent, Bundle options);

    void superStartActivityFromChild(Activity child, Intent intent, int requestCode);

    void superStartActivityFromChild(Activity child, Intent intent, int requestCode, Bundle options);

    void superStartActivityFromFragment(Fragment fragment, Intent intent, int requestCode);

    void superStartActivityFromFragment(Fragment fragment, Intent intent, int requestCode, Bundle options);

    void superStartIntentSenderFromChild(Activity child, IntentSender intent, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags) throws IntentSender.SendIntentException;

    void superStartIntentSenderFromChild(Activity child, IntentSender intent, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, Bundle options) throws IntentSender.SendIntentException;

    void superOverridePendingTransition(int enterAnim, int exitAnim);

    Uri superGetReferrer();

    String superGetCallingPackage();

    ComponentName superGetCallingActivity();

    void superSetVisible(boolean visible);

    boolean superIsFinishing();

    boolean superIsDestroyed();

    boolean superIsChangingConfigurations();

    void superRecreate();

    void superFinish();

    void superFinishAffinity();

    void superFinishFromChild(Activity child);

    void superFinishAfterTransition();

    void superFinishActivity(int requestCode);

    void superFinishActivityFromChild(Activity child, int requestCode);

    void superFinishAndRemoveTask();

    boolean superReleaseInstance();

    PendingIntent superCreatePendingResult(int requestCode, Intent data, int flags);

    void superSetRequestedOrientation(int requestedOrientation);

    int superGetRequestedOrientation();

    int superGetTaskId();

    boolean superIsTaskRoot();

    boolean superMoveTaskToBack(boolean nonRoot);

    String superGetLocalClassName();

    ComponentName superGetComponentName();

    SharedPreferences superGetPreferences(int mode);

    Object superGetSystemService(String name);

    void superSetTitle(CharSequence title);

    void superSetTitle(int titleId);

    void superSetTitleColor(int textColor);

    void superSetTaskDescription(ActivityManager.TaskDescription taskDescription);

    void superDump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args);

    boolean superIsImmersive();

    boolean superRequestVisibleBehind(boolean visible);

    void superSetImmersive(boolean i);

    void superSetVrModeEnabled(boolean enabled, ComponentName requestedComponent) throws PackageManager.NameNotFoundException;

    ActionMode superStartActionMode(ActionMode.Callback callback);

    ActionMode superStartActionMode(ActionMode.Callback callback, int type);

    boolean superShouldUpRecreateTask(Intent targetIntent);

    boolean superNavigateUpTo(Intent upIntent);

    boolean superNavigateUpToFromChild(Activity child, Intent upIntent);

    Intent superGetParentActivityIntent();

    void superSetEnterSharedElementCallback(SharedElementCallback callback);

    void superSetExitSharedElementCallback(SharedElementCallback callback);

    void superPostponeEnterTransition();

    void superStartPostponedEnterTransition();

    DragAndDropPermissions superRequestDragAndDropPermissions(DragEvent event);

    void superStartLockTask();

    void superStopLockTask();

    void superShowLockTaskEscapeMessage();

    void superSetShowWhenLocked(boolean showWhenLocked);

    void superSetTurnScreenOn(boolean turnScreenOn);

    void superApplyOverrideConfiguration(Configuration overrideConfiguration);

    AssetManager superGetAssets();

    Resources superGetResources();

    Resources.Theme superGetTheme();

    Context superGetBaseContext();

    PackageManager superGetPackageManager();

    ContentResolver superGetContentResolver();

    Looper superGetMainLooper();

    Context superGetApplicationContext();

    ClassLoader superGetClassLoader();

    String superGetPackageName();

    ApplicationInfo superGetApplicationInfo();

    String superGetPackageResourcePath();

    String superGetPackageCodePath();

    SharedPreferences superGetSharedPreferences(String name, int mode);

    boolean superMoveSharedPreferencesFrom(Context sourceContext, String name);

    boolean superDeleteSharedPreferences(String name);

    FileInputStream superOpenFileInput(String name) throws FileNotFoundException;

    FileOutputStream superOpenFileOutput(String name, int mode) throws FileNotFoundException;

    boolean superDeleteFile(String name);

    File superGetFileStreamPath(String name);

    String[] superFileList();

    File superGetDataDir();

    File superGetFilesDir();

    File superGetNoBackupFilesDir();

    File superGetExternalFilesDir(String type);

    File[] superGetExternalFilesDirs(String type);

    File superGetObbDir();

    File[] superGetObbDirs();

    File superGetCacheDir();

    File superGetCodeCacheDir();

    File superGetExternalCacheDir();

    File[] superGetExternalCacheDirs();

    File[] superGetExternalMediaDirs();

    File superGetDir(String name, int mode);

    SQLiteDatabase superOpenOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory);

    SQLiteDatabase superOpenOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler);

    boolean superMoveDatabaseFrom(Context sourceContext, String name);

    boolean superDeleteDatabase(String name);

    File superGetDatabasePath(String name);

    String[] superDatabaseList();

    Drawable superGetWallpaper();

    Drawable superPeekWallpaper();

    int superGetWallpaperDesiredMinimumWidth();

    int superGetWallpaperDesiredMinimumHeight();

    void superSetWallpaper(Bitmap bitmap) throws IOException;

    void superSetWallpaper(InputStream data) throws IOException;

    void superClearWallpaper() throws IOException;

    void superSendBroadcast(Intent intent);

    void superSendBroadcast(Intent intent, String receiverPermission);

    void superSendOrderedBroadcast(Intent intent, String receiverPermission);

    void superSendOrderedBroadcast(Intent intent, String receiverPermission, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras);

    void superSendBroadcastAsUser(Intent intent, UserHandle user);

    void superSendBroadcastAsUser(Intent intent, UserHandle user, String receiverPermission);

    void superSendOrderedBroadcastAsUser(Intent intent, UserHandle user, String receiverPermission, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras);

    void superSendStickyBroadcast(Intent intent);

    void superSendStickyOrderedBroadcast(Intent intent, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras);

    void superRemoveStickyBroadcast(Intent intent);

    void superSendStickyBroadcastAsUser(Intent intent, UserHandle user);

    void superSendStickyOrderedBroadcastAsUser(Intent intent, UserHandle user, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras);

    void superRemoveStickyBroadcastAsUser(Intent intent, UserHandle user);

    Intent superRegisterReceiver(BroadcastReceiver receiver, IntentFilter filter);

    Intent superRegisterReceiver(BroadcastReceiver receiver, IntentFilter filter, int flags);

    Intent superRegisterReceiver(BroadcastReceiver receiver, IntentFilter filter, String broadcastPermission, Handler scheduler);

    Intent superRegisterReceiver(BroadcastReceiver receiver, IntentFilter filter, String broadcastPermission, Handler scheduler, int flags);

    void superUnregisterReceiver(BroadcastReceiver receiver);

    ComponentName superStartService(Intent service);

    ComponentName superStartForegroundService(Intent service);

    boolean superStopService(Intent name);

    boolean superBindService(Intent service, ServiceConnection conn, int flags);

    void superUnbindService(ServiceConnection conn);

    boolean superStartInstrumentation(ComponentName className, String profileFile, Bundle arguments);

    String superGetSystemServiceName(Class<?> serviceClass);

    int superCheckPermission(String permission, int pid, int uid);

    int superCheckCallingPermission(String permission);

    int superCheckCallingOrSelfPermission(String permission);

    int superCheckSelfPermission(String permission);

    void superEnforcePermission(String permission, int pid, int uid, String message);

    void superEnforceCallingPermission(String permission, String message);

    void superEnforceCallingOrSelfPermission(String permission, String message);

    void superGrantUriPermission(String toPackage, Uri uri, int modeFlags);

    void superRevokeUriPermission(Uri uri, int modeFlags);

    void superRevokeUriPermission(String targetPackage, Uri uri, int modeFlags);

    int superCheckUriPermission(Uri uri, int pid, int uid, int modeFlags);

    int superCheckCallingUriPermission(Uri uri, int modeFlags);

    int superCheckCallingOrSelfUriPermission(Uri uri, int modeFlags);

    int superCheckUriPermission(Uri uri, String readPermission, String writePermission, int pid, int uid, int modeFlags);

    void superEnforceUriPermission(Uri uri, int pid, int uid, int modeFlags, String message);

    void superEnforceCallingUriPermission(Uri uri, int modeFlags, String message);

    void superEnforceCallingOrSelfUriPermission(Uri uri, int modeFlags, String message);

    void superEnforceUriPermission(Uri uri, String readPermission, String writePermission, int pid, int uid, int modeFlags, String message);

    Context superCreatePackageContext(String packageName, int flags) throws PackageManager.NameNotFoundException;

    Context superCreateConfigurationContext(Configuration overrideConfiguration);

    Context superCreateDisplayContext(Display display);

    boolean superIsRestricted();

    Context superCreateDeviceProtectedStorageContext();

    boolean superIsDeviceProtectedStorage();

    Context superCreateContextForSplit(String splitName) throws PackageManager.NameNotFoundException;

    void superRegisterComponentCallbacks(ComponentCallbacks callback);

    void superUnregisterComponentCallbacks(ComponentCallbacks callback);

    void superAttachBaseContext(Context newBase);

    void superOnCreate(Bundle savedInstanceState);

    void superOnCreate(Bundle savedInstanceState, PersistableBundle persistentState);

    void superOnRestoreInstanceState(Bundle savedInstanceState);

    void superOnRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState);

    void superOnPostCreate(Bundle savedInstanceState);

    void superOnPostCreate(Bundle savedInstanceState, PersistableBundle persistentState);

    void superOnStart();

    void superOnRestart();

    void superOnStateNotSaved();

    void superOnResume();

    void superOnPostResume();

    void superOnLocalVoiceInteractionStarted();

    void superOnLocalVoiceInteractionStopped();

    void superOnNewIntent(Intent intent);

    void superOnSaveInstanceState(Bundle outState);

    void superOnSaveInstanceState(Bundle outState, PersistableBundle outPersistentState);

    void superOnPause();

    void superOnUserLeaveHint();

    boolean superOnCreateThumbnail(Bitmap outBitmap, Canvas canvas);

    CharSequence superOnCreateDescription();

    void superOnProvideAssistData(Bundle data);

    void superOnProvideAssistContent(AssistContent outContent);

    void superOnProvideKeyboardShortcuts(List<KeyboardShortcutGroup> data, Menu menu, int deviceId);

    void superOnStop();

    void superOnDestroy();

    void superOnMultiWindowModeChanged(boolean isInMultiWindowMode, Configuration newConfig);

    void superOnMultiWindowModeChanged(boolean isInMultiWindowMode);

    void superOnPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig);

    void superOnPictureInPictureModeChanged(boolean isInPictureInPictureMode);

    void superOnConfigurationChanged(Configuration newConfig);

    Object superOnRetainNonConfigurationInstance();

    void superOnLowMemory();

    void superOnTrimMemory(int level);

    void superOnAttachFragment(Fragment fragment);

    boolean superOnKeyDown(int keyCode, KeyEvent event);

    boolean superOnKeyLongPress(int keyCode, KeyEvent event);

    boolean superOnKeyUp(int keyCode, KeyEvent event);

    boolean superOnKeyMultiple(int keyCode, int repeatCount, KeyEvent event);

    void superOnBackPressed();

    boolean superOnKeyShortcut(int keyCode, KeyEvent event);

    boolean superOnTouchEvent(MotionEvent event);

    boolean superOnTrackballEvent(MotionEvent event);

    boolean superOnGenericMotionEvent(MotionEvent event);

    void superOnUserInteraction();

    void superOnWindowAttributesChanged(WindowManager.LayoutParams params);

    void superOnContentChanged();

    void superOnWindowFocusChanged(boolean hasFocus);

    void superOnAttachedToWindow();

    void superOnDetachedFromWindow();

    View superOnCreatePanelView(int featureId);

    boolean superOnCreatePanelMenu(int featureId, Menu menu);

    boolean superOnPreparePanel(int featureId, View view, Menu menu);

    boolean superOnMenuOpened(int featureId, Menu menu);

    boolean superOnMenuItemSelected(int featureId, MenuItem item);

    void superOnPanelClosed(int featureId, Menu menu);

    boolean superOnCreateOptionsMenu(Menu menu);

    boolean superOnPrepareOptionsMenu(Menu menu);

    boolean superOnOptionsItemSelected(MenuItem item);

    boolean superOnNavigateUp();

    boolean superOnNavigateUpFromChild(Activity child);

    void superOnCreateNavigateUpTaskStack(TaskStackBuilder builder);

    void superOnPrepareNavigateUpTaskStack(TaskStackBuilder builder);

    void superOnOptionsMenuClosed(Menu menu);

    void superOnCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo);

    boolean superOnContextItemSelected(MenuItem item);

    void superOnContextMenuClosed(Menu menu);

    Dialog superOnCreateDialog(int id);

    Dialog superOnCreateDialog(int id, Bundle args);

    void superOnPrepareDialog(int id, Dialog dialog);

    void superOnPrepareDialog(int id, Dialog dialog, Bundle args);

    boolean superOnSearchRequested(SearchEvent searchEvent);

    boolean superOnSearchRequested();

    void superOnApplyThemeResource(Resources.Theme theme, int resid, boolean first);

    void superOnRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);

    Uri superOnProvideReferrer();

    void superOnActivityResult(int requestCode, int resultCode, Intent data);

    void superOnActivityReenter(int resultCode, Intent data);

    void superOnTitleChanged(CharSequence title, int color);

    void superOnChildTitleChanged(Activity childActivity, CharSequence title);

    View superOnCreateView(String name, Context context, AttributeSet attrs);

    View superOnCreateView(View parent, String name, Context context, AttributeSet attrs);

    void superOnVisibleBehindCanceled();

    void superOnEnterAnimationComplete();

    ActionMode superOnWindowStartingActionMode(ActionMode.Callback callback);

    ActionMode superOnWindowStartingActionMode(ActionMode.Callback callback, int type);

    void superOnActionModeStarted(ActionMode mode);

    void superOnActionModeFinished(ActionMode mode);

    void superOnPointerCaptureChanged(boolean hasCapture);
}
