package com.tencent.shadow.core.runtime;

import android.content.Context;

public class ShadowInstrumentation {
    public void callActivityOnDestroy(ShadowActivity activity) {
    }

    static public ShadowApplication newShadowApplication(Class<?> clazz, Context context)
            throws InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        return null;
    }
}
