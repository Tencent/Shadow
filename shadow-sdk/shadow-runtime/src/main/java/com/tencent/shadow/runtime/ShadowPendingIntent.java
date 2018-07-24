package com.tencent.shadow.runtime;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;


public class ShadowPendingIntent {

    public static PendingIntent getService(Context context, int requestCode,
                                            Intent intent,  int flags) {
        if (context instanceof ShadowContext && intent.getComponent() != null) {
            ShadowContext shadowContext = (ShadowContext) context;
            if (shadowContext.getPendingIntentConverter() != null) {
                context = shadowContext.getPendingIntentConverter().convertPluginServiceIntent(intent).first;
                intent = shadowContext.getPendingIntentConverter().convertPluginServiceIntent(intent).second;
            }
        }
        return PendingIntent.getService(context, requestCode, intent, flags);
    }

    public static PendingIntent getActivity(Context context, int requestCode,
                                            Intent intent, int flags) {
        return getActivity(context, requestCode, intent, flags, null);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static PendingIntent getActivity(Context context, int requestCode,
                                            Intent intent, int flags, Bundle options) {
        if (context instanceof ShadowContext && intent.getComponent() != null) {
            ShadowContext shadowContext = (ShadowContext) context;
            if(shadowContext.getPendingIntentConverter() != null){
                context = shadowContext.getPendingIntentConverter().convertPluginActivityIntent(intent).first;
                intent = shadowContext.getPendingIntentConverter().convertPluginActivityIntent(intent).second;
            }
        }
        return PendingIntent.getActivity(context, requestCode, intent, flags, options);
    }


}
