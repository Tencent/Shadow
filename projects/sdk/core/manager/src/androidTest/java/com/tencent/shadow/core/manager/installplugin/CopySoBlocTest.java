package com.tencent.shadow.core.manager.installplugin;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.tencent.shadow.core.common.LoggerFactory;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import common.AndroidLogLoggerFactory;

@RunWith(AndroidJUnit4.class)
public class CopySoBlocTest {

    private File apkFile = new File("/data/local/tmp/CopySoBlocTest.apk");
    private File soDir;
    private File copiedTagFile;

    @Before
    public void setUp() throws Exception {
        //这是一个手工测试用例，不适合自动化测试
        //测试前用adb push plugin.apk /data/local/tmp/CopySoBlocTest.apk先将被测插件复制到手机上
        //否则这个测试将跳过
        Assume.assumeTrue(apkFile.exists());

        LoggerFactory.setILoggerFactory(new AndroidLogLoggerFactory());

        System.out.println("apkFile文件存在，执行测试");

        Context targetContext = ApplicationProvider.getApplicationContext();
        soDir = new File(targetContext.getCacheDir(), "CopySoBlocTestSoDir");
        soDir.mkdirs();
        FileUtils.cleanDirectory(soDir);

        copiedTagFile = new File(targetContext.getCacheDir(), "CopySoBlocTestCopiedTagFile");
        copiedTagFile.delete();
        Assert.assertFalse(copiedTagFile.exists());
    }

    @After
    public void tearDown() throws Exception {
        if (!apkFile.exists()) return;

        FileUtils.deleteDirectory(soDir);
        copiedTagFile.delete();
    }

    @Test
    public void copySo() throws InstallPluginException {
        CopySoBloc.copySo(apkFile, soDir, copiedTagFile, "lib/armeabi-v7a/");
        Assert.assertTrue(copiedTagFile.exists());
        Assert.assertEquals(8, soDir.list().length);
    }
}