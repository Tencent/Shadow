package com.tencent.shadow.test.cases.plugin_main;

import android.content.Intent;
import android.os.Build;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class PluginUseApacheClass extends PluginMainAppTest {

    @Override
    protected Intent getLaunchIntent() {
        Intent pluginIntent = new Intent();
        String packageName = ApplicationProvider.getApplicationContext().getPackageName();
        pluginIntent.setClassName(
                packageName,
                "com.tencent.shadow.test.plugin.general_cases.lib.usecases.classloader.UseApacheClassActivity"
        );
        return pluginIntent;
    }

    /**
     * 测试宿主代码中使用org.apache.http包的org.apache.http.conn.scheme.SchemeRegistry情况
     */
    @Test
    public void testHostUseSchemeRegistry() {
        ClassNotFoundException exception = null;
        Class<?> theClass = null;
        try {
            theClass = Class.forName("org.apache.http.conn.scheme.SchemeRegistry");
        } catch (ClassNotFoundException e) {
            exception = e;
        }

        if (Build.VERSION.SDK_INT < 28) {
            String classLoaderName = theClass.getClassLoader().getClass().getSimpleName();
            Assert.assertEquals("BootClassLoader", classLoaderName);
        } else {
            Assert.assertNotNull(exception);
        }
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
