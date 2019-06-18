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

import android.content.Context;

import com.tencent.shadow.core.common.InstalledApk;

final class LoaderImplLoader extends ImplLoader {
    /**
     * 加载{@link #sLoaderFactoryImplClassName}时
     * 需要从宿主PathClassLoader（含双亲委派）中加载的类
     */
    private static final String[] sInterfaces = new String[]{
            //当runtime是动态加载的时候，runtime的ClassLoader是PathClassLoader的parent，
            // 所以不需要写在这个白名单里。但是写在这里不影响，也可以兼容runtime打包在宿主的情况。
            "com.tencent.shadow.core.runtime.container",
            "com.tencent.shadow.dynamic.host",
            "com.tencent.shadow.core.common"
    };

    private final static String sLoaderFactoryImplClassName
            = "com.tencent.shadow.dynamic.loader.impl.LoaderFactoryImpl";

    PluginLoaderImpl load(InstalledApk installedApk, String uuid, Context appContext) throws Exception {
        ApkClassLoader pluginLoaderClassLoader = new ApkClassLoader(
                installedApk,
                LoaderImplLoader.class.getClassLoader(),
                loadWhiteList(installedApk),
                1
        );
        LoaderFactory loaderFactory = pluginLoaderClassLoader.getInterface(
                LoaderFactory.class,
                sLoaderFactoryImplClassName
        );

        return loaderFactory.buildLoader(uuid, appContext);
    }

    @Override
    String[] getCustomWhiteList() {
        return sInterfaces;
    }
}
