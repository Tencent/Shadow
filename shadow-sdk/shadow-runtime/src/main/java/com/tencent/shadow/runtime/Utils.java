package com.tencent.shadow.runtime;

public class Utils {

    public static boolean shouldGetPluginPkgName() {
        Throwable throwable = new Throwable();
        StackTraceElement[] list = throwable.getStackTrace();

        for (int i = 0; i < list.length; i++) {
            if (list[i] != null) {
                String className = list[i].getClassName();
                if (className.startsWith("oicq.wlogin_sdk")) {
                    return true;
                }
            }
        }
        return false;
    }

}
