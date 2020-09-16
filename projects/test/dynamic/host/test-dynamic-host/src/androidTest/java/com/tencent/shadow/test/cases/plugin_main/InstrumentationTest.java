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

    @Test
    public void testNewApplicationSuccess1() {
        matchTextWithViewTag("newApplicationSuccess1", Boolean.toString(true));
    }

    @Test
    public void testNewShadowActivitySuccess() {
        matchTextWithViewTag("newShadowActivitySuccess", Boolean.toString(true));
    }

    @Test
    public void testCallApplicationOnCreateSuccess() {
        matchTextWithViewTag("callApplicationOnCreateSuccess", Boolean.toString(true));
    }

    @Test
    public void testCallActivityOnCreateSuccess() {
        matchTextWithViewTag("callActivityOnCreateSuccess", Boolean.toString(true));
    }

    @Test
    public void testCallActivityOnCreateSuccess1() {
        matchTextWithViewTag("callActivityOnCreateSuccess1", Boolean.toString(true));
    }

    @Test
    public void testExecStartActivitySuccess() {
        matchTextWithViewTag("execStartActivitySuccess", Boolean.toString(true));
    }

}
