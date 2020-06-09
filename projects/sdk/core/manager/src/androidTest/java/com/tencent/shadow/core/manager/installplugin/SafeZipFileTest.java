package com.tencent.shadow.core.manager.installplugin;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

@RunWith(AndroidJUnit4.class)
public class SafeZipFileTest {

    private File testZipFile;
    private ZipOutputStream zipOutputStream;

    @Before
    public void setUp() throws Exception {
        testZipFile = File.createTempFile("SafeZipFileTest", ".zip");
        zipOutputStream = new ZipOutputStream(new FileOutputStream(testZipFile));
    }

    @After
    public void tearDown() throws Exception {
        zipOutputStream.close();
        boolean success = testZipFile.delete();
        if (!success) {
            throw new RuntimeException("删除临时文件失败");
        }
    }

    @Test
    public void testContainsManifest() throws IOException {
        //向测试zip中写入一个文件
        ZipOutputStream out = this.zipOutputStream;
        ZipEntry e = new ZipEntry("META-INF/MANIFEST.MF");
        out.putNextEntry(e);
        byte[] data = "测试内容".getBytes();
        out.write(data, 0, data.length);
        out.closeEntry();
        out.close();

        //测试用SafeZipFile类型遍历zip文件
        ZipFile zipFile
                = new SafeZipFile(testZipFile);
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        if (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            String entryName = entry.getName();
            Assert.assertTrue(entryName.length() > 0);
        }
        zipFile.close();
    }
}