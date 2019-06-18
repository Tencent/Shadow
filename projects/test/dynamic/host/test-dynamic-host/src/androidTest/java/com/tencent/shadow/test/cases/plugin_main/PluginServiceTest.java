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


@RunWith(AndroidJUnit4.class)
@LargeTest
public class PluginServiceTest extends PluginMainAppTest {

    @Override
    protected Intent getLaunchIntent() {
        Intent pluginIntent = new Intent();
        String packageName = ApplicationProvider.getApplicationContext().getPackageName();
        pluginIntent.setClassName(
                packageName,
                "com.tencent.shadow.test.plugin.general_cases.lib.usecases.service.TestStartServiceActivity"
        );
        return pluginIntent;
    }

    @Test
    public void testService() {
        // test startService
        click("start");
        String text = "onCreate-onStartCommand";
        check(text);

        // test stopService
        click("stop");
        text += "-onDestroy";
        check(text);

        // test bindService
        click("bind");
        text += "-onCreate-onBind";
        check(text);

        // test callBinder
        click("testBinder");
        text += "-callTest";
        check(text);

        // test unbindService
        click("unbind");
        text += "-onUnbind-onDestroy";
        check(text);

        // test startService + bindService + stopService + unBind
        click("start");
        text += "-onCreate-onStartCommand";
        check(text);
        click("bind");
        text += "-onBind";
        check(text);
        click("stop");
        check(text);
        click("unbind");
        text += "-onUnbind-onDestroy";
        check(text);
    }

    private void click(String tag){
        Espresso.onView(ViewMatchers.withTagValue(Matchers.<Object>is(tag))).perform(ViewActions.click());
    }

    private void check(String text){
        matchTextWithViewTag("text",text);
    }

}
