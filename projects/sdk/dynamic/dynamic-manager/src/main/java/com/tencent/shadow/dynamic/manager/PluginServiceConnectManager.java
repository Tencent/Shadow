package com.tencent.shadow.dynamic.manager;

import android.content.ServiceConnection;

import java.util.HashMap;
import java.util.Map;

public class PluginServiceConnectManager {


    private static Map<ServiceConnection, ServiceConnectionWrapper> sServiceConnections = new HashMap<>();

    public static synchronized ServiceConnectionWrapper getServiceConnectionWrapper(ServiceConnection serviceConnection) {
        ServiceConnectionWrapper serviceConnectionWrapper = sServiceConnections.get(serviceConnection);
        if (serviceConnectionWrapper == null) {
            serviceConnectionWrapper = new ServiceConnectionWrapper(serviceConnection);
        }
        return serviceConnectionWrapper;
    }

}
