package com.tencent.shadow.core.common;

public final class ShadowLoggerFactory {

    volatile private static ILoggerFactory sILoggerFactory;

    public static void setILoggerFactory(ILoggerFactory loggerFactory) {
        if (sILoggerFactory != null) {
            throw new RuntimeException("不能重复初始化");
        }
        sILoggerFactory = loggerFactory;
    }

    public static ILogger getLogger(Class<?> clazz) {
        ILoggerFactory iLoggerFactory = getILoggerFactory();
        return iLoggerFactory.getLogger(clazz.getName());
    }

    public static ILoggerFactory getILoggerFactory() {
        if (sILoggerFactory == null) {
            throw new RuntimeException("没有找到 ILoggerFactory 实现，请先调用setILoggerFactory");
        }
        return sILoggerFactory;
    }
}
