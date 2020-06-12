package com.tencent.shadow.test.cases.plugin_service;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 测试通过bindPluginService方式绑定插件Service时，
 * PluginServiceConnection收到回调是否正常。
 */
@RunWith(AndroidJUnit4.class)
public class PluginIntentServiceConnectionTest extends PluginServiceAppTest {
    private static final String ServiceClassName =
            "com.tencent.shadow.test.plugin.particular_cases.plugin_service_for_host.SystemExitIntentService";

    private String packageName;

    @Override
    protected Intent getLaunchIntent() {
        Intent pluginIntent = new Intent();
        packageName = ApplicationProvider.getApplicationContext().getPackageName();
        pluginIntent.setClassName(
                packageName,
                ServiceClassName
        );
        return pluginIntent;
    }

    @Test
    public void testOnServiceConnected() {
        Espresso.onView(ViewMatchers.withTagValue(Matchers.<Object>is("BIND_BUTTON_TAG"))).perform(ViewActions.click());
        matchTextWithViewTag("STATUS_VIEW_TAG", "onServiceConnected");
        matchTextWithViewTag("PACKAGE_VIEW_TAG", packageName);
        matchTextWithViewTag("CLASS_VIEW_TAG", ServiceClassName);
    }

    @Test
    public void onServiceDisconnected() {
        Espresso.onView(ViewMatchers.withTagValue(Matchers.<Object>is("BIND_BUTTON_TAG"))).perform(ViewActions.click());
        matchTextWithViewTag("STATUS_VIEW_TAG", "onServiceConnected");
        Espresso.onView(ViewMatchers.withTagValue(Matchers.<Object>is("STOP_BUTTON_TAG"))).perform(ViewActions.click());
        matchTextWithViewTag("STATUS_VIEW_TAG", "onServiceDisconnected");
        matchTextWithViewTag("PACKAGE_VIEW_TAG", packageName);
        matchTextWithViewTag("CLASS_VIEW_TAG", ServiceClassName);
    }
}
