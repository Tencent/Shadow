package com.tencent.shadow.core.runtime;

import android.app.Activity;
import android.app.Instrumentation;

public class ShadowInstrumentation extends Instrumentation {

    public void callActivityOnDestroy(ShadowActivity activity) {
        Activity hostActivity = (Activity) activity.mHostActivityDelegator.getHostActivity();
        super.callActivityOnDestroy(hostActivity);
    }
}
