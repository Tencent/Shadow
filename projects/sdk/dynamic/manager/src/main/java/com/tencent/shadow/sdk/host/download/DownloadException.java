package com.tencent.shadow.sdk.host.download;

public class DownloadException extends Exception {

    public DownloadException(String s) {
        super(s);
    }

    public DownloadException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
