package com.tencent.shadow.core.utils;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class Md5Test {

    @Test(expected = RuntimeException.class)
    public void nullAsFile() {
        Md5.md5File(null);
    }

    @Test
    public void emptyFile() throws IOException {
        File tempFile = File.createTempFile("Md5Test", "emptyFile");
        try {
            String actual = Md5.md5File(tempFile);
            assertEquals("d41d8cd98f00b204e9800998ecf8427e", actual);
        } finally {
            FileUtils.delete(tempFile);
        }
    }

    @Test
    public void smallFile() throws IOException {
        File tempFile = File.createTempFile("Md5Test", "smallFile");
        try {
            byte[] bytes = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
            FileUtils.writeByteArrayToFile(tempFile, bytes);

            String actual = Md5.md5File(tempFile);
            assertEquals("1bdd36b0a024c90db383512607293692", actual);
        } finally {
            FileUtils.delete(tempFile);
        }
    }
}
