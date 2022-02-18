package com.tencent.shadow.test.cases.plugin_main;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class ContextGetPackageCodePathTest extends PluginMainAppTest {

    @Override
    protected Intent getLaunchIntent() {
        Intent pluginIntent = new Intent();
        String packageName = ApplicationProvider.getApplicationContext().getPackageName();
        pluginIntent.setClassName(
                packageName,
                "com.tencent.shadow.test.plugin.general_cases.lib.usecases.context.ContextGetPackageCodePathTestActivity"
        );
        return pluginIntent;
    }

    @Test
    public void testGetPackageCodePath() {
        matchSubstringWithViewTag("PackageCodePath", "/plugin-debug.zip/test-plugin-general-cases-plugin-debug.apk");
    }

}
