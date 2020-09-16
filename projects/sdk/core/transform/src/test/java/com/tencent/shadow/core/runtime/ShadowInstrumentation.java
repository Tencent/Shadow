package com.tencent.shadow.core.runtime;

import android.app.Fragment;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

public class ShadowInstrumentation {
    public void callActivityOnDestroy(ShadowActivity activity) {
    }

    static public ShadowApplication newShadowApplication(Class<?> clazz, Context context)
            throws InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        return null;
    }

    public ShadowApplication newApplication(ClassLoader cl, String className, Context context)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return null;
    }

    public ShadowActivity newShadowActivity(ClassLoader cl, String className, Intent intent)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return (ShadowActivity) cl.loadClass(className).newInstance();
    }

    public void callApplicationOnCreate(ShadowApplication app) {
        app.onCreate();
    }

    public Instrumentation.ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token, ShadowActivity target, Intent intent, int requestCode) {
        return new Instrumentation.ActivityResult(requestCode, intent);
    }

    public Instrumentation.ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token, ShadowActivity target, Intent intent, int requestCode, Bundle options) {
        return new Instrumentation.ActivityResult(requestCode, intent);
    }

    public Instrumentation.ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token, Fragment target, Intent intent, int requestCode, Bundle options) {
        return new Instrumentation.ActivityResult(requestCode, intent);
    }

    public Instrumentation.ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token, String target, Intent intent, int requestCode, Bundle options) {
        return new Instrumentation.ActivityResult(requestCode, intent);
    }
}
