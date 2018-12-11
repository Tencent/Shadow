package com.tencent.shadow.dynamic.manager.download;


import com.tencent.shadow.core.host.common.annotation.API;

/**
 * 下载目标文件信息
 */
@API
public class TargetDownloadInfo {
    public TargetDownloadInfo(String url, String hash, long size) {
        this.url = url;
        this.hash = hash;
        this.size = size;
    }

    @API
    public final String url;

    @API
    public final String hash;

    @API
    public final long size;
}
