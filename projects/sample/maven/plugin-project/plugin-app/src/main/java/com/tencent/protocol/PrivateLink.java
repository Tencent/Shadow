package com.tencent.protocol;

import android.content.Context;
import android.os.Build;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;


public class PrivateLink {

    static {
        System.loadLibrary("tquic");
        System.loadLibrary("protocol");
    }

    public class CallResponse {
        public int requestId;
        public long ret;
        public String errMsg;
        public long code;
        public HashMap<String, String> header;
        public String data;

        @Override
        public String toString() {
            return "CallResponse{" +
                    "requestId=" + requestId +
                    ", ret=" + ret +
                    ", errMsg='" + errMsg + '\'' +
                    ", code=" + code +
                    ", header=" + header +
                    ", data='" + data + '\'' +
                    '}';
        }
    }

    class SyncCallBack implements ProtocolCallback {

        public final CountDownLatch countDownLatch = new CountDownLatch(1);
        private final CallResponse resp = new CallResponse();

        @Override
        public void response(long ret, String errMsg, long code, HashMap<String, String> header, String data) {

            resp.ret = ret;
            resp.code = code;
            resp.header = header;
            resp.data = data;
            resp.errMsg = errMsg;
            countDownLatch.countDown();
        }

        public CallResponse getResp() {
            return resp;
        }
    }

    private volatile static PrivateLink privateLink;

    private PrivateLink(Context context, String uin, String host, String gatewayId, String rsaKey, int port, int timeout) {

        InitGWCloud(context, uin, host, gatewayId, rsaKey, port, timeout);
    }

    public static PrivateLink getPrivateLink(Context context, String uin, String host, String gatewayId, String rsaKey, int port, int timeout) {

        if (privateLink == null) {
            synchronized (PrivateLink.class) {
                if (privateLink == null) {
                    privateLink = new PrivateLink(context, uin, host, gatewayId, rsaKey, port, timeout);
                }
            }
        }

        return privateLink;
    }

    public int CallContainer(String url, String method, HashMap<String, String> header, String data, ProtocolCallback callBack) {

        String[] headerParams;
        if (header != null && header.size() != 0) {
            headerParams = new String[header.size() * 2];
            int i = 0;
            for (Map.Entry<String, String> entry : header.entrySet()) {
                headerParams[2 * i] = entry.getKey();
                headerParams[2 * i + 1] = entry.getValue();
                i++;
            }
        } else {
            headerParams = new String[]{};
        }

        return CallGWCloud(url, method, headerParams, headerParams.length, data, callBack);

    }

    public CallResponse SyncCallContainer(String url, String method, HashMap<String, String> header, String data) {

        SyncCallBack callback = new SyncCallBack();
        int reqId = CallContainer(url, method, header, data, callback);

        try {
            callback.countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        CallResponse rsp = callback.getResp();
        rsp.requestId = reqId;

        return rsp;
    }

    public void CancelRequest(int requestId) {
        CancelCallGWCloud(requestId);
    }

    public void CloseConnect(){
        CloseGWConnect();
    }

    private native void InitGWCloud(Context context, String uin, String host, String gatewayId, String rsaKey, int port, int timeout);

    private native int CallGWCloud(String url, String method, String[] headerParams, int headerParamsLength, String data, ProtocolCallback callBack);

    private native void CancelCallGWCloud(int requestId);

    private native void CloseGWConnect();

    private static String getUUid() {
        return UUID.randomUUID().toString();
    }

    private static String getOSVersion() {
        return Build.VERSION.RELEASE;
    }

}
