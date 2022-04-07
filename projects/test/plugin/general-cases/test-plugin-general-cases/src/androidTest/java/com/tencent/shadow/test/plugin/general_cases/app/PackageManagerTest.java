package com.tencent.shadow.test.plugin.general_cases.app;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class PackageManagerTest extends NormalAppTest {

    @Before
    public void launchActivity() {
        Intent intent = new Intent();
        String packageName = ApplicationProvider.getApplicationContext().getPackageName();
        intent.setClassName(
                packageName,
                "com.tencent.shadow.test.plugin.general_cases.lib.usecases.packagemanager.TestPackageManagerActivity"
        );
        ActivityScenario.launch(intent);
    }

    @Test
    public void testGetApplicationInfoClassName() {
        matchTextWithViewTag("getApplicationInfo/className", "com.tencent.shadow.test.plugin.general_cases.lib.gallery.TestApplication");
    }

    @Test
    public void testGetApplicationInfoNativeLibraryDir() {
        String nativeLibraryDir = ApplicationProvider.getApplicationContext().getApplicationInfo().nativeLibraryDir;
        matchTextWithViewTag("getApplicationInfo/nativeLibraryDir", nativeLibraryDir);
    }

    @Test
    public void testGetApplicationInfoMetaData() {
        matchTextWithViewTag("getApplicationInfo/metaData", "test_value");
    }

    @Test
    public void testGetActivityInfoName() {
        matchTextWithViewTag("getActivityInfo/name", "com.tencent.shadow.test.plugin.general_cases.lib.usecases.packagemanager.TestPackageManagerActivity");
    }

    @Test
    public void testGetActivityInfoPackageName() {
        matchTextWithViewTag("getActivityInfo/packageName", "com.tencent.shadow.test.hostapp");
    }

    @Test
    public void testGetPackageInfoVersionName() {
        matchTextWithViewTag("getPackageInfo/versionName", "2.0.12");
    }

    @Test
    public void testGetPackageInfoVersionCode() {
        matchTextWithViewTag("getPackageInfo/versionCode", "1");
    }

    @Test
    public void testQueryContentProvidersName() {
        matchTextWithViewTag("queryContentProviders/size", ">0");
    }

    @Test
    public void testResolveActivityByExplicitIntent() {
        matchTextWithViewTag("resolveActivity/explicit", "com.tencent.shadow.test.plugin.general_cases.lib.usecases.packagemanager.TestPackageManagerActivity");
    }
}
