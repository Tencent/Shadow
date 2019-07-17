package com.tencent.shadow.sample.host.lib;

import android.content.Context;
import android.util.Log;

/**
 * @author zhukun on 2019-07-13.
 */
public class HostCallbackProvider {
    private static HostCallbackProvider sInstance;
    private Callback mCallback;

    public static void init(Context mHostApplicationContext) {
        sInstance = new HostCallbackProvider(mHostApplicationContext);
    }

    public static HostCallbackProvider getInstance() {
        return sInstance;
    }

    final private Context mHostApplicationContext;

    private HostCallbackProvider(Context mHostApplicationContext) {
        this.mHostApplicationContext = mHostApplicationContext;
    }


    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    public Callback getCallback() {
        return this.mCallback;
    }

    public void call() {
        String result = this.mCallback.call("shadow");

        Log.i("test", result);
    }
}
