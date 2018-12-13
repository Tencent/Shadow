package com.tencent.shadow.core.interface_.log;

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
            throw new RuntimeException("没有找到 ILoggerFactory 实现，请先调用setILoggerFactory");
        }
        return sILoggerFactory;
    }
}
