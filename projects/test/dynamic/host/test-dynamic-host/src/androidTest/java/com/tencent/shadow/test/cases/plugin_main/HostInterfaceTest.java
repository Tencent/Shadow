package com.tencent.shadow.test.cases.plugin_main;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;

import com.tencent.shadow.test.lib.plugin_use_host_code_lib.interfaces.HostTestInterface;

import org.hamcrest.Matchers;
import org.junit.Test;

public class HostInterfaceTest extends PluginMainAppTest {

    @Override
    protected Intent getLaunchIntent() {
        Intent pluginIntent = new Intent();
        String packageName = ApplicationProvider.getApplicationContext().getPackageName();
        pluginIntent.setClassName(
                packageName,
                "com.tencent.shadow.demo.usecases.interfaces.TestHostInterfaceActivity"
        );
        return pluginIntent;
    }

    @Test
    public void testHostInterfaceTest() {
        Espresso.onView(ViewMatchers.withTagValue(Matchers.<Object>is("button"))).perform(ViewActions.click());

        matchTextWithViewTag("text", HostTestInterface.getText());

        Espresso.onView(ViewMatchers.withTagValue(Matchers.<Object>is("button1"))).perform(ViewActions.click());

        matchTextWithViewTag("text", "ClassNotFound");
    }
}