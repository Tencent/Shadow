package com.tencent.shadow.test.cases.plugin_main;

import android.content.Intent;
import android.os.Build;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ActivityLifecycleCallbacksTest extends PluginMainAppTest {

    @Override
    protected Intent getLaunchIntent() {
        Intent pluginIntent = new Intent();
        String packageName = ApplicationProvider.getApplicationContext().getPackageName();
        pluginIntent.setClassName(
                packageName,
                "com.tencent.shadow.test.plugin.general_cases.lib.usecases.application.ActivityLifecycleCallbacksTestActivity"
        );
        return pluginIntent;
    }


    @Test
    public void testRecreateRecord() {
        Espresso.onView(ViewMatchers.withTagValue(Matchers.<Object>is("recreate")))
                .perform(ViewActions.click());

        String api29 = "[onActivityPreCreated, " +
                "onActivityCreated, " +
                "onActivityPostCreated, " +
                "onActivityPreStarted, " +
                "onActivityStarted, " +
                "onActivityPostStarted, " +
                "onActivityPreResumed, " +
                "onActivityResumed, " +
                "onActivityPostResumed, " +
                "onActivityPrePaused, " +
                "onActivityPaused, " +
                "onActivityPostPaused, " +
                "onActivityPreStopped, " +
                "onActivityStopped, " +
                "onActivityPostStopped, " +
                "onActivityPreSaveInstanceState, " +
                "onActivitySaveInstanceState, " +
                "onActivityPostSaveInstanceState, " +
                "onActivityPreDestroyed, " +
                "onActivityDestroyed, " +
                "onActivityPostDestroyed, " +
                "onActivityPreCreated, " +
                "onActivityCreated]";

        String api28 = "[onActivityCreated, " +
                "onActivityStarted, " +
                "onActivityResumed, " +
                "onActivityPaused, " +
                "onActivityStopped, " +
                "onActivitySaveInstanceState, " +
                "onActivityDestroyed, " +
                "onActivityCreated]";

        String api27 = "[onActivityCreated, " +
                "onActivityStarted, " +
                "onActivityResumed, " +
                "onActivityPaused, " +
                "onActivitySaveInstanceState, " +
                "onActivityStopped, " +
                "onActivityDestroyed, " +
                "onActivityCreated]";

        String expect;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
            expect = api27;
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P) {
            expect = api28;
        } else {
            expect = api29;
        }

        matchTextWithViewTag("ActivityCreateTest", expect);
    }

}
