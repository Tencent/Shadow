package com.tencent.shadow.demo.host;

import android.content.Context;

import com.tencent.shadow.core.loader.Reporter;
import com.tencent.shadow.core.loader.ShadowPluginLoader;
import com.tencent.shadow.core.loader.managers.ComponentManager;
import com.tencent.shadow.core.loader.managers.PluginBroadcastManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DemoPluginLoader extends ShadowPluginLoader {

    final private ComponentManager mCM = new DemoComponentManager();

    public DemoPluginLoader(@NotNull Context context) {
        super(context);
    }

    @Override
    public String getMAbi() {
        return "armeabi";
    }

    @Override
    public ComponentManager getComponentManager() {
        return mCM;
    }

    @Override
    public Reporter getMExceptionReporter() {
        return new Reporter() {
            @Override
            public void reportException(Exception e) {

            }

            @Override
            public void log(String s) {

            }
        };
    }

    @Override
    public PluginBroadcastManager getBusinessPluginReceiverManager(Context context) {
        return new PluginBroadcastManager() {
            @Override
            public List<BroadcastInfo> getBroadcastInfoList(String s) {
                List<BroadcastInfo> broadcastInfos = new ArrayList<>();
                broadcastInfos.add(new BroadcastInfo("com.tencent.shadow.demo.usecases.receiver.MyReceiver",new String[]{"com.tencent.test.action"}));
                return broadcastInfos;
            }
        };
    }
}
