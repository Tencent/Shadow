package com.tencent.shadow.demo.host;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;

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
        String hostDataDir = ApplicationProvider.getApplicationContext().getDataDir().getAbsolutePath();
        matchTextWithViewTag("GET_DATA_DIR", hostDataDir + "/ShadowPluginDataDir/demo");
    }
}
