package com.tencent.shadow.test.plugin.androidx_cases.lib;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.CoreComponentFactory;

@SuppressLint("RestrictedApi")
@RequiresApi(api = Build.VERSION_CODES.P)
public class TestComponentFactory extends CoreComponentFactory {
    @NonNull
    @Override
    public Activity instantiateActivity(@NonNull ClassLoader cl, @NonNull String className, @Nullable Intent intent) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Activity activity = super.instantiateActivity(cl, className, intent);
        if (activity instanceof AppComponentFactoryTestActivity) {
            ((AppComponentFactoryTestActivity) activity).flag = true;
        }
        return activity;
    }
}
