package com.tencent.shadow.test.plugin.particular_cases.plugin_service_for_host;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

@SuppressWarnings("NullableProblems")
public class SystemExitService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return new Binder() {
            @Override
            protected boolean onTransact(int code,
                                         Parcel data,
                                         Parcel reply,
                                         int flags) throws RemoteException {
                //随便发什么来都退出进程以便触发onServiceDisconnected
                System.exit(0);
                return super.onTransact(code, data, reply, flags);
            }
        };
    }
}
