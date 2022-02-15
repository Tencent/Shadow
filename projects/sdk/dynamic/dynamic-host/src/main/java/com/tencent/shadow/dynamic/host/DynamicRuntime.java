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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.tencent.shadow.core.common.InstalledApk;
import com.tencent.shadow.core.common.Logger;
import com.tencent.shadow.core.common.LoggerFactory;

import java.io.File;
import java.lang.reflect.Field;

import dalvik.system.BaseDexClassLoader;

/**
 * 将runtime apk加载到DexPathClassLoader，形成如下结构的classLoader树结构
 * ---BootClassLoader
 * ----RuntimeClassLoader
 * ------PathClassLoader
 */
public class DynamicRuntime {

    private static final Logger mLogger = LoggerFactory.getLogger(DynamicRuntime.class);

    private static final String SP_NAME = "ShadowRuntimeLoader";

    private static final String KEY_RUNTIME_APK = "KEY_RUNTIME_APK";
    private static final String KEY_RUNTIME_ODEX = "KEY_RUNTIME_ODEX";
    private static final String KEY_RUNTIME_LIB = "KEY_RUNTIME_LIB";

    /**
     * 加载runtime apk
     *
     * @return true 加载了新的runtime
     */
    public static boolean loadRuntime(InstalledApk installedRuntimeApk) {
        ClassLoader contextClassLoader = DynamicRuntime.class.getClassLoader();
        RuntimeClassLoader runtimeClassLoader = getRuntimeClassLoader();
        if (runtimeClassLoader != null) {
            String apkPath = runtimeClassLoader.apkPath;
            if (mLogger.isInfoEnabled()) {
                mLogger.info("last apkPath:" + apkPath + " new apkPath:" + installedRuntimeApk.apkFilePath);
            }
            if (TextUtils.equals(apkPath, installedRuntimeApk.apkFilePath)) {
                //已经加载相同版本的runtime了,不需要加载
                if (mLogger.isInfoEnabled()) {
                    mLogger.info("已经加载相同apkPath的runtime了,不需要加载");
                }
                return false;
            } else {
                //版本不一样，说明要更新runtime，先恢复正常的classLoader结构
                if (mLogger.isInfoEnabled()) {
                    mLogger.info("加载不相同apkPath的runtime了,先恢复classLoader树结构");
                }
                try {
                    recoveryClassLoader();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        //正常处理，将runtime 挂到pathclassLoader之上
        try {
            hackParentToRuntime(installedRuntimeApk, contextClassLoader);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return true;
    }


    private static void recoveryClassLoader() throws Exception {
        ClassLoader contextClassLoader = DynamicRuntime.class.getClassLoader();
        ClassLoader child = contextClassLoader;
        ClassLoader tmpClassLoader = contextClassLoader.getParent();
        while (tmpClassLoader != null) {
            if (tmpClassLoader instanceof RuntimeClassLoader) {
                hackParentClassLoader(child, tmpClassLoader.getParent());
                return;
            }
            child = tmpClassLoader;
            tmpClassLoader = tmpClassLoader.getParent();
        }
    }


    private static RuntimeClassLoader getRuntimeClassLoader() {
        ClassLoader contextClassLoader = DynamicRuntime.class.getClassLoader();
        ClassLoader tmpClassLoader = contextClassLoader.getParent();
        while (tmpClassLoader != null) {
            if (tmpClassLoader instanceof RuntimeClassLoader) {
                return (RuntimeClassLoader) tmpClassLoader;
            }
            tmpClassLoader = tmpClassLoader.getParent();
        }
        return null;
    }


    private static void hackParentToRuntime(InstalledApk installedRuntimeApk, ClassLoader contextClassLoader) throws Exception {
        RuntimeClassLoader runtimeClassLoader = new RuntimeClassLoader(installedRuntimeApk.apkFilePath, installedRuntimeApk.oDexPath,
                installedRuntimeApk.libraryPath, contextClassLoader.getParent());
        hackParentClassLoader(contextClassLoader, runtimeClassLoader);
    }


    /**
     * 修改ClassLoader的parent
     *
     * @param classLoader          需要修改的ClassLoader
     * @param newParentClassLoader classLoader的新的parent
     * @throws Exception 失败时抛出
     */
    static void hackParentClassLoader(ClassLoader classLoader,
                                      ClassLoader newParentClassLoader) throws Exception {
        Field field = getParentField();
        if (field == null) {
            throw new RuntimeException("在ClassLoader.class中没找到类型为ClassLoader的parent域");
        }
        field.setAccessible(true);
        field.set(classLoader, newParentClassLoader);
    }

    /**
     * 安全地获取到ClassLoader类的parent域
     *
     * @return ClassLoader类的parent域.或不能通过反射访问该域时返回null.
     */
    private static Field getParentField() {
        ClassLoader classLoader = DynamicRuntime.class.getClassLoader();
        ClassLoader parent = classLoader.getParent();
        Field field = null;
        for (Field f : ClassLoader.class.getDeclaredFields()) {
            try {
                boolean accessible = f.isAccessible();
                f.setAccessible(true);
                Object o = f.get(classLoader);
                f.setAccessible(accessible);
                if (o == parent) {
                    field = f;
                    break;
                }
            } catch (IllegalAccessException ignore) {
            }
        }
        return field;
    }

    /**
     * 重新恢复runtime
     *
     * @return true 进行了runtime恢复
     */
    public static boolean recoveryRuntime(Context context) {
        InstalledApk installedApk = getLastRuntimeInfo(context);
        if (installedApk != null && new File(installedApk.apkFilePath).exists()) {
            if (installedApk.oDexPath != null && !new File(installedApk.oDexPath).exists()) {
                return false;
            }
            try {
                hackParentToRuntime(installedApk, DynamicRuntime.class.getClassLoader());
                return true;
            } catch (Exception e) {
                if (mLogger.isErrorEnabled()) {
                    mLogger.error("recoveryRuntime 错误", e);
                }
                removeLastRuntimeInfo(context);
            }
        }
        return false;
    }

    @SuppressLint("ApplySharedPref")
    public static void saveLastRuntimeInfo(Context context, InstalledApk installedRuntimeApk) {
        SharedPreferences preferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        preferences.edit()
                .putString(KEY_RUNTIME_APK, installedRuntimeApk.apkFilePath)
                .putString(KEY_RUNTIME_ODEX, installedRuntimeApk.oDexPath)
                .putString(KEY_RUNTIME_LIB, installedRuntimeApk.libraryPath)
                .commit();
    }

    private static InstalledApk getLastRuntimeInfo(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        String apkFilePath = preferences.getString(KEY_RUNTIME_APK, null);
        String oDexPath = preferences.getString(KEY_RUNTIME_ODEX, null);
        String libraryPath = preferences.getString(KEY_RUNTIME_LIB, null);

        if (apkFilePath == null) {
            return null;
        } else {
            return new InstalledApk(apkFilePath, oDexPath, libraryPath);
        }
    }

    @SuppressLint("ApplySharedPref")
    private static void removeLastRuntimeInfo(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        preferences.edit()
                .remove(KEY_RUNTIME_APK)
                .remove(KEY_RUNTIME_ODEX)
                .remove(KEY_RUNTIME_LIB)
                .commit();
    }


    static class RuntimeClassLoader extends BaseDexClassLoader {
        /**
         * 加载的apk路径
         */
        private String apkPath;


        RuntimeClassLoader(String dexPath, String optimizedDirectory, String librarySearchPath, ClassLoader parent) {
            super(dexPath, optimizedDirectory == null ? null : new File(optimizedDirectory), librarySearchPath, parent);
            this.apkPath = dexPath;
        }
    }
}
