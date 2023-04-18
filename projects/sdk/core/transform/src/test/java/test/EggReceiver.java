package test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;

abstract class EggReceiver extends BroadcastReceiver {
    List<String> log;

    EggReceiver(List<String> log) {
        this.log = log;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        log.add("EggReceiver onReceive");
    }

    public static class FoxReceiver extends EggReceiver {
        FoxReceiver(List<String> log) {
            super(log);
        }
    }

}
