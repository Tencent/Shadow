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

package com.tencent.shadow.sample.plugin.runtime;


import android.annotation.SuppressLint;

import com.tencent.shadow.core.runtime.container.PluginContainerActivity;

@SuppressLint("Registered")//无需注册在这个模块的Manifest中，要注册在宿主的Manifest中。
public class PluginDefaultProxyActivity extends PluginContainerActivity {

    @Override
    protected String getDelegateProviderKey() {
        return "SAMPLE";
    }
}
