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

import android.content.ComponentName;
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;

import org.hamcrest.Matchers;
import org.junit.Test;

public class GetCallingActivityTest extends PluginMainAppTest {

    public static final String PrintActivityResultActivity = "com.tencent.shadow.test.plugin.general_cases.lib.usecases.activity.PrintActivityResultActivity";

    @Override
    protected Intent getLaunchIntent() {
        Intent pluginIntent = new Intent();
        String packageName = ApplicationProvider.getApplicationContext().getPackageName();
        pluginIntent.setClassName(
                packageName,
                PrintActivityResultActivity
        );
        pluginIntent.putExtra("targetClassName", "com.tencent.shadow.test.plugin.general_cases.lib.usecases.activity.TestCallingActivity");
        pluginIntent.putExtra("waitForResult", false);
        return pluginIntent;
    }

    @Test
    public void testGetCallingActivity() {
        Espresso.onView(ViewMatchers.withTagValue(Matchers.<Object>is("button"))).perform(ViewActions.click());

        String packageName = ApplicationProvider.getApplicationContext().getPackageName();
        ComponentName componentName = new ComponentName(packageName, PrintActivityResultActivity);
        matchTextWithViewTag("TAG_GET_CALLING_ACTIVITY", componentName.toShortString());
    }
}