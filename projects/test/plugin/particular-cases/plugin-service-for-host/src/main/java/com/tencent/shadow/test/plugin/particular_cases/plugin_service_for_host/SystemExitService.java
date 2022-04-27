package com.tencent.shadow.test.plugin.particular_cases.plugin_service_for_host;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

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

    @Override
    public boolean onUnbind(Intent intent) {
        long magic_number = intent.getLongExtra("magic_number", 0L);
        File outputFile = new File(getFilesDir(), "SystemExitService.onUnbind");
        try (BufferedWriter bufferedWriter
                     = new BufferedWriter(new FileWriter(outputFile, false))) {
            bufferedWriter.append(Long.toString(magic_number));
            bufferedWriter.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return super.onUnbind(intent);
    }
}
