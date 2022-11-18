package test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;

/**
 * 直接继承
 * <p>
 * 需要修改，但不用管super调用。
 */
class AceReceiver extends BroadcastReceiver {
    List<String> log;

    AceReceiver(List<String> log) {
        this.log = log;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        log.add("AceReceiver onReceive");
    }
}

/**
 * 间接继承，不调用父类方法
 * <p>
 * 需要修改，和直接继承一样。
 */
class BarReceiver extends AceReceiver {
    List<String> log;

    BarReceiver(List<String> log) {
        super(log);
        this.log = log;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // do not call super
        log.add("BarReceiver onReceive");
    }
}

/**
 * 间接继承，调用父类方法
 * <p>
 * 需要修改，和直接继承一样。
 * <p>
 * 额外的，需要把super调用改到superOnReceive上。不能直接调用super的被修改后的方法，
 * 因为super可能是个系统类，我们不会修改它。所以让superOnReceive用原本的参数调用super原本的方法。
 */
class CatReceiver extends BarReceiver {
    List<String> log;

    CatReceiver(List<String> log) {
        super(log);
        this.log = log;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        log.add("CatReceiver onReceive enter");
        super.onReceive(context, intent);
        log.add("CatReceiver onReceive leave");
    }
}

/**
 * 间接继承，不覆盖方法
 * <p>
 * 这种不用修改，它不会自己处理onReceive收到的参数。
 */
class DogReceiver extends CatReceiver {
    List<String> log;

    DogReceiver(List<String> log) {
        super(log);
        this.log = log;
    }
}
