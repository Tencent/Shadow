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
 * 插件前台Service由宿主PluginContainerService承载：多个插件Service共享一个容器，
 * 生命周期独立转发，全部停止后容器才停止。
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class PluginForegroundServiceTest extends PluginMainAppTest {

    private static final String CONTAINER_FOREGROUND = "running=true,foreground=true";
    private static final String CONTAINER_STOPPED = "running=false";

    @Override
    protected Intent getLaunchIntent() {
        Intent pluginIntent = new Intent();
        String packageName = ApplicationProvider.getApplicationContext().getPackageName();
        pluginIntent.setClassName(
                packageName,
                "com.tencent.shadow.test.plugin.general_cases.lib.usecases.service.TestForegroundServiceActivity"
        );
        return pluginIntent;
    }

    @Test
    public void testServicesShareContainer() {
        matchTextWithViewTag("container", CONTAINER_STOPPED);

        click("startA");
        String events = "A.onCreate-A.onStartCommand";
        matchTextWithViewTag("events", events);
        matchTextWithViewTag("container", CONTAINER_FOREGROUND);

        click("startB");
        events += "-B.onCreate-B.onStartCommand";
        matchTextWithViewTag("events", events);
        matchTextWithViewTag("container", CONTAINER_FOREGROUND);

        click("stopA");
        events += "-A.onDestroy";
        matchTextWithViewTag("events", events);
        matchTextWithViewTag("container", CONTAINER_FOREGROUND);

        click("stopB");
        events += "-B.onDestroy";
        matchTextWithViewTag("events", events);
        matchTextWithViewTag("container", CONTAINER_STOPPED);
    }

    @Test
    public void testRestartAfterStop() {
        click("startA");
        String events = "A.onCreate-A.onStartCommand";
        click("startA");
        events += "-A.onStartCommand";
        matchTextWithViewTag("events", events);

        click("stopA");
        events += "-A.onDestroy";
        matchTextWithViewTag("events", events);
        matchTextWithViewTag("container", CONTAINER_STOPPED);

        click("startA");
        events += "-A.onCreate-A.onStartCommand";
        matchTextWithViewTag("events", events);
        matchTextWithViewTag("container", CONTAINER_FOREGROUND);

        click("stopA");
        events += "-A.onDestroy";
        matchTextWithViewTag("events", events);
        matchTextWithViewTag("container", CONTAINER_STOPPED);
    }

    @Test
    public void testStopBeforeContainerStarted() {
        click("startThenStopA");
        matchTextWithViewTag("events", "A.onCreate-A.onStartCommand-A.onDestroy");
        matchTextWithViewTag("container", CONTAINER_STOPPED);
    }

    private void click(String tag) {
        Espresso.onView(ViewMatchers.withTagValue(Matchers.<Object>is(tag))).perform(ViewActions.click());
    }
}
