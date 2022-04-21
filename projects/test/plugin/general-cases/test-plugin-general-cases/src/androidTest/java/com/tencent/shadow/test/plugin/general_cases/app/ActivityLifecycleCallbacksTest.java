package com.tencent.shadow.test.plugin.general_cases.app;

import android.content.Intent;
import android.os.Build;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ActivityLifecycleCallbacksTest extends NormalAppTest {

    @Before
    public void launchActivity() {
        Intent intent = new Intent();
        String packageName = ApplicationProvider.getApplicationContext().getPackageName();
        intent.setClassName(
                packageName,
                "com.tencent.shadow.test.plugin.general_cases.lib.usecases.application.ActivityLifecycleCallbacksTestActivity"
        );
        ActivityScenario.launch(intent);
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

        String api16 = "[onActivityCreated, " +
                "onActivityStarted, " +
                "onActivityResumed, " +
                "onActivityPaused, " +
                "onActivitySaveInstanceState, " +
                "onActivityStopped, " +
                "onActivityDestroyed, " +
                "onActivityCreated]";

        String expect = Build.VERSION.SDK_INT < Build.VERSION_CODES.Q ? api28 : api29;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
            expect = api16;
        }

        matchTextWithViewTag("ActivityCreateTest", expect);
    }

}
