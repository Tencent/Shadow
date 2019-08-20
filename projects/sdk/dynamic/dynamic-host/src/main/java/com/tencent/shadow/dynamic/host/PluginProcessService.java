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

package com.tencent.shadow.dynamic.host;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.tencent.shadow.core.common.InstalledApk;
import com.tencent.shadow.core.common.Logger;
import com.tencent.shadow.core.common.LoggerFactory;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import static com.tencent.shadow.dynamic.host.FailedException.ERROR_CODE_FILE_NOT_FOUND_EXCEPTION;
import static com.tencent.shadow.dynamic.host.FailedException.ERROR_CODE_RELOAD_LOADER_EXCEPTION;
import static com.tencent.shadow.dynamic.host.FailedException.ERROR_CODE_RELOAD_RUNTIME_EXCEPTION;
import static com.tencent.shadow.dynamic.host.FailedException.ERROR_CODE_RESET_UUID_EXCEPTION;
import static com.tencent.shadow.dynamic.host.FailedException.ERROR_CODE_RUNTIME_EXCEPTION;
import static com.tencent.shadow.dynamic.host.FailedException.ERROR_CODE_UUID_MANAGER_DEAD_EXCEPTION;
import static com.tencent.shadow.dynamic.host.FailedException.ERROR_CODE_UUID_MANAGER_NULL_EXCEPTION;


public class PluginProcessService extends Service {
    private static final Logger mLogger = LoggerFactory.getLogger(PluginProcessService.class);

    private final PpsBinder mPpsControllerBinder = new PpsBinder(this);

    static final ActivityHolder sActivityHolder = new ActivityHolder();

    /**
     * PPS应该代表插件进程的生命周期。插件进程应该由PPS启动而启动。
     * 所以不应该出现在同一个插件进程有两个PPS对象的情况。
     * 如果出现，将会重复加载Loader、Runtime、业务等插件，进而出现非常奇怪的异常。
     * 因此，用这样一个静态变量检测出这种情况。PPS不能死后重新创建。需要在上层合理设计保持PPS始终存活。
     */
    private static Object sSingleInstanceFlag = null;

    public static Application.ActivityLifecycleCallbacks getActivityHolder() {
        return sActivityHolder;
    }

    public static PpsController wrapBinder(IBinder ppsBinder) {
        return new PpsController(ppsBinder);
    }

    @Override
    public void onCreate() {
        if (sSingleInstanceFlag == null) {
            sSingleInstanceFlag = new Object();
        } else {
            throw new IllegalStateException("PPS出现多实例");
        }
        super.onCreate();
        if (mLogger.isInfoEnabled()) {
            mLogger.info("onCreate:" + this);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("onBind:" + this);
        }
        return mPpsControllerBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("onUnbind:" + this);
        }
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        if (mLogger.isInfoEnabled()) {
            mLogger.info("onRebind:" + this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLogger.isInfoEnabled()) {
            mLogger.info("onDestroy:" + this);
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        if (mLogger.isInfoEnabled()) {
            mLogger.info("onTaskRemoved:" + this);
        }
    }

    private UuidManager mUuidManager;

    private PluginLoaderImpl mPluginLoader;

    private boolean mRuntimeLoaded = false;

    /**
     * 当前的Uuid。一旦设置不可修改。
     */
    private String mUuid = "";

    private void setUuid(String uuid) throws FailedException {
        if (mUuid.isEmpty()) {
            mUuid = uuid;
        } else if (!mUuid.equals(uuid)) {
            throw new FailedException(ERROR_CODE_RESET_UUID_EXCEPTION, "已设置过uuid==" + mUuid + "试图设置uuid==" + uuid);
        }
    }

    private void checkUuidManagerNotNull() throws FailedException {
        if (mUuidManager == null) {
            throw new FailedException(ERROR_CODE_UUID_MANAGER_NULL_EXCEPTION, "mUuidManager == null");
        }
    }

    void loadRuntime(String uuid) throws FailedException {
        checkUuidManagerNotNull();
        setUuid(uuid);
        if (mRuntimeLoaded) {
            throw new FailedException(ERROR_CODE_RELOAD_RUNTIME_EXCEPTION
                    , "重复调用loadRuntime");
        }
        try {
            if (mLogger.isInfoEnabled()) {
                mLogger.info("loadRuntime uuid:" + uuid);
            }
            InstalledApk installedApk;
            try {
                installedApk = mUuidManager.getRuntime(uuid);
            } catch (RemoteException e) {
                throw new FailedException(ERROR_CODE_UUID_MANAGER_DEAD_EXCEPTION, e.getMessage());
            } catch (NotFoundException e) {
                throw new FailedException(ERROR_CODE_FILE_NOT_FOUND_EXCEPTION, "uuid==" + uuid + "的Runtime没有找到。cause:" + e.getMessage());
            }

            InstalledApk installedRuntimeApk = new InstalledApk(installedApk.apkFilePath, installedApk.oDexPath, installedApk.libraryPath);
            boolean loaded = DynamicRuntime.loadRuntime(installedRuntimeApk);
            if (loaded) {
                DynamicRuntime.saveLastRuntimeInfo(this, installedRuntimeApk);
            }
            mRuntimeLoaded = true;
        } catch (RuntimeException e) {
            if (mLogger.isErrorEnabled()) {
                mLogger.error("loadRuntime发生RuntimeException", e);
            }
            throw new FailedException(e);
        }
    }

    void loadPluginLoader(String uuid) throws FailedException {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("loadPluginLoader uuid:" + uuid + " mPluginLoader:" + mPluginLoader);
        }
        checkUuidManagerNotNull();
        setUuid(uuid);
        if (mPluginLoader != null) {
            throw new FailedException(ERROR_CODE_RELOAD_LOADER_EXCEPTION
                    , "重复调用loadPluginLoader");
        }
        try {
            InstalledApk installedApk;
            try {
                installedApk = mUuidManager.getPluginLoader(uuid);
                if (mLogger.isInfoEnabled()) {
                    mLogger.info("取出uuid==" + uuid + "的Loader apk:" + installedApk.apkFilePath);
                }
            } catch (RemoteException e) {
                if (mLogger.isErrorEnabled()) {
                    mLogger.error("获取Loader Apk失败", e);
                }
                throw new FailedException(ERROR_CODE_UUID_MANAGER_DEAD_EXCEPTION, e.getMessage());
            } catch (NotFoundException e) {
                throw new FailedException(ERROR_CODE_FILE_NOT_FOUND_EXCEPTION, "uuid==" + uuid + "的PluginLoader没有找到。cause:" + e.getMessage());
            }
            File file = new File(installedApk.apkFilePath);
            if (!file.exists()) {
                throw new FailedException(ERROR_CODE_FILE_NOT_FOUND_EXCEPTION, file.getAbsolutePath() + "文件不存在");
            }

            PluginLoaderImpl pluginLoader = new LoaderImplLoader().load(installedApk, uuid, getApplicationContext());
            pluginLoader.setUuidManager(mUuidManager);
            mPluginLoader = pluginLoader;
        } catch (RuntimeException e) {
            if (mLogger.isErrorEnabled()) {
                mLogger.error("loadPluginLoader发生RuntimeException", e);
            }
            throw new FailedException(e);
        } catch (Exception e) {
            if (mLogger.isErrorEnabled()) {
                mLogger.error("loadPluginLoader发生Exception", e);
            }
            throw new FailedException(ERROR_CODE_RUNTIME_EXCEPTION, "加载动态实现失败 cause：" + e.getCause().getMessage());
        }
    }

    void setUuidManager(UuidManager uuidManager) {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("setUuidManager uuidManager==" + uuidManager);
        }
        mUuidManager = uuidManager;
        if (mPluginLoader != null) {
            if (mLogger.isInfoEnabled()) {
                mLogger.info("更新mPluginLoader的uuidManager");
            }
            mPluginLoader.setUuidManager(uuidManager);
        }
    }

    void exit() {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("exit ");
        }
        PluginProcessService.sActivityHolder.finishAll();
        System.exit(0);
        try {
            wait();
        } catch (InterruptedException ignored) {
        }
    }

    PpsStatus getPpsStatus() {
        return new PpsStatus(mUuid, mRuntimeLoaded, mPluginLoader != null, mUuidManager != null);
    }

    IBinder getPluginLoader() {
        return mPluginLoader;
    }

    static class ActivityHolder implements Application.ActivityLifecycleCallbacks {

        private List<Activity> mActivities = new LinkedList<>();

        void finishAll() {
            for (Activity activity : mActivities) {
                activity.finish();
            }
        }

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            mActivities.add(activity);
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            mActivities.remove(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }


    }
}
