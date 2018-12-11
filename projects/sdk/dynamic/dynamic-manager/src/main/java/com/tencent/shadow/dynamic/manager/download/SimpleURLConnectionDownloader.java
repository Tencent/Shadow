package com.tencent.shadow.dynamic.manager.download;


import com.tencent.shadow.core.host.common.MinFileUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

/**
 * {@link Downloader}接口的URLConnection版本的简单实现
 * <p>
 * 实现了边下载边校验的功能和反映下载进度的功能
 */
class SimpleURLConnectionDownloader implements Downloader {

    private final ExecutorService mExecutorService = Executors.newSingleThreadExecutor();

    @Override
    public Future<File> download(TargetDownloadInfo targetDownloadInfo, File outputFile, File tmpFile) {
        final AtomicLong count = new AtomicLong(0);
        long size = targetDownloadInfo.size;
        final double fileSize = size;
        DownloadTask downloadTask = new DownloadTask(targetDownloadInfo, outputFile, tmpFile, count);
        final Future<File> future = mExecutorService.submit(downloadTask);
        return future;
    }

    class DownloadTask implements Callable<File> {
        final TargetDownloadInfo targetDownloadInfo;
        final File target;
        final File tmpFile;
        AtomicLong count;

        DownloadTask(TargetDownloadInfo targetDownloadInfo, File target, File tmpFile, AtomicLong count) {
            this.targetDownloadInfo = targetDownloadInfo;
            this.target = target;
            this.count = count;
            this.tmpFile = tmpFile;
        }

        @Override
        public File call() throws Exception {
            HttpURLConnection conn = getHttpURLConnection();
            downloadFile(conn);
            return target;
        }

        void downloadFile(HttpURLConnection conn) throws Exception {
            if (tmpFile.exists() && !tmpFile.delete()) {
                throw new Exception("无法删除" + tmpFile.getAbsolutePath());
            }
            File dir = tmpFile.getParentFile();
            dir.mkdirs();
            if (!dir.isDirectory()) {
                throw new Exception("创建目录失败:" + dir.getAbsolutePath());
            }
            tmpFile.createNewFile();

            RandomAccessFile fos = null;
            MessageDigest messageDigest = null;
            try {
                if (!targetDownloadInfo.hash.isEmpty()) {
                    try {
                        messageDigest = MessageDigest.getInstance("MD5");
                    } catch (NoSuchAlgorithmException ignore) {
                    }
                }
                fos = new RandomAccessFile(tmpFile, "rw");

                byte[] bytes = new byte[4096];
                int read = 0;

                BufferedInputStream binaryreader = new BufferedInputStream(conn.getInputStream());

                while ((read = binaryreader.read(bytes)) > 0) {
                    fos.write(bytes, 0, read);

                    count.getAndAdd(read);
                    if (messageDigest != null) {
                        messageDigest.update(bytes, 0, read);
                    }

                    if (Thread.interrupted())
                        throw new Error("interrupted");
                }

                binaryreader.close();
            } finally {
                if (fos != null)
                    fos.close();
            }
            if (messageDigest != null) {
                byte[] digest = messageDigest.digest();
                StringBuilder sb = new StringBuilder(digest.length * 2);
                for (byte signedNumber : digest) {
                    int unsignedNumber = signedNumber & 0xFF;
                    sb.append(Integer.toHexString(unsignedNumber | 0x100).substring(1, 3));
                }
                String md5 = sb.toString();
                if (!md5.equalsIgnoreCase(targetDownloadInfo.hash)) {
                    throw new Error("MD5检验失败" + "expect==" + targetDownloadInfo.hash + " actual==" + md5);
                }
            }

            MinFileUtils.ensureParentDirExists(target);
            if (!tmpFile.renameTo(target)) {
                throw new Exception("重命名失败: " + tmpFile.getAbsolutePath() + "->" + target.getAbsolutePath());
            }
        }

        HttpURLConnection getHttpURLConnection() throws IOException {
            String urlString = targetDownloadInfo.url;
            URL url = new URL(urlString);

            URLConnection urlConnection = url.openConnection();

            if (!(urlConnection instanceof HttpURLConnection)) {
                throw new Error(urlString + "连接不是http(s)协议的");
            }
            HttpURLConnection conn = (HttpURLConnection) urlConnection;

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new Error("连接返回值不是" + HttpURLConnection.HTTP_OK
                        + ",而为" + conn.getResponseCode());
            }
            return conn;
        }
    }

}
