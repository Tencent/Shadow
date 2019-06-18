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

package com.tencent.shadow.test.none_dynamic.host;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class BasicTest {

    @Rule
    public ActivityTestRule<?> startActivityRule() {
        Context applicationContext = InstrumentationRegistry.getTargetContext().getApplicationContext();
        HostApplication application = (HostApplication) applicationContext;
        application.loadPlugin(HostApplication.PART_MAIN);

        String packageName = InstrumentationRegistry.getTargetContext().getPackageName();
        Intent pluginIntent = new Intent();
        pluginIntent.setClassName(
                packageName,
                "com.tencent.shadow.test.plugin.general_cases.lib.usecases.activity.TestActivityOnCreate"
        );
        return PluginActivityTestRule.build(pluginIntent);
    }


    @Test
    public void testBasicUsage() {
        Espresso.onView(ViewMatchers.withTagValue(Matchers.<Object>is("tv_msg")))
                .check(ViewAssertions.matches(ViewMatchers.withText("Activity生命周期测试")));
    }
}
