package com.tencent.shadow.test.cases.manager;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;

import com.tencent.shadow.test.dynamic.host.UpdateManagerImplTestActivity;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

public class UpdateManagerImplTest {
    @Before
    public void launchActivity() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), UpdateManagerImplTestActivity.class);
        ActivityScenario.launch(intent);
    }

    @Test
    public void testImplName() {
        Espresso.onView(ViewMatchers.withTagValue(Matchers.<Object>is("ImplName")))
                .check(ViewAssertions.matches(ViewMatchers.withText(
                        "com.tencent.shadow.test.dynamic.manager.TestDynamicDumbManager" +
                                "/" +
                                "com.tencent.shadow.test.dynamic.manager.TestDynamicPluginManager"
                )));
    }
}
