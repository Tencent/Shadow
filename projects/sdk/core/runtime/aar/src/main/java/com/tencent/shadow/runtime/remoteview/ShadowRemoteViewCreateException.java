package com.tencent.shadow.runtime.remoteview;

/**
 * 跨插件apk创建View异常
 * Created by jaylanchen on 2018/12/7.
 */
public class ShadowRemoteViewCreateException extends Exception {
    public ShadowRemoteViewCreateException() {
    }

    public ShadowRemoteViewCreateException(String message) {
        super(message);
    }

    public ShadowRemoteViewCreateException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShadowRemoteViewCreateException(Throwable cause) {
        super(cause);
    }

}
