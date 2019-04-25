package com.tencent.shadow.demo.host;

import android.content.Intent;
import android.content.SharedPreferences;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FilenameFilter;

import static android.content.Context.MODE_PRIVATE;

public class GetDataDirTest extends BaseTest {
    @Override
    Intent getLaunchIntent() {
        Intent pluginIntent = new Intent();
        String packageName = ApplicationProvider.getApplicationContext().getPackageName();
        pluginIntent.setClassName(
                packageName,
                "com.tencent.shadow.demo.usecases.datadir.TestDataDirActivity"
        );
        return pluginIntent;
    }

    @Test
    public void testGetDataDir() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            String hostDataDir = ApplicationProvider.getApplicationContext().getDataDir().getAbsolutePath();
            assertTextAndFileExist("GET_DATA_DIR", hostDataDir + "/ShadowPluginDataDir/demo");
        }
    }

    @Test
    public void testGetFilesDir() {
        String hostFilesDir = ApplicationProvider.getApplicationContext().getFilesDir().getAbsolutePath();
        assertTextAndFileExist("GET_FILES_DIR", hostFilesDir + "/ShadowPluginFilesDir/demo");
    }

    @Test
    public void testGetCacheDir() {
        String hostCacheDir = ApplicationProvider.getApplicationContext().getCacheDir().getAbsolutePath();
        assertTextAndFileExist("GET_CACHE_DIR", hostCacheDir + "/ShadowPluginCacheDir/demo");
    }

    @Test
    public void testGetDirTest() {
        String hostDirTest = ApplicationProvider.getApplicationContext().getDir("test", MODE_PRIVATE).getAbsolutePath();
        assertTextAndFileExist("GET_DIR_TEST", hostDirTest + "/ShadowPluginDir/demo");
    }

    @Test
    public void testGetDatabasePath() {
        String hostDatabasePath = ApplicationProvider.getApplicationContext().getDatabasePath("test").getParentFile().getAbsolutePath();
        assertTextAndFileExist("GET_DATABASE_PATH", hostDatabasePath + "/ShadowPluginDatabase_demo_test");
    }

    @Test
    public void testGetSharedPreferences() {
        final String testSharedPreferences = "testGetSharedPreferences";
        SharedPreferences sharedPreferences
                = ApplicationProvider.getApplicationContext().getSharedPreferences(testSharedPreferences, MODE_PRIVATE);
        boolean commit = sharedPreferences.edit().putString("test", "test").commit();
        if (!commit) {
            throw new RuntimeException("commit failed");
        }

        File sharedPrefs = new File(ApplicationProvider.getApplicationContext().getFilesDir().getParent(), "shared_prefs");
        File[] files = sharedPrefs.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.contains(testSharedPreferences)) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        if (files.length != 1) {
            throw new RuntimeException("匹配文件数量不对。");
        }

        String hostSPFilePath = files[0].getParentFile().getAbsolutePath();
        String expectPluginSPFilePath = hostSPFilePath + "/ShadowPlugin_demo_testSharedPreferences.xml";
        assertTextAndFileExist("GET_SHARED_PREF", expectPluginSPFilePath);
    }

    private void assertTextAndFileExist(String viewTag, String text) {
        matchTextWithViewTag(viewTag, text);
        Assert.assertTrue("文件应该存在", new File(text).exists());
    }
}
