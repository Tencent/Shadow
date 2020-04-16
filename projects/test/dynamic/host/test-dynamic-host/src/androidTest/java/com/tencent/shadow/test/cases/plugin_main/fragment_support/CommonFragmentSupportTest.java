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

package com.tencent.shadow.test.cases.plugin_main.fragment_support;

import android.content.Intent;
import android.os.Build;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;

import com.tencent.shadow.test.cases.plugin_main.PluginMainAppTest;

import org.hamcrest.Matchers;
import org.junit.Assume;
import org.junit.Test;

abstract class CommonFragmentSupportTest extends PluginMainAppTest {

    abstract protected String getActivityName();

    abstract protected String expectMsg();

    abstract protected String fragmentType();

    @Override
    protected Intent getLaunchIntent() {
        Intent pluginIntent = new Intent();
        String packageName = ApplicationProvider.getApplicationContext().getPackageName();
        pluginIntent.setClassName(
                packageName,
                "com.tencent.shadow.test.plugin.general_cases.lib.usecases.fragment." + getActivityName()
        );
        pluginIntent.putExtra("FragmentType", fragmentType());
        return pluginIntent;
    }

    @Test
    public void setArguments() {
        matchTextWithViewTag("TestFragmentTextView", expectMsg());
    }

    @Test
    public void fragmentStartActivity() {
        Espresso.onView(ViewMatchers.withTagValue(Matchers.<Object>is("fragmentStartActivity"))).perform(ViewActions.click());
        matchTextWithViewTag("finish_button", "finish");
    }

    @Test
    public void fragmentStartActivityWithOptions() {
        Assume.assumeTrue(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
        Espresso.onView(ViewMatchers.withTagValue(Matchers.<Object>is("fragmentStartActivityWithOptions"))).perform(ViewActions.click());
        matchTextWithViewTag("finish_button", "finish");
    }

    @Test
    public void attachContext() {
        Assume.assumeTrue(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
        matchTextWithViewTag("AttachContextView",
                "com.tencent.shadow.test.plugin.general_cases.lib.usecases.fragment." + getActivityName());
    }

    @Test
    public void attachActivity() {
        matchTextWithViewTag("AttachActivityView",
                "com.tencent.shadow.test.plugin.general_cases.lib.usecases.fragment." + getActivityName());
    }

    @Test
    public void getActivity() {
        matchTextWithViewTag("GetActivityView",
                "com.tencent.shadow.test.plugin.general_cases.lib.usecases.fragment." + getActivityName());
    }

    @Test
    public void getContext() {
        Assume.assumeTrue(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
        matchTextWithViewTag("GetContextView",
                "com.tencent.shadow.test.plugin.general_cases.lib.usecases.fragment." + getActivityName());
    }

    @Test
    public void getHost() {
        Assume.assumeTrue(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
        matchTextWithViewTag("GetHostView",
                "com.tencent.shadow.test.plugin.general_cases.lib.usecases.fragment." + getActivityName());
    }
}
