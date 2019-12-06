package com.tencent.shadow.dynamic.host;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;

import static android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE;

public class MultiLoaderPpsBinder extends Binder {

    static final String DESCRIPTOR = MultiLoaderPpsBinder.class.getName();

    static final int TRANSACTION_CODE_NO_EXCEPTION = 0;
    static final int TRANSACTION_CODE_FAILED_EXCEPTION = 1;

    static final int TRANSACTION_loadRuntimeForPlugin = (FIRST_CALL_TRANSACTION);
    static final int TRANSACTION_loadPluginLoaderForPlugin = (FIRST_CALL_TRANSACTION + 1);
    static final int TRANSACTION_setUuidManagerForPlugin = (FIRST_CALL_TRANSACTION + 2);
    static final int TRANSACTION_getPpsStatusForPlugin = (FIRST_CALL_TRANSACTION + 3);
    static final int TRANSACTION_getPluginLoaderForPlugin = (FIRST_CALL_TRANSACTION + 4);
    static final int TRANSACTION_exit = (FIRST_CALL_TRANSACTION + 5);

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
            case TRANSACTION_loadRuntimeForPlugin: {
                data.enforceInterface(DESCRIPTOR);
                String _arg0, _arg1;
                _arg0 = data.readString();
                _arg1 = data.readString();
                try {
                    mPps.loadRuntimeForPlugin(_arg0, _arg1);
                    reply.writeInt(TRANSACTION_CODE_NO_EXCEPTION);
                } catch (FailedException e) {
                    reply.writeInt(TRANSACTION_CODE_FAILED_EXCEPTION);
                    e.writeToParcel(reply, 0);
                }
                return true;
            }
            case TRANSACTION_loadPluginLoaderForPlugin: {
                data.enforceInterface(DESCRIPTOR);
                String _arg0, _arg1;
                _arg0 = data.readString();
                _arg1 = data.readString();
                try {
                    mPps.loadPluginLoaderForPlugin(_arg0, _arg1);
                    reply.writeInt(TRANSACTION_CODE_NO_EXCEPTION);
                } catch (FailedException e) {
                    reply.writeInt(TRANSACTION_CODE_FAILED_EXCEPTION);
                    e.writeToParcel(reply, 0);
                }
                return true;
            }
            case TRANSACTION_setUuidManagerForPlugin: {
                data.enforceInterface(DESCRIPTOR);
                String _arg0 = data.readString();
                IBinder iBinder = data.readStrongBinder();
                UuidManager uuidManager = iBinder != null ? new BinderUuidManager(iBinder) : null;
                mPps.setUuidManagerForPlugin(_arg0, uuidManager);
                reply.writeNoException();
                return true;
            }
            case TRANSACTION_getPpsStatusForPlugin: {
                data.enforceInterface(DESCRIPTOR);
                String _arg0 = data.readString();
                PpsStatus ppsStatus = mPps.getPpsStatusForPlugin(_arg0);
                reply.writeNoException();
                ppsStatus.writeToParcel(reply, PARCELABLE_WRITE_RETURN_VALUE);
                return true;
            }
            case TRANSACTION_getPluginLoaderForPlugin: {
                data.enforceInterface(DESCRIPTOR);
                String _arg0 = data.readString();
                IBinder pluginLoader = mPps.getPluginLoaderForPlugin(_arg0);
                reply.writeNoException();
                reply.writeStrongBinder(pluginLoader);
                return true;
            }
            case TRANSACTION_exit: {
                data.enforceInterface(DESCRIPTOR);
                mPps.exit();
                reply.writeNoException();
                return true;
            }
            default:
                return false;
        }
    }
}
