package com.tencent.shadow.test.cases.plugin_httpclient;

import android.content.Intent;
import android.os.Build;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 同plugin_main中的PluginUseApacheClass的区别是这个httpclient插件打包了httpclient
 */
@RunWith(AndroidJUnit4.class)
public class PluginUseApacheClass extends PluginHttpClientTest {

    @Override
    protected Intent getLaunchIntent() {
        Intent pluginIntent = new Intent();
        String packageName = ApplicationProvider.getApplicationContext().getPackageName();
        pluginIntent.setClassName(
                packageName,
                "com.tencent.shadow.test.plugin.particular_cases.httpclient.UseApacheClassActivity"
        );
        return pluginIntent;
    }

    /**
     * 测试插件代码中使用org.apache.http包的org.apache.http.conn.scheme.SchemeRegistry情况
     */
    @Test
    public void testPluginUseSchemeRegistry() {
        if (Build.VERSION.SDK_INT < 28) {
            matchTextWithViewTag("classLoaderName", "BootClassLoader");
        } else {
            matchTextWithViewTag("exceptionName", "ClassNotFoundException");
        }
    }
}
