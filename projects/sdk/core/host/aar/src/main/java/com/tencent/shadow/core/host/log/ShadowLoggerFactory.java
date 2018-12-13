package com.tencent.shadow.core.host.log;

public final class ShadowLoggerFactory {

    private static ILoggerFactory sILoggerFactory;

    public static void setILoggerFactory(ILoggerFactory loggerFactory) {
        sILoggerFactory = loggerFactory;
    }

    public static ILogger getLogger(String name) {
        ILoggerFactory iLoggerFactory = getILoggerFactory();
        return iLoggerFactory.getLogger(name);
    }

    public static ILoggerFactory getILoggerFactory() {
        if (sILoggerFactory == null) {
            sILoggerFactory = new SimpleLoggerFactory();
        }
        return sILoggerFactory;
    }
}
