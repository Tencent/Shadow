package com.tencent.shadow.core.runtime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BroadcastReceiverWapper extends BroadcastReceiver {

    private BroadcastReceiver mRealBroadcastReceiver;

    private ShadowContext mShadowContext;

    public BroadcastReceiverWapper(BroadcastReceiver broadcastReceiver, ShadowContext shadowContext) {
        mRealBroadcastReceiver = broadcastReceiver;
        mShadowContext = shadowContext;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mRealBroadcastReceiver.onReceive(mShadowContext, intent);
    }
}
