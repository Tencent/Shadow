package com.tencent.shadow.dynamic.host;

import android.app.Application;
import android.content.Intent;
import android.os.IBinder;

import java.util.HashMap;

public class MultiLoaderPluginProcessService extends BasePluginProcessService {

    static final ActivityHolder sActivityHolder = new ActivityHolder();
    private final MultiLoaderPpsBinder mPpsControllerBinder = new MultiLoaderPpsBinder(this);

    private HashMap<String, SingleLoaderPpsBinder> mPpsHashMap = new HashMap<>();

    public static Application.ActivityLifecycleCallbacks getActivityHolder() {
        return sActivityHolder;
    }

    public static MultiLoaderPpsController wrapBinder(IBinder ppsBinder) {
        return new MultiLoaderPpsController(ppsBinder);
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("onBind:" + this);
        }
        return mPpsControllerBinder;
    }

    void exit() {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("exit ");
        }
        MultiLoaderPluginProcessService.sActivityHolder.finishAll();
        System.exit(0);
        try {
            wait();
        } catch (InterruptedException ignored) {
        }
    }

    public IBinder getSingleLoaderPps(String uuid) {
        SingleLoaderPpsBinder ppsBinder = mPpsHashMap.get(uuid);
        if (ppsBinder == null) {
            ppsBinder = new SingleLoaderPpsBinder(new SingleLoaderImpl(this));
        }
        return ppsBinder;
    }
}
