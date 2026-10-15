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
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 插件Activity声明singleTask和非默认taskAffinity时，应分配宿主独立Task容器，
 * 并在重复启动时复用同一个Task、回调onNewIntent。
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class IndependentTaskTest extends PluginMainAppTest {

    @Override
    protected Intent getLaunchIntent() {
        Intent pluginIntent = new Intent();
        String packageName = ApplicationProvider.getApplicationContext().getPackageName();
        pluginIntent.setClassName(
                packageName,
                "com.tencent.shadow.test.plugin.general_cases.lib.usecases.task.TestIndependentTaskActivity"
        );
        return pluginIntent;
    }

    @Test
    public void testLaunchInIndependentTask() {
        matchTextWithViewTag("task_root", "isTaskRoot=true");
        matchTextWithViewTag("lifecycle", "onCreate=1,onNewIntent=0");
        click("finish");
    }

    @Test
    public void testRelaunchReusesTask() {
        matchTextWithViewTag("lifecycle", "onCreate=1,onNewIntent=0");

        click("relaunch");
        matchTextWithViewTag("lifecycle", "onCreate=1,onNewIntent=1");
        matchTextWithViewTag("task_root", "isTaskRoot=true");

        click("relaunch");
        matchTextWithViewTag("lifecycle", "onCreate=1,onNewIntent=2");
        click("finish");
    }

    private void click(String tag) {
        Espresso.onView(ViewMatchers.withTagValue(Matchers.<Object>is(tag))).perform(ViewActions.click());
    }
}
