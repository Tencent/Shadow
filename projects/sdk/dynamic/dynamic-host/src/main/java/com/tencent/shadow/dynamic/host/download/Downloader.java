package com.tencent.shadow.dynamic.host.download;


import com.tencent.shadow.core.host.API;

import java.io.File;
import java.util.concurrent.Future;

/**
 * 下载器
 */
@API
public interface Downloader {

    /**
     * 下载文件。如果{@link TargetDownloadInfo#hash}不为空，下载器应该对文件进行校验。
     *
     * @param targetDownloadInfo 下载目标信息
     * @param outputFile         文件保存位置
     * @param tmpFile            临时文件位置
     * @return 能够查询进度的ProgressFuture
     */
    @API
    Future<File> download(TargetDownloadInfo targetDownloadInfo, File outputFile, File tmpFile);
}
