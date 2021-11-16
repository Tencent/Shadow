package com.tencent.shadow.test.plugin.general_cases.lib.usecases.context;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcel;
import android.view.ViewGroup;

import com.tencent.shadow.test.plugin.general_cases.lib.gallery.util.UiUtil;

import java.util.Arrays;

public class RegisterNullReceiverActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewGroup mItemViewGroup = UiUtil.setActivityContentView(this);

        String string;
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent intent = registerReceiver(null, intentFilter);

        if (intent != null) {
            Parcel parcel = Parcel.obtain();
            intent.writeToParcel(parcel, 0);
            byte[] byteArray = parcel.marshall();
            string = Arrays.toString(byteArray);
            parcel.recycle();
        } else {
            string = "intent == null";
        }


        mItemViewGroup.addView(
                UiUtil.makeItem(
                        this,
                        "byteArray",
                        "byteArray",
                        string
                )
        );
    }
}
