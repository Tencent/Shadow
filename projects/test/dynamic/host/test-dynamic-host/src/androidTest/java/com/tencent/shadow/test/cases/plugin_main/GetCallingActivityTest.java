package com.tencent.shadow.test.cases.plugin_main;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;

import org.hamcrest.Matchers;
import org.junit.Test;

public class GetCallingActivityTest extends PluginMainAppTest {

    @Override
    protected Intent getLaunchIntent() {
        Intent pluginIntent = new Intent();
        String packageName = ApplicationProvider.getApplicationContext().getPackageName();
        pluginIntent.setClassName(
                packageName,
                "com.tencent.shadow.demo.usecases.activity.JumpActivity"
        );
        pluginIntent.putExtra("targetClassName", "com.tencent.shadow.demo.usecases.activity.TestCallingActivity");
        return pluginIntent;
    }

    @Test
    public void testGetCallingActivity() {
        Espresso.onView(ViewMatchers.withTagValue(Matchers.<Object>is("button"))).perform(ViewActions.click());

        matchTextWithViewTag("text", "com.tencent.shadow.demo.usecases.activity.JumpActivity");
    }
}