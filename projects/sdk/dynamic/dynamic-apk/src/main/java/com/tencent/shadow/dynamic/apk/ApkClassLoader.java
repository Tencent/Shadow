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

package com.tencent.shadow.dynamic.apk;

import android.os.Build;

import com.tencent.shadow.core.common.InstalledApk;

import dalvik.system.DexClassLoader;

/**
 * Apk插件加载专用ClassLoader
 * <p>
 * 将宿主apk和插件apk隔离。但例外的是,插件可以从宿主apk中加载到约定的接口。
 * 这样隔离的目的是让宿主apk中的类可以通过约定的接口使用插件apk中的实现。而插件中的类不会使用到和宿主同名的类。
 * <p>
 * 如果目标类符合构造时传入的包名,则从parent ClassLoader中查找,否则先从自己的dexPath中查找,如果找不到,则再从
 * parent的parent ClassLoader中查找。
 *
 * @author cubershi
 */
public class ApkClassLoader extends DexClassLoader {
    private ClassLoader mGrandParent;
    private final String[] mInterfacePackageNames;

    public ApkClassLoader(InstalledApk installedApk,
                          ClassLoader parent, String[] mInterfacePackageNames, int grandTimes) {
        super(installedApk.apkFilePath, installedApk.oDexPath, installedApk.libraryPath, parent);
        ClassLoader grand = parent;
        for (int i = 0; i < grandTimes; i++) {
            grand = grand.getParent();
        }
        mGrandParent = grand;
        this.mInterfacePackageNames = mInterfacePackageNames;
    }

    @Override
    protected Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException {
        String packageName;
        int dot = className.lastIndexOf('.');
        if (dot != -1) {
            packageName = className.substring(0, dot);
        } else {
            packageName = "";
        }

        boolean isInterface = false;
        for (String interfacePackageName : mInterfacePackageNames) {
            if (packageName.equals(interfacePackageName)) {
                isInterface = true;
                break;
            }
        }

        if (isInterface) {
            return super.loadClass(className, resolve);
        } else {
            Class<?> clazz = findLoadedClass(className);

            if (clazz == null) {
                ClassNotFoundException suppressed = null;
                try {
                    clazz = findClass(className);
                } catch (ClassNotFoundException e) {
                    suppressed = e;
                }

                if (clazz == null) {
                    try {
                        clazz = mGrandParent.loadClass(className);
                    } catch (ClassNotFoundException e) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            e.addSuppressed(suppressed);
                        }
                        throw e;
                    }
                }
            }

            return clazz;
        }
    }

    /**
     * 从apk中读取接口的实现
     *
     * @param clazz     接口类
     * @param className 实现类的类名
     * @param <T>       接口类型
     * @return 所需接口
     * @throws Exception
     */
    public <T> T getInterface(Class<T> clazz, String className) throws Exception {
        try {
            Class<?> interfaceImplementClass = loadClass(className);
            Object interfaceImplement = interfaceImplementClass.newInstance();
            return clazz.cast(interfaceImplement);
        } catch (ClassNotFoundException | InstantiationException
                | ClassCastException | IllegalAccessException e) {
            throw new Exception(e);
        }
    }

}

