package com.tencent.shadow.sample.common;

import android.app.Application;

public class ApplicationHelper {

    private static Application me;

    public static void init(Application application) {
        me = application;
    }

    public static Application getInstance() {
        return me;
    }
}