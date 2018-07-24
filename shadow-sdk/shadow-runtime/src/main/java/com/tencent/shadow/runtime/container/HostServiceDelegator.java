package com.tencent.shadow.runtime.container;

import android.content.Context;

/**
 * HostService作为委托者的接口。主要提供它的委托方法的super方法，
 * 以便Delegate可以通过这个接口调用到Service的super方法。
 *
 * @author cubershi
 */
public interface HostServiceDelegator {
    void superOnCreate();

    void superStopSelf();

    Context getApplicationContext();

    Context getBaseContext();
}
