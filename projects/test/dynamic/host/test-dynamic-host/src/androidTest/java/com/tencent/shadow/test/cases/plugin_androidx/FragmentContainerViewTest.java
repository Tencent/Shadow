package com.tencent.shadow.test.cases.plugin_androidx;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class FragmentContainerViewTest extends PluginAndroidxAppTest {

    @Override
    protected Intent getLaunchIntent() {
        Intent pluginIntent = new Intent();
        String packageName = ApplicationProvider.getApplicationContext().getPackageName();
        pluginIntent.setClassName(
                packageName,
                "com.tencent.shadow.test.plugin.androidx_cases.lib.FragmentContainerViewTestActivity"
        );
        return pluginIntent;
    }

    @Test
    public void testFooSupportFragmentInflate() {
        matchTextWithViewTag("msg", "FooSupportFragment");
    }

}
