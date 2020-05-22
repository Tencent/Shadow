package com.tencent.shadow.test.cases.plugin_main;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Parcel;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;


@RunWith(AndroidJUnit4.class)
public class RegisterNullReceiverTest extends PluginMainAppTest {

    @Override
    protected Intent getLaunchIntent() {
        Intent pluginIntent = new Intent();
        String packageName = ApplicationProvider.getApplicationContext().getPackageName();
        pluginIntent.setClassName(
                packageName,
                "com.tencent.shadow.test.plugin.general_cases.lib.usecases.context.RegisterNullReceiverActivity"
        );
        return pluginIntent;
    }

    @Test
    public void testRegisterNullReceiver() {
        // 抄一段和RegisterNullReceiverActivity中构造数据一样的代码在Host中直接运行，
        // 得到string跟插件环境下运行的相同代码对比
        String string;
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent intent = ApplicationProvider.getApplicationContext().registerReceiver(null, intentFilter);

        if (intent != null) {
            Parcel parcel = Parcel.obtain();
            intent.writeToParcel(parcel, 0);
            byte[] byteArray = parcel.marshall();
            Assert.assertTrue(byteArray.length > 0);
            string = Arrays.toString(byteArray);
            parcel.recycle();
        } else {
            string = "intent == null";
        }

        matchTextWithViewTag("byteArray", string);
    }

}
