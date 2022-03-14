package com.tencent.shadow.core.runtime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BroadcastReceiverWrapper extends BroadcastReceiver {

    final private BroadcastReceiver mRealBroadcastReceiver;

    final private ShadowContext mShadowContext;

    public BroadcastReceiverWrapper(BroadcastReceiver broadcastReceiver, ShadowContext shadowContext) {
        mRealBroadcastReceiver = broadcastReceiver;
        mShadowContext = shadowContext;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mRealBroadcastReceiver.onReceive(mShadowContext, intent);
    }
}
