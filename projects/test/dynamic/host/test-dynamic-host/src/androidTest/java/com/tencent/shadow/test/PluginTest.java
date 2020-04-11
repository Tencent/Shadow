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

package com.tencent.shadow.test;

import android.app.Activity;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;

import com.tencent.shadow.test.dynamic.host.HostApplication;
import com.tencent.shadow.test.dynamic.host.JumpToPluginActivity;
import com.tencent.shadow.test.dynamic.host.R;
import com.tencent.shadow.test.dynamic.host.SimpleIdlingResourceImpl;
import com.tencent.shadow.test.lib.constant.Constant;
import com.tencent.shadow.test.lib.test_manager.TestManager;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;

public abstract class PluginTest {

    /**
     * 要启动的插件intent
     * @return  插件Activity intent
     */
    abstract protected Intent getLaunchIntent();

    /**
     * 要启动的插件的PartKey
     */
    abstract protected String getPartKey();

    /**
     * 检测view
     * @param tag  view的tag
     * @param text view上的文字
     */
    public void matchTextWithViewTag(String tag,String text){
        Espresso.onView(ViewMatchers.withTagValue(Matchers.<Object>is(tag)))
                .check(ViewAssertions.matches(ViewMatchers.withText(text)));
    }

    public void matchSubstringWithViewTag(String tag, String text) {
        Espresso.onView(ViewMatchers.withTagValue(Matchers.<Object>is(tag)))
                .check(ViewAssertions.matches(ViewMatchers.withSubstring(text)));
    }

    @Before
    public void launchActivity() {
        SimpleIdlingResourceImpl idlingResource = HostApplication.getApp().mIdlingResource;
        IdlingRegistry.getInstance().register(idlingResource);
        TestManager.TheSimpleIdlingResource = idlingResource;
        launchJumpActivity(getPartKey(), getLaunchIntent());

        Espresso.onView(ViewMatchers.withId(R.id.jump)).perform(ViewActions.click());
    }


    @After
    public void unregisterIdlingResource() {
        TestManager.TheSimpleIdlingResource = null;
        IdlingResource idlingResource = HostApplication.getApp().mIdlingResource;
        IdlingRegistry.getInstance().unregister(idlingResource);
    }

    private void launchJumpActivity(String partKey, Intent pluginIntent) {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), getJumpActivityClass());
        intent.putExtra(Constant.KEY_PLUGIN_PART_KEY, partKey);
        intent.putExtra(Constant.KEY_ACTIVITY_CLASSNAME, pluginIntent.getComponent().getClassName());
        intent.putExtra(Constant.KEY_EXTRAS, pluginIntent.getExtras());
        ActivityScenario.launch(intent);
    }

    protected Class<? extends Activity> getJumpActivityClass() {
        return JumpToPluginActivity.class;
    }
}
