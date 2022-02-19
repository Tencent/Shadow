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

package com.tencent.shadow.test.dynamic.manager;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.shadow.core.manager.installplugin.InstalledPlugin;
import com.tencent.shadow.dynamic.host.EnterCallback;
import com.tencent.shadow.dynamic.host.PpsController;
import com.tencent.shadow.dynamic.host.PpsStatus;
import com.tencent.shadow.test.lib.constant.Constant;
import com.tencent.shadow.test.lib.test_manager.TestManager;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ReinstallPluginTestDynamicPluginManager extends FastPluginManager {

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private Context mCurrentContext;

    public ReinstallPluginTestDynamicPluginManager(Context context) {
        super(context);
        mCurrentContext = context;
    }

    /**
     * @return PluginManager实现的别名，用于区分不同PluginManager实现的数据存储路径
     */
    @Override
    protected String getName() {
        return "reinstall-plugin-test-dynamic-manager";
    }

    /**
     * @return 宿主中注册的PluginProcessService实现的类名
     */
    @Override
    protected String getPluginProcessServiceName() {
        return "com.tencent.shadow.test.dynamic.host.PluginProcessPPS";
    }

    public String getProcessName(Context context, int pid) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return "";
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return "";
    }

    public int getProcessId(Context context, String name) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return -1;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.processName.equals(name)) {
                return procInfo.pid;
            }
        }
        return -1;
    }

    @Override
    public void enter(final Context context, long fromId, Bundle bundle, final EnterCallback callback) {
        final String pluginZipPath = bundle.getString(Constant.KEY_PLUGIN_ZIP_PATH);
        final String partKey = bundle.getString(Constant.KEY_PLUGIN_PART_KEY);
        final String className = bundle.getString(Constant.KEY_ACTIVITY_CLASSNAME);
        if (className == null) {
            throw new NullPointerException("className == null");
        }

        if(fromId == Constant.FROM_ID_REINSTALL_PLUGIN || fromId == Constant.FROM_ID_REINSTALL_PLUGIN_ONLY) executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String zipPath;
                    zipPath = pluginZipPath;
                    // 安装后卸载再安装
                    //InstalledPlugin installedPlugin = installPlugin(zipPath, null, true);
                    zipPath = pluginZipPath.replace("plugin-debug.zip", "plugin-reinstall-debug.zip");
                   // deleteInstalledPlugin(installedPlugin.UUID);
                    InstalledPlugin installedPlugin = installPlugin(zipPath, null, true);

                    TestManager.uuid = installedPlugin.UUID;

                    Intent pluginIntent = new Intent();
                    pluginIntent.setComponent(
                            new ComponentName(context.getPackageName(),className)
                    );

                    startPluginActivity(context, installedPlugin, partKey, pluginIntent);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        else if(fromId == Constant.FROM_ID_REINSTALL_PLUGIN_WHEN_USED) executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String zipPath;
                    zipPath = pluginZipPath;
                    InstalledPlugin installedPlugin = installPlugin(zipPath, null, true);
                    TestManager.uuid = installedPlugin.UUID;

                    Intent pluginIntent = new Intent();
                    pluginIntent.setComponent(
                            new ComponentName(context.getPackageName(),className)
                    );

                    startPluginActivity(context, installedPlugin, partKey, pluginIntent);


                    // 先卸载之前在用的插件

                    Thread.sleep(5000);

                    int pid = getProcessId(mCurrentContext,".plugin");
                    PpsStatus ppsStatus = mPpsController.getPpsStatus();
                    if(ppsStatus.runtimeLoaded) {
                        try {
                            //android.os.Process.killProcess(pid);
                            mPpsController.exit();
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        //android.os.Process.killProcess(pid);
                    }

                    Thread.sleep(5000);
                   // android.os.Process.killProcess(pid);

                    //

                    //

                    ppsStatus = mPpsController.getPpsStatus();

                    deleteInstalledPlugin(TestManager.uuid);
                    // 找 第二次编译的包
                    String reinstallPluginZipPath = pluginZipPath.replace("plugin-debug.zip", "plugin-reinstall-debug.zip");
                    // 再安装
                    installedPlugin = installPlugin(reinstallPluginZipPath, null, true);

                    TestManager.uuid = installedPlugin.UUID;

                    pluginIntent = new Intent();
                    pluginIntent.setClassName(
                            context.getPackageName(),
                            className
                    );

                    startPluginActivity(context, installedPlugin, partKey, pluginIntent);


                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

}
