package com.tencent.shadow.core.runtime;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class ShadowFragmentSupport {

    public static ShadowActivity fragmentGetActivity(Fragment fragment) {
        return null;
    }

    public static Context fragmentGetContext(Fragment fragment) {
        return null;
    }

    public static Object fragmentGetHost(Fragment fragment) {
        return null;
    }

    public void fragmentStartActivity(Fragment fragment, Intent intent) {
    }

    public void fragmentStartActivity(Fragment fragment, Intent intent, Bundle options) {
    }

    public static void fragmentStartActivityForResult(Fragment fragment, Intent intent, int requestCode) {
    }

    public static void fragmentStartActivityForResult(Fragment fragment, Intent intent, int requestCode, Bundle options) {
    }

    public static Context toPluginContext(Context pluginContainerActivity) {
        return null;
    }

    public static Context toOriginalContext(Context pluginActivity) {
        return null;
    }
}
