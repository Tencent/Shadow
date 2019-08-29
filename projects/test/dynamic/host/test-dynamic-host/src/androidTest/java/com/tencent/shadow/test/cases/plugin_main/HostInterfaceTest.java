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

package com.tencent.shadow.test.cases.plugin_main;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;

/**
 * 插件访问宿主类的白名单机制测试用例
 */
public class HostInterfaceTest extends PluginMainAppTest {

    @Override
    protected Intent getLaunchIntent() {
        Intent pluginIntent = new Intent();
        String packageName = ApplicationProvider.getApplicationContext().getPackageName();
        pluginIntent.setClassName(
                packageName,
                "com.tencent.shadow.test.plugin.general_cases.lib.usecases.interfaces.TestHostInterfaceActivity"
        );
        return pluginIntent;
    }

    @Test
    public void testInWhitelist() {
        matchTextWithViewTag("TAG_loadClass_in_whitelist",
                Boolean.toString(true));
    }

    @Test
    public void testNotInWhitelistOtherPackage() {
        matchTextWithViewTag("TAG_loadClass_not_in_whitelist_other_package",
                Boolean.toString(false));
    }

    @Test
    public void testNotInWhitelistSubPackage() {
        matchTextWithViewTag("TAG_loadClass_not_in_whitelist_sub_package",
                Boolean.toString(false));
    }
}