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

package com.tencent.shadow.dynamic.manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.TransactionTooLargeException;

import com.tencent.shadow.core.common.InstalledApk;
import com.tencent.shadow.core.common.Logger;
import com.tencent.shadow.core.common.LoggerFactory;
import com.tencent.shadow.core.load_parameters.LoadParameters;
import com.tencent.shadow.core.manager.BasePluginManager;
import com.tencent.shadow.core.manager.installplugin.InstalledPlugin;
import com.tencent.shadow.core.manager.installplugin.InstalledType;
import com.tencent.shadow.dynamic.host.FailedException;
import com.tencent.shadow.dynamic.host.NotFoundException;
import com.tencent.shadow.dynamic.host.PluginManagerImpl;
import com.tencent.shadow.dynamic.host.PluginProcessService;
import com.tencent.shadow.dynamic.host.PpsController;
import com.tencent.shadow.dynamic.host.PpsStatus;
import com.tencent.shadow.dynamic.loader.PluginLoader;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static android.content.Context.BIND_AUTO_CREATE;

public abstract class PluginManagerThatUseDynamicLoader extends BasePluginManager implements PluginManagerImpl {

    private static final Logger mLogger = LoggerFactory.getLogger(PluginManagerThatUseDynamicLoader.class);


    protected PluginManagerThatUseDynamicLoader(Context context) {
        super(context);
    }

    /**
     * 插件进程PluginProcessService的接口
     */
    protected PpsController mPpsController;

    /**
     * 插件加载服务端接口6
     */
    protected PluginLoader mPluginLoader;

    /**
     * 防止绑定service重入
     */
    private AtomicBoolean mServiceConnecting = new AtomicBoolean(false);
    /**
     * 等待service绑定完成的计数器
     */
    private AtomicReference<CountDownLatch> mConnectCountDownLatch = new AtomicReference<>();

    /**
     * 启动PluginProcessService
     *
     * @param serviceName 注册在宿主中的插件进程管理service完整名字
     */
    public final void bindPluginProcessService(final String serviceName) {
        if (mServiceConnecting.get()) {
            if (mLogger.isInfoEnabled()) {
                mLogger.info("pps service connecting");
            }
            return;
        }
        if (mLogger.isInfoEnabled()) {
            mLogger.info("bindPluginProcessService " + serviceName);
        }

        mConnectCountDownLatch.set(new CountDownLatch(1));

        mServiceConnecting.set(true);

        final CountDownLatch startBindingLatch = new CountDownLatch(1);
        final boolean[] asyncResult = new boolean[1];
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(mHostContext, serviceName));
                boolean binding = mHostContext.bindService(intent, new ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName name, IBinder service) {
                        if (mLogger.isInfoEnabled()) {
                            mLogger.info("onServiceConnected connectCountDownLatch:" + mConnectCountDownLatch);
                        }
                        mServiceConnecting.set(false);
                        mPpsController = PluginProcessService.wrapBinder(service);
                        try {
                            mPpsController.setUuidManager(new UuidManagerBinder(PluginManagerThatUseDynamicLoader.this));
                        } catch (DeadObjectException e) {
                            if (mLogger.isErrorEnabled()) {
                                mLogger.error("onServiceConnected RemoteException:" + e);
                            }
                        } catch (RemoteException e) {
                            if (e.getClass().getSimpleName().equals("TransactionTooLargeException")) {
                                if (mLogger.isErrorEnabled()) {
                                    mLogger.error("onServiceConnected TransactionTooLargeException:" + e);
                                }
                            } else {
                                throw new RuntimeException(e);
                            }
                        }

                        try {
                            IBinder iBinder = mPpsController.getPluginLoader();
                            if (iBinder != null) {
                                mPluginLoader = new BinderPluginLoader(iBinder);
                            }
                        } catch (RemoteException ignored) {
                            if (mLogger.isErrorEnabled()) {
                                mLogger.error("onServiceConnected mPpsController getPluginLoader:", ignored);
                            }
                        }

                        mConnectCountDownLatch.get().countDown();

                        if (mLogger.isInfoEnabled()) {
                            mLogger.info("onServiceConnected countDown:" + mConnectCountDownLatch);
                        }
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName name) {
                        if (mLogger.isInfoEnabled()) {
                            mLogger.info("onServiceDisconnected");
                        }
                        mServiceConnecting.set(false);
                        mPpsController = null;
                        mPluginLoader = null;
                    }
                }, BIND_AUTO_CREATE);
                asyncResult[0] = binding;
                startBindingLatch.countDown();
            }
        });
        try {
            //等待bindService真正开始
            startBindingLatch.await(10, TimeUnit.SECONDS);
            if (!asyncResult[0]) {
                throw new IllegalArgumentException("无法绑定PPS:" + serviceName);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public final void waitServiceConnected(int timeout, TimeUnit timeUnit) throws TimeoutException {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new RuntimeException("waitServiceConnected 不能在主线程中调用");
        }
        try {
            if (mLogger.isInfoEnabled()) {
                mLogger.info("waiting service connect connectCountDownLatch:" + mConnectCountDownLatch);
            }
            long s = System.currentTimeMillis();
            boolean isTimeout = !mConnectCountDownLatch.get().await(timeout, timeUnit);
            if (isTimeout) {
                throw new TimeoutException("连接Service超时 ,等待了："+(System.currentTimeMillis() - s));
            }
            if (mLogger.isInfoEnabled()) {
                mLogger.info("service connected " + (System.currentTimeMillis() - s));
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public final void loadRunTime(String uuid) throws RemoteException, FailedException {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("loadRunTime mPpsController:" + mPpsController);
        }
        PpsStatus ppsStatus = mPpsController.getPpsStatus();
        if (!ppsStatus.runtimeLoaded) {
            mPpsController.loadRuntime(uuid);
        }
    }

    public final void loadPluginLoader(String uuid) throws RemoteException, FailedException {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("loadPluginLoader mPluginLoader:" + mPluginLoader);
        }
        if (mPluginLoader == null) {
            PpsStatus ppsStatus = mPpsController.getPpsStatus();
            if (!ppsStatus.loaderLoaded) {
                mPpsController.loadPluginLoader(uuid);
            }
            IBinder iBinder = mPpsController.getPluginLoader();
            mPluginLoader = new BinderPluginLoader(iBinder);
        }
    }


    /**
     * PluginManager对象创建的时候回调
     *
     * @param bundle 当PluginManager有更新时会回调老的PluginManager对象onSaveInstanceState存储数据，bundle不为null说明发生了更新
     *               为null说明是首次创建
     */
    public void onCreate(Bundle bundle) {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("onCreate bundle:" + bundle);
        }
    }

    /**
     * 当PluginManager有更新时会先回调老的PluginManager对象 onSaveInstanceState存储数据
     *
     * @param bundle 要存储的数据
     */
    public void onSaveInstanceState(Bundle bundle) {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("onSaveInstanceState:" + bundle);
        }
    }

    /**
     * 当PluginManager有更新时先会销毁老的PluginManager对象，回调对应的onDestroy
     */
    public void onDestroy() {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("onDestroy:");
        }
    }

    public InstalledApk getPlugin(String uuid, String partKey) throws FailedException, NotFoundException {
        try {
            InstalledPlugin.Part part;
            try {
                part = getPluginPartByPartKey(uuid, partKey);
            } catch (RuntimeException e) {
                throw new NotFoundException("uuid==" + uuid + "partKey==" + partKey + "的Plugin找不到");
            }
            String businessName = part instanceof InstalledPlugin.PluginPart ? ((InstalledPlugin.PluginPart) part).businessName : null;
            String[] dependsOn = part instanceof InstalledPlugin.PluginPart ? ((InstalledPlugin.PluginPart) part).dependsOn : null;
            String[] hostWhiteList = part instanceof InstalledPlugin.PluginPart ? ((InstalledPlugin.PluginPart) part).hostWhiteList : null;
            LoadParameters loadParameters
                    = new LoadParameters(businessName, partKey, dependsOn, hostWhiteList);

            Parcel parcelExtras = Parcel.obtain();
            loadParameters.writeToParcel(parcelExtras, 0);
            byte[] parcelBytes = parcelExtras.marshall();
            parcelExtras.recycle();

            return new InstalledApk(
                    part.pluginFile.getAbsolutePath(),
                    part.oDexDir == null ? null : part.oDexDir.getAbsolutePath(),
                    part.libraryDir == null ? null : part.libraryDir.getAbsolutePath(),
                    parcelBytes
            );
        } catch (RuntimeException e) {
            if (mLogger.isErrorEnabled()) {
                mLogger.error("getPlugin exception:", e);
            }
            throw new FailedException(e);
        }
    }

    private InstalledApk getInstalledPL(String uuid, int type) throws FailedException, NotFoundException {
        try {
            InstalledPlugin.Part part;
            try {
                part = getLoaderOrRunTimePart(uuid, type);
            } catch (RuntimeException e) {
                if (mLogger.isErrorEnabled()) {
                    mLogger.error("getInstalledPL exception:", e);
                }
                throw new NotFoundException("uuid==" + uuid + " type==" + type + "没找到。cause：" + e.getMessage());
            }
            return new InstalledApk(part.pluginFile.getAbsolutePath(),
                    part.oDexDir == null ? null : part.oDexDir.getAbsolutePath(),
                    part.libraryDir == null ? null : part.libraryDir.getAbsolutePath());
        } catch (RuntimeException e) {
            throw new FailedException(e);
        }
    }

    public InstalledApk getPluginLoader(String uuid) throws FailedException, NotFoundException {
        return getInstalledPL(uuid, InstalledType.TYPE_PLUGIN_LOADER);
    }

    public InstalledApk getRuntime(String uuid) throws FailedException, NotFoundException {
        return getInstalledPL(uuid, InstalledType.TYPE_PLUGIN_RUNTIME);
    }
}
