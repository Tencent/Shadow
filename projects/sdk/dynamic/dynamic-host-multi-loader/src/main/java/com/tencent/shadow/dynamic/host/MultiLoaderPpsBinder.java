package com.tencent.shadow.dynamic.host;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;

public class MultiLoaderPpsBinder extends Binder {

    static final String DESCRIPTOR = MultiLoaderPpsBinder.class.getName();

    static final int TRANSACTION_CODE_NO_EXCEPTION = 0;
    static final int TRANSACTION_CODE_FAILED_EXCEPTION = 1;

    static final int TRANSACTION_exit = (FIRST_CALL_TRANSACTION + 1);
    static final int TRANSACTION_getSingleLoaderPps = (FIRST_CALL_TRANSACTION + 2);

    private final MultiLoaderPluginProcessService mPps;

    MultiLoaderPpsBinder(MultiLoaderPluginProcessService pps) {
        mPps = pps;
    }

    @Override
    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) {
        switch (code) {
            case INTERFACE_TRANSACTION: {
                reply.writeString(DESCRIPTOR);
                return true;
            }
            case TRANSACTION_exit: {
                data.enforceInterface(DESCRIPTOR);
                mPps.exit();
                reply.writeNoException();
                return true;
            }
            case TRANSACTION_getSingleLoaderPps: {
                data.enforceInterface(DESCRIPTOR);
                String uuid = data.readString();
                IBinder pps = mPps.getSingleLoaderPps(uuid);
                reply.writeNoException();
                reply.writeStrongBinder(pps);
                reply.writeNoException();
                return true;
            }
            default:
                return false;
        }
    }
}
