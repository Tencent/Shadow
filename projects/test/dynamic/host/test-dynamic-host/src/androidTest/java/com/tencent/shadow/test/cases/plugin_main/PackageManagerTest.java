package com.tencent.shadow.test.cases.plugin_main;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;

import com.tencent.shadow.test.lib.test_manager.TestManager;

import org.junit.Test;

public class PackageManagerTest extends PluginMainAppTest {

    @Override
    protected Intent getLaunchIntent() {
        Intent pluginIntent = new Intent();
        String packageName = ApplicationProvider.getApplicationContext().getPackageName();
        pluginIntent.setClassName(
                packageName,
                "com.tencent.shadow.test.plugin.general_cases.lib.usecases.packagemanager.TestPackageManagerActivity"
        );
        return pluginIntent;
    }

    @Test
    public void testGetApplicationInfoClassName() {
        matchTextWithViewTag("getApplicationInfo/className", "com.tencent.shadow.test.plugin.general_cases.lib.gallery.TestApplication");
    }

    @Test
    public void testGetApplicationInfoNativeLibraryDir() {
        matchSubstringWithViewTag("getApplicationInfo/nativeLibraryDir", TestManager.uuid + "_lib");
    }

    @Test
    public void testGetApplicationInfoMetaData() {
        matchTextWithViewTag("getApplicationInfo/metaData", "test_value");
    }

    @Test
    public void testQueryContentProvidersName() {
        matchTextWithViewTag("queryContentProviders/size", "2");
        matchTextWithViewTag("queryContentProviders/name", "[android.support.v4.content.FileProvider, com.tencent.shadow.test.plugin.general_cases.lib.usecases.provider.TestProvider]");
    }
}
