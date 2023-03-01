package com.tencent.shadow.sample.plugin;

import android.app.Activity;
import android.os.Bundle;

import com.tencent.protocol.PrivateLink;

public class MainActivity extends Activity {
    /*腾讯微信网关*/
    private static String uin = "10000";
    private static String host = "privatelink-security-001.tencentcloudbase.com";
    private static String gatewayId = "kye-test-nat";
    private static String rsaKey = "-----BEGIN PUBLIC KEY-----\n" +
            "xSvfKS32XNfRODhrZMa6upJKR7pZzJ+GkYbpcV+nAWUjvaRMsuq1tBWkcO4HRYlj\n" +
            "Bb+bqydMZZKULng6UUOKzbXdo3FxYFetgQBIZPaF1uK5C5IeuBTKcN/eazjGTuUk\n" +
            "nQIDAQAB\n" +
            "-----END PUBLIC KEY-----";
    private static int port = 443;
    private static int timeout = 60;
    //微信网关PrivateLink 类
    private static PrivateLink privateLink1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化微信网关PrivateLink
        privateLink1 = PrivateLink.getPrivateLink(MainActivity.this, uin, host, gatewayId, rsaKey, port, timeout);
    }
}