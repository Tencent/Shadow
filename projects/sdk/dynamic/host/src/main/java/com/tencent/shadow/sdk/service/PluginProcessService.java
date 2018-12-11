package com.tencent.shadow.sdk.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.tencent.shadow.dynamic.host.IProcessServiceInterface;
import com.tencent.shadow.sdk.service.load.PluginLoaderServiceLoader;
import com.tencent.shadow.sdk.service.load.RunTimeLoader;


public class PluginProcessService extends Service {

    private final static String TAG = "PluginProcessService";

    private IBinder mLoaderBinder;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d(TAG, "onRebind");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    private final IProcessServiceInterface.Stub mBinder = new IProcessServiceInterface.Stub() {
        @Override
        public void loadRuntime(String uuid, String apkPath) throws RemoteException {
            RunTimeLoader.loadRunTime(uuid, apkPath);
        }

        @Override
        public IBinder loadPluginLoader(String uuid, String apkPath) throws RemoteException {
            if (mLoaderBinder == null) {
                mLoaderBinder = PluginLoaderServiceLoader.loadPluginLoaderService(PluginProcessService.this, uuid, apkPath);
            }
            return mLoaderBinder;
        }

    };

}
