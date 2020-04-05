package com.tencent.shadow.test.plugin.general_cases.lib.usecases.receiver;

import android.content.Context;
import android.content.Intent;

public class BroadCastHelper {


    private static Notify notify;


    public static void setNotify(Notify notify) {
        BroadCastHelper.notify = notify;
    }

    public static void notify(Intent intent, Context context) {
        notify.onReceiver(intent, context);
    }

    interface Notify {
        void onReceiver(Intent intent, Context context);
    }


}
