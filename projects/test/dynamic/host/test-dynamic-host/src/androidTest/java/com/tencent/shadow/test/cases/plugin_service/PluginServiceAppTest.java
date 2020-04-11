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

package com.tencent.shadow.test.cases.plugin_service;

import android.app.Activity;

import com.tencent.shadow.test.PluginTest;
import com.tencent.shadow.test.dynamic.host.BindPluginServiceActivity;
import com.tencent.shadow.test.lib.constant.Constant;

public abstract class PluginServiceAppTest extends PluginTest {

    /**
     * 要启动的插件的PartKey
     */
    @Override
    protected String getPartKey() {
        return Constant.PART_KEY_PLUGIN_SERVICE_FOR_HOST;
    }

    @Override
    protected Class<? extends Activity> getJumpActivityClass() {
        return BindPluginServiceActivity.class;
    }
}
