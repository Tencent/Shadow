package com.tencent.shadow.dynamic.host;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

/**
 * 单个Manager的PPS抽象
 * <p>
 * Binder实现 {@link PpsBinder}
 * Proxy实现 {@link PpsController}
 * 实际服务 {@link PluginProcessService}
 */
public interface SinglePps extends IInterface {
    String DESCRIPTOR = SinglePps.class.getName();

    void setUuidManager(IBinder binder) throws RemoteException;

    IBinder getPluginLoader() throws RemoteException;

    PpsStatus getPpsStatus() throws RemoteException;

    void loadRuntime(String uuid) throws RemoteException, FailedException;

    void loadPluginLoader(String uuid) throws RemoteException, FailedException;
}
