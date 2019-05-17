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
