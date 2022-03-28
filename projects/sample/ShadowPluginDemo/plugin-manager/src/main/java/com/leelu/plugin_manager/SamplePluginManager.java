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

package com.leelu.plugin_manager;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.leelu.constants.Constant;
import com.tencent.shadow.core.manager.installplugin.InstalledPlugin;
import com.tencent.shadow.dynamic.host.EnterCallback;

import org.json.JSONException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SamplePluginManager extends FastPluginManager {

    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final String TAG = this.getClass().getSimpleName();
    private Context mCurrentContext;

    public SamplePluginManager(Context context) {
        super(context);
        mCurrentContext = context;
    }

    /**
     * @return PluginManager实现的别名，用于区分不同PluginManager实现的数据存储路径
     */
    @Override
    protected String getName() {
        return "test-dynamic-manager";
    }

    /**
     * @return 宿主中注册的PluginProcessService实现的类名
     */
    @Override
    protected String getPluginProcessServiceName(String partKey) {
        return "com.leelu.shadow.service.MainPluginProcessService";
/*        if (Constant.PLUGIN_APP_NAME.equals(partKey)) {//plugin-app：插件标识名
            return "com.leelu.shadow.service.MainPluginProcessService";
        }
        if (Constant.PLUGIN_OTHER_NAME.equals(partKey)) {//plugin-other：插件标识名
            return "com.leelu.shadow.service.MainPluginProcessService";
        } else {
            return "com.leelu.shadow.service.MainPluginProcessService";
            //如果有默认PPS，可用return代替throw
//            throw new IllegalArgumentException("unexpected plugin load request意外的插件加载请求: " + partKey);
        }*/
    }

    @Override
    public void enter(final Context context, long fromId, Bundle bundle, final EnterCallback callback) {

        Log.d("SamplePluginManager", "enter ，fromId = " + fromId);
        if (fromId == Constant.FROM_ID_NOOP) {
            //do nothing.
        } else if (fromId == Constant.FROM_ID_START_ACTIVITY) {
            onStartActivity(context, bundle, callback);
        } else if (fromId == Constant.FROM_ID_CLOSE) {
            close();
        }else if (fromId == Constant.FROM_ID_START_ACTIVITY_NORMAL) {
            startActivityNormal(context, bundle);
        } else if (fromId == Constant.FROM_ID_LOAD_VIEW_TO_HOST) {
            loadViewToHost(context, bundle);
        } else if (fromId == Constant.FROM_ID_INSTALL_PLUGIN) {
            final String pluginZipPath = bundle.getString(Constant.KEY_PLUGIN_ZIP_PATH);
            try {
                installPlugin(pluginZipPath, null, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (fromId == Constant.FROM_ID_LOAD_LOADER_AND_RUNTIME) {
            final String pluginZipPath = bundle.getString(Constant.KEY_PLUGIN_ZIP_PATH);
            final String partKey = bundle.getString(Constant.KEY_PLUGIN_NAME);
            loadLoaderAndRuntime(pluginZipPath, partKey);
        } else {
            throw new IllegalArgumentException("不认识的fromId==" + fromId);
        }
    }

    private void startActivityNormal(Context context, Bundle bundle) {
        //从bundle中取出数据。
        //plugin的zip地址
        final String pluginZipPath = bundle.getString(Constant.KEY_PLUGIN_ZIP_PATH);
        Log.d(TAG,"pluginZipPath = " + pluginZipPath);
        //plugin的partKey。打包插件时定义的，插件zip中的config文件中也可以看到
        final String partKey = bundle.getString(Constant.KEY_PLUGIN_PART_KEY);
        Log.d(TAG,"partKey = " + partKey);
        //要启动的activity全名
        final String className = bundle.getString(Constant.KEY_ACTIVITY_CLASSNAME);
        Log.d(TAG,"className = " + className);
        if (className == null) {
            throw new NullPointerException("className == null");
        }
        //启动时附带的extras中的数据
        final Bundle extras = bundle.getBundle(Constant.KEY_EXTRAS);

/*        if (callback != null) {
            //可以返回一个加载动画的view。
            final View view = LayoutInflater.from(mCurrentContext).inflate(R.layout.activity_load_plugin, null);
            //启动开始时的回调。
            callback.onShowLoadingView(view);
        }*/

        //启动过程是阻塞的，需要在io线程进行
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //如果还未安装插件zip。会在这解压安装.最后返回插件的安装信息
                    InstalledPlugin installedPlugin = installPlugin(pluginZipPath, null, true);

                    //加载插件
                    loadPlugin(installedPlugin.UUID, partKey);
//                    loadPlugin(installedPlugin.UUID, PART_KEY_PLUGIN_MAIN_APP);
                    //调用插件的application，如果是第一次启动这个插件的话
                    callApplicationOnCreate(partKey);
//                    callApplicationOnCreate(PART_KEY_PLUGIN_MAIN_APP);

                    //构建启动的intent
                    Intent pluginIntent = new Intent();
                    pluginIntent.setClassName(
                            context.getPackageName(),
                            className
                    );
                    if (extras != null) {
                        pluginIntent.replaceExtras(extras);
                    }
                    //对intent进行转换。启动占位activity。（占位activity会代理我们插件中activity的生命周期和方法）
                    Intent intent = mPluginLoader.convertActivityIntent(pluginIntent);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //通过mPluginLoader调用的方法，会通过bind调用到DynamicPluginLoader中的方法，DynamicPluginLoader最终基本都会调用到 ShadowPluginLoader 中去。而我们会在 loader中创建一个类，继承自ShadowPluginLoader。
                    //调用 ShadowPluginLoader 中对应的 startActivityInPluginProcess 方法
                    mPluginLoader.startActivityInPluginProcess(intent);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
/*                if (callback != null) {
                    //启动完成的回调
                    callback.onCloseLoadingView();
                }*/
            }
        });
    }

    private void loadViewToHost(final Context context, Bundle bundle) {
        Intent pluginIntent = new Intent();
        pluginIntent.setClassName(
                context.getPackageName(),
                "com.tencent.shadow.sample.plugin.app.lib.usecases.service.HostAddPluginViewService"
        );
        pluginIntent.putExtras(bundle);
        try {
            mPluginLoader.startPluginService(pluginIntent);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    private void onStartActivity(final Context context, Bundle bundle, final EnterCallback callback) {
//        final String pluginZipPath = bundle.getString(Constant.KEY_PLUGIN_ZIP_PATH);
        final String partKey = bundle.getString(Constant.KEY_PLUGIN_NAME);
        final String className = bundle.getString(Constant.KEY_ACTIVITY_CLASSNAME);
        if (className == null) {
            throw new NullPointerException("className == null");
        }
        final Bundle extras = bundle.getBundle(Constant.KEY_EXTRAS);
        if (callback != null) {
//            final View view = LayoutInflater.from(mCurrentContext).inflate(R.layout.view_plugin_manager_loading, null);
//            callback.onShowLoadingView(view);
        }

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
//                    InstalledPlugin installedPlugin = installPlugin(pluginZipPath, null, true);
                    loadPlugin("2324CE96-044C-4FE9-BFA6-1311BE8CC986", partKey);
                    callApplicationOnCreate(partKey);
                    Intent pluginIntent = new Intent();
                    pluginIntent.setClassName(
                            context.getPackageName(),
                            className
                    );
                    if (extras != null) {
                        pluginIntent.replaceExtras(extras);
                    }
                    Intent intent = mPluginLoader.convertActivityIntent(pluginIntent);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mPluginLoader.startActivityInPluginProcess(intent);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                if (callback != null) {
                    callback.onCloseLoadingView();
                }
            }
        });
    }




    private void loadLoaderAndRuntime(String pluginZipPath, String partKey) {
        try {
            Log.d(TAG,"loadLoaderAndRuntime , pluginZipPath = " + pluginZipPath + ", partKey = " +partKey);
            InstalledPlugin installedPlugin = installPlugin(pluginZipPath, null, true);
//            loadPluginLoaderAndRuntime(installedPlugin.UUID, partKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
