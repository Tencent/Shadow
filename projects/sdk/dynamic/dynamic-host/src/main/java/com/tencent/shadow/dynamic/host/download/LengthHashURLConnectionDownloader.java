package com.tencent.shadow.dynamic.host.download;


import android.util.Log;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 根据文件长度是否不变的检查，来判断文件是否更新需要下载
 *
 * @author owenguo
 */
public class LengthHashURLConnectionDownloader extends SimpleURLConnectionDownloader {

    private final String TAG = "LengthCheckDownloader";

    private final ExecutorService mExecutorService = Executors.newSingleThreadExecutor();

    @Override
    public Future<File> download(TargetDownloadInfo targetDownloadInfo, File outputFile, File tmpFile) {
        final AtomicLong count = new AtomicLong(0);
        long size = targetDownloadInfo.size;
        final double fileSize = size;
        DownloadTask downloadTask = new LengthHashDownloadTask(targetDownloadInfo, outputFile, tmpFile, count);
        final Future<File> future = mExecutorService.submit(downloadTask);
        return future;
    }

    class LengthHashDownloadTask extends DownloadTask {

        public LengthHashDownloadTask(TargetDownloadInfo targetDownloadInfo, File target, File tmpFile, AtomicLong count) {
            super(targetDownloadInfo, target, tmpFile, count);
        }

        @Override
        public File call() throws Exception {
            long fileLength = target.length();
            HttpURLConnection conn = getHttpURLConnection();
            int connLength = conn.getContentLength();
            if (fileLength != 0 && fileLength == connLength) {
                Log.d(TAG, "下载的文件没有变化，不进行下载 fileLenght:" + fileLength);
            } else {
                downloadFile(conn);
            }
            return target;
        }
    }


}
