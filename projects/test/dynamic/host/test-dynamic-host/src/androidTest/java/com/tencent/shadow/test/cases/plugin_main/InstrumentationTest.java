package com.tencent.shadow.test.cases.plugin_main;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;

public class InstrumentationTest extends PluginMainAppTest {

    @Override
    protected Intent getLaunchIntent() {
        Intent pluginIntent = new Intent();
        String packageName = ApplicationProvider.getApplicationContext().getPackageName();
        pluginIntent.setClassName(
                packageName,
                "com.tencent.shadow.test.plugin.general_cases.lib.usecases.instrumentation.TestInstrumentationActivity"
        );
        return pluginIntent;
    }

    @Test
    public void testStaticMethod() {
        matchTextWithViewTag("newApplicationSuccess", Boolean.toString(true));
    }

    @Test
    public void testSubClassMemberMethod() {
        matchTextWithViewTag("callActivityOnDestroySuccess", Boolean.toString(true));
    }

}
