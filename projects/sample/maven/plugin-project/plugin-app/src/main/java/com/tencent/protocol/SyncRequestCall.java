package com.tencent.protocol;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

public class SyncRequestCall {

    public class Response {
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
        private final Response resp = new Response();

        @Override
        public void response(long ret, String errMsg, long code, HashMap<String, String> header, String data) {
            resp.ret = ret;
            resp.code = code;
            resp.header = header;
            resp.data = data;
            resp.errMsg = errMsg;
            countDownLatch.countDown();
        }

        public Response getResp() {
            return resp;
        }
    }

    private boolean executed;
    private int requestId;
    private String url;
    private String method;
    private HashMap<String, String> header;
    private String data;
    private PrivateLink privateLink;

    public SyncRequestCall(PrivateLink link, String url, String method, HashMap<String, String> header, String data) {
        this.privateLink = link;
        this.url = url;
        this.method = method;
        this.header = header;
        this.data = data;
        this.executed = false;
        this.requestId = -1;
    }

    public Response execute() {
        synchronized (this) {
            if (executed) {
                throw new IllegalStateException("Already Executed");
            }
            executed = true;
        }
        SyncCallBack callback = new SyncCallBack();
        this.requestId = privateLink.CallContainer(url, method, header, data, callback);
        try {
            callback.countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Response rsp = callback.getResp();
        rsp.requestId = this.requestId;
        return rsp;
    }

    public void cancel() {
        synchronized (this) {
            if (this.requestId < 0 || !executed) {
                throw new IllegalStateException("Req Not Executed");
            }
            executed = false;
        }
        privateLink.CancelRequest(this.requestId);
    }
}
