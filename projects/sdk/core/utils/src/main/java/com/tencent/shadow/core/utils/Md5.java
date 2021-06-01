package com.tencent.shadow.core.utils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.security.MessageDigest;

public class Md5 {
    /**
     * 获取 文件的md5
     * 计算方式性能优化
     * 参考：https://juejin.im/post/583e2172128fe1006bf66bc8#heading-9
     */
    public static String md5File(File file) {
        MessageDigest messageDigest;
        RandomAccessFile randomAccessFile = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            randomAccessFile = new RandomAccessFile(file, "r");
            byte[] bytes = new byte[2 * 1024 * 1024];
            int len = 0;
            while ((len = randomAccessFile.read(bytes)) != -1) {
                messageDigest.update(bytes, 0, len);
            }
            BigInteger bigInt = new BigInteger(1, messageDigest.digest());
            StringBuilder md5 = new StringBuilder(bigInt.toString(16));
            while (md5.length() < 32) {
                md5.insert(0, "0");
            }
            return md5.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            closeQuietly(randomAccessFile);
        }
    }

    static void closeQuietly(final Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (final IOException ioe) {
            // ignore
        }
    }

}
