package com.tencent.cubershi.mock_interface;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;


public class MockPendingIntent {

    public static PendingIntent getService(Context context, int requestCode,
                                            Intent intent,  int flags) {
        if(context instanceof MockContext && intent.getComponent() != null){
            MockContext mockContext = (MockContext)context;
            if(mockContext.getPendingIntentConverter() != null){
                context = mockContext.getPendingIntentConverter().convertPluginServiceIntent(intent).first;
                intent = mockContext.getPendingIntentConverter().convertPluginServiceIntent(intent).second;
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
        if(context instanceof MockContext && intent.getComponent() != null){
            MockContext mockContext = (MockContext)context;
            if(mockContext.getPendingIntentConverter() != null){
                context = mockContext.getPendingIntentConverter().convertPluginActivityIntent(intent).first;
                intent = mockContext.getPendingIntentConverter().convertPluginActivityIntent(intent).second;
            }
        }
        return PendingIntent.getActivity(context, requestCode, intent, flags, options);
    }


}
