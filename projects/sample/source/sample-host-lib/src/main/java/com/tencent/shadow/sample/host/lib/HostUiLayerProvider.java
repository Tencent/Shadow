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

package com.tencent.shadow.sample.host.lib;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

/**
 * 这是一个将要打包到宿主中的类。原本的目的是宿主依赖插件，宿主
 */
public class HostUiLayerProvider {
    private static HostUiLayerProvider sInstance;

    public static void init(Context mHostApplicationContext) {
        sInstance = new HostUiLayerProvider(mHostApplicationContext);
    }

    public static HostUiLayerProvider getInstance() {
        return sInstance;
    }

    final private Context mHostApplicationContext;

    private HostUiLayerProvider(Context mHostApplicationContext) {
        this.mHostApplicationContext = mHostApplicationContext;
    }

    public View buildHostUiLayer() {
        return LayoutInflater.from(mHostApplicationContext)
                .inflate(R.layout.host_ui_layer_layout, null, false);
    }
}
