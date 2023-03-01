package com.tencent.protocol;

import java.util.HashMap;

public interface ProtocolCallback {
    void response(long ret, String errMsg, long code, HashMap<String, String> header, String data);
}