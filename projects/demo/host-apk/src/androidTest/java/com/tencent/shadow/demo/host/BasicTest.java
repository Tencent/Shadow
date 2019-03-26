/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tencent.shadow.demo.host;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class BasicTest {
    private static final String TAG = "ExampleInstrumentedTest";

    private IdlingResource mIdlingResource;

    @Before
    public void launchActivity() {
        String packageName = ApplicationProvider.getApplicationContext().getPackageName();
        Intent pluginIntent = new Intent();
        pluginIntent.setClassName(
                packageName,
                "com.tencent.shadow.demo.usecases.activity.TestActivityOnCreate"
        );
        ActivityScenario<JumpToPluginActivity> activityScenario = PluginActivityScenario.launch(pluginIntent);

        activityScenario.onActivity(new ActivityScenario.ActivityAction<JumpToPluginActivity>() {
            @Override
            public void perform(JumpToPluginActivity activity) {
                mIdlingResource = activity.mIdlingResource;
                IdlingRegistry.getInstance().register(mIdlingResource);
            }
        });
    }

    @Test
    public void testBasicUsage() {
        Espresso.onView(ViewMatchers.withId(R.id.jump)).perform(ViewActions.click());

        Espresso.onView(ViewMatchers.withTagValue(Matchers.<Object>is("tv_msg")))
                .check(ViewAssertions.matches(ViewMatchers.withText("Activity生命周期测试")));
    }

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            IdlingRegistry.getInstance().unregister(mIdlingResource);
        }
    }
}
