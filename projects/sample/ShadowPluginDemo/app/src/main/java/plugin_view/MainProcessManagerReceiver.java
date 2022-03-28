package plugin_view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.leelu.constants.Constant;
import com.leelu.shadow.MyApplication;

/**
 * CreateDate: 2022/3/17 17:47
 * Author: 李露
 * Email: lilu2@haier.com
 * Version: 1.0
 * Description:
 */
public class MainProcessManagerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        MyApplication.getApp().getPluginManager()
                .enter(context, Constant.FROM_ID_LOAD_VIEW_TO_HOST, intent.getExtras(), null);
    }
}

