package com.tencent.shadow.demo.usecases;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import androidx.test.espresso.IdlingRegistry;

public class BaseAndroidTestActivity extends Activity {

    protected SimpleIdlingResource mIdlingResource = new SimpleIdlingResource();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IdlingRegistry.getInstance().register(mIdlingResource);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IdlingRegistry.getInstance().unregister(mIdlingResource);
    }
}
